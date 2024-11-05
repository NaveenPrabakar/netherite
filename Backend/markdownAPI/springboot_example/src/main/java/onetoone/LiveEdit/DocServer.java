package onetoone.LiveEdit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Map;

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

@ServerEndpoint("/document/{owner}/{fileName}")
@Component
public class DocServer {

    private static Map<Session, String> sessionFileMap = new Hashtable<>();
    private static final Logger logger = LoggerFactory.getLogger(DocServer.class);
    private static final Path location = Paths.get("root");

    /**
     * The method updates which people are currently joining the document to edit
     *
     * @param session,  the current session of the document
     * @param fileName, the name of the file that's being edited
     * @throws IOException
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("fileName") String fileName) throws IOException {
        logger.info("[onOpen] File: " + fileName + " connected.");
        sessionFileMap.put(session, fileName);
    }

    /**
     * The updates to the document everytime a user types
     *
     * @param session
     * @param content
     * @throws IOException
     */
    @OnMessage
    public void onMessage(Session session, String content) throws IOException {
        String fileName = sessionFileMap.get(session);
        logger.info("[onMessage] File: " + fileName + " Content: " + content);

        // Update the document with the new content
        Path filePath = location.resolve(fileName);
        Files.write(filePath, content.getBytes());

        // Broadcast the updated content to other connected users
        broadcast(fileName + " updated: " + content);
    }

    /**
     * Updating the session of a user leaves
     *
     * @param session
     * @throws IOException
     */
    @OnClose
    public void onClose(Session session) throws IOException {
        String fileName = sessionFileMap.get(session);
        logger.info("[onClose] File: " + fileName + " disconnected.");
        sessionFileMap.remove(session);
    }

    /**
     * Error message to show any errors
     * @param session
     * @param throwable
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("[onError] Error: " + throwable.getMessage());
    }

    /**
     * broadcast class to display message to all users
     * @param message
     */
    private void broadcast(String message) {
        sessionFileMap.keySet().forEach(session -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                logger.error("[Broadcast Exception] " + e.getMessage());
            }
        });
    }
}

