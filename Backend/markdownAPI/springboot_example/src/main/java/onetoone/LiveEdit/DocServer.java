package onetoone.LiveEdit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import onetoone.FileRepository;
import onetoone.FileEntity;

@ServerEndpoint("/document/{fileName}")
@Component
public class DocServer {

    // Static reference to the FileRepository instance
    public static FileRepository f;


    private static Map<Session, String> sessionFileMap = new Hashtable<>();


    private static BlockingQueue<MessageTask> messageQueue = new LinkedBlockingQueue<>();


    private static final ExecutorService executor = Executors.newSingleThreadExecutor();


    private static final Logger logger = LoggerFactory.getLogger(DocServer.class);

    private static final Path location = Paths.get("root");

    //the thread to execute cursor position
    static {
        // Start processing tasks in the queue
        executor.execute(() -> {
            while (true) {
                try {
                    MessageTask task = messageQueue.take();  // Retrieve and remove task from the queue
                    task.process();
                } catch (InterruptedException e) {
                    logger.error("[Queue Processing Interrupted] " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    @Autowired
    public void setFileRepository(FileRepository repo) {
        f = repo;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("fileName") String fileName) throws IOException {
        logger.info("[onOpen] File: " + fileName + " connected.");
        sessionFileMap.put(session, fileName);
    }

    @OnMessage
    public void onMessage(Session session, String content) {
        // Queue the task for processing
        messageQueue.add(new MessageTask(session, content));
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        String fileName = sessionFileMap.get(session);
        logger.info("[onClose] File: " + fileName + " disconnected.");
        sessionFileMap.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("[onError] Error: " + throwable.getMessage());
    }

    private void broadcast(String message, Session skipSession) {
        sessionFileMap.keySet().forEach(session -> {
            if (session != skipSession) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    logger.error("[Broadcast Exception] " + e.getMessage());
                }
            }
        });
    }

    /**
     * Internal class for processing messages in sequence.
     */
    private class MessageTask {
        private final Session session;
        private final String content;

        public MessageTask(Session session, String content) {
            this.session = session;
            this.content = content;
        }

        public void process() {
            String fileName = sessionFileMap.get(session);
            logger.info("[onMessage] File: " + fileName + " Content: " + content);

            try {
                // Fetch the document entity
                Long l = Long.parseLong(fileName);
                Optional<FileEntity> fileOptional = f.findById(l);
                FileEntity fileEntity = fileOptional.orElse(null);
                if (fileEntity == null) {
                    logger.error("[File Not Found] ID: " + l);
                    return;
                }

                // Update the file with new content
                Path filePath = location.resolve(fileEntity.getName());
                Files.write(filePath, content.getBytes());

                //need to cursor calc

                // Prepare the JSON payload
                String jsonResponse = "{ \"source\": \"live edit\", \"content\": \"" + content + "\", \"cursor\": [{ \"user\": \"" + session.getId() + "\", \"position\": /* calculated position */ }] }";

                // Broadcast updated content to other connected clients
                broadcast(jsonResponse, session);
            } catch (Exception e) {
                logger.error("[Message Processing Error] " + e.getMessage());
            }
        }
    }
}
