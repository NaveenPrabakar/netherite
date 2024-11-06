package onetoone.LiveEdit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import onetoone.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This class represents a WebSocket server endpoint for collaborative document editing.
 * It listens to messages from clients, updates the document, and broadcasts the changes to other connected clients.
 */
@ServerEndpoint("/document/{fileName}")
@Component
public class DocServer {

    // Static reference to the FileRepository instance
    public static FileRepository f;

    // A map that stores sessions and their corresponding file names
    private static Map<Session, String> sessionFileMap = new Hashtable<>();

    // Logger for logging important events
    private static final Logger logger = LoggerFactory.getLogger(DocServer.class);

    // The location where documents are stored
    private static final Path location = Paths.get("root");

    /**
     * Sets the FileRepository instance. This is used to interact with the database and manage documents.
     *
     * @param repo The FileRepository to be set
     */
    @Autowired
    public void setFileRepository(FileRepository repo) {
        f = repo;  // we are setting the static variable
    }

    /**
     * This method is called when a client opens a connection to the WebSocket server.
     * It logs the connection and associates the session with a document file name.
     *
     * @param session The WebSocket session of the client connecting
     * @param fileName The name of the file being accessed by the client
     * @throws IOException If an I/O error occurs while handling the session
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("fileName") String fileName) throws IOException {
        logger.info("[onOpen] File: " + fileName + " connected.");
        sessionFileMap.put(session, fileName);
    }

    /**
     * This method is called when a client sends a message (e.g., updating the document content).
     * It updates the file with the new content and broadcasts the update to other connected clients.
     *
     * @param session The WebSocket session of the client sending the message
     * @param content The content to be written to the document
     * @throws IOException If an I/O error occurs while writing the content to the file
     */
    @OnMessage
    public void onMessage(Session session, String content) throws IOException {
        String fileName = sessionFileMap.get(session);
        logger.info("[onMessage] File: " + fileName + " Content: " + content);

        // Convert the file name to a long value and fetch the document from the repository
        Long l = Long.parseLong(fileName);
        Optional<FileEntity> allOptional = f.findById(l);
        FileEntity all = allOptional.orElse(null);

        // Update the document with the new content
        Path filePath = location.resolve(all.getName());
        Files.write(filePath, content.getBytes());

        // Broadcast the updated content to other connected clients
        broadcast(content, session);
    }

    /**
     * This method is called when a client closes the WebSocket connection.
     * It logs the disconnection and removes the session from the session map.
     *
     * @param session The WebSocket session of the client disconnecting
     * @throws IOException If an I/O error occurs while handling the session closure
     */
    @OnClose
    public void onClose(Session session) throws IOException {
        String fileName = sessionFileMap.get(session);
        logger.info("[onClose] File: " + fileName + " disconnected.");
        sessionFileMap.remove(session);
    }

    /**
     * This method is called when an error occurs in a WebSocket session.
     * It logs the error message.
     *
     * @param session The WebSocket session where the error occurred
     * @param throwable The exception or error that occurred
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("[onError] Error: " + throwable.getMessage());
    }

    /**
     * Broadcasts a message to all other connected clients, except the client sending the message.
     *
     * @param message The message to broadcast to other clients
     * @param skipSession The session of the client who should not receive the broadcasted message
     */
    private void broadcast(String message, Session skipSession) {
        sessionFileMap.keySet().forEach(session -> {
            if (session != skipSession) {  // Skip the session passed as a parameter
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    logger.error("[Broadcast Exception] " + e.getMessage());
                }
            }
        });
    }
}


