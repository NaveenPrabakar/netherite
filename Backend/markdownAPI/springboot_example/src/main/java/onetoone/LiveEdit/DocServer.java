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

    public static FileRepository f;
    private static Map<Session, String> sessionFileMap = new Hashtable<>();
    private static final Logger logger = LoggerFactory.getLogger(DocServer.class);
    private static final Path location = Paths.get("root");

    // Queue to store incoming document changes in sequential order
    private static final BlockingQueue<Runnable> editQueue = new LinkedBlockingQueue<>();

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
        editQueue.offer(() -> {
            try {
                String fileName = sessionFileMap.get(session);
                logger.info("[onMessage] File: " + fileName + " Content: " + content);

                Long l = Long.parseLong(fileName);
                Optional<FileEntity> allOptional = f.findById(l);
                FileEntity all = allOptional.orElse(null);

                Path filePath = location.resolve(all.getName());
                String previousContent = Files.readString(filePath);
                Files.write(filePath, content.getBytes());

                // Calculate the updated cursor position (add the cursor position)
                int updatedCursorPos = getCorrectCursorLocation(previousContent, content, 0);

                // Broadcast the change with the updated cursor position
                String jsonMessage = String.format(
                        "{ \"source\": \"live edit\", \"content\": \"%s\", \"cursor\": {\"user\": %d}}",
                        content, updatedCursorPos
                );
                broadcast(jsonMessage, session);

            } catch (Exception e) {
                logger.error("[Queue Processing Error] " + e.getMessage());
            }
        });
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
     * Calculates the correct cursor location after an edit based on the before and after content,
     * and the current cursor position of the user.
     *
     * @param before Original content before the edit
     * @param after Content after the edit
     * @param cursorPos Initial cursor position of the user before the edit
     * @return Updated cursor position after the edit
     */
    public int getCorrectCursorLocation(String before, String after, int cursorPos) {
        int lenBefore = before.length();
        int lenAfter = after.length();
        int minLen = Math.min(lenBefore, lenAfter);
        int diffIndex = minLen;

        if (lenBefore != lenAfter) {
            for (int i = 0; i < minLen; i++) {
                if (before.charAt(i) != after.charAt(i)) {
                    diffIndex = i;
                    break;
                }
            }
            int lenChanged = lenAfter - lenBefore;
            if (lenChanged > 0) {
                if (diffIndex < cursorPos) {
                    return cursorPos + lenChanged;
                }
            } else if (lenChanged < 0) {
                if (diffIndex <= cursorPos) {
                    return cursorPos + lenChanged;
                }
            }
        }
        return cursorPos;
    }

    // Thread to process the edit queue for sequential processing
    private static final Thread editProcessingThread = new Thread(() -> {
        while (true) {
            try {
                editQueue.take().run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    });

    static {
        editProcessingThread.start();
    }
}
