package onetoone.Mail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import onetoone.LiveEdit.DocServer;
import onetoone.signupAPI.signEntity;
import onetoone.signupAPI.signRepository;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import onetoone.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import onetoone.*;
import jakarta.persistence.Entity;

@ServerEndpoint("/Mail")
@Component
public class LiveMail {

    private static Set<Session> sessionFileMap = new HashSet<>();
    private static HashMap<String, Session> map = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(DocServer.class);

    public static JsonRepository j;
    public static signRepository s;

    private static String apiEndpoint = "http://coms-3090-068.class.las.iastate.edu:8080/share/new";


    @Autowired
    public void setFileRepository(JsonRepository repo, signRepository s) {
        j = repo;
        this.s = s;
    }


    @OnOpen
    public void onOpen(Session session) throws IOException {
        logger.info("[onOpen] Main Server");
        sessionFileMap.add(session);


    }

    @OnMessage
    public void onMessage(Session session, String json) throws IOException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);

        System.out.println(json);




        String type = jsonNode.get("type").asText();

        if(type.equals("share")){
            String fromUser = jsonNode.get("fromUser").asText();
            String toUser = jsonNode.get("toUser").asText();

            //String response = callShareApi(apiEndpoint,fromUser, toUs);

            signEntity signs = s.findByEmail(toUser);
            String filesystem = j.getSystem(signs.getId());
            Session se = map.get(toUser);
            broadcast(filesystem, se);
        }
        else{

            map.put(jsonNode.get("email").asText(), session);
            System.out.print(jsonNode.get("email"));

        }

        // Call the `share` API with parsed values
        //String response = callShareApi(apiEndpoint,fromUser, toUser, fileName);
    }



        private String callShareApi(String apiUrl, String fromUser, String toUser, String docName) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "?fromUser=" + fromUser + "&toUser=" + toUser + "&docName=" + docName))
                .POST(HttpRequest.BodyPublishers.noBody()) // POST with no body since parameters are in the query string
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        logger.info("[onClose]disconnected.");
        sessionFileMap.remove(session);

    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("[onError] Error: " + throwable.getMessage());
    }

    private void broadcast(String message, Session skipSession) {
        map.keySet().forEach(key -> {
            Session session = map.get(key);
            if (session != null && session.equals(skipSession)) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    logger.error("[Broadcast Exception] Failed to send message to session: " + key, e);
                }
            }
        });
    }


}
