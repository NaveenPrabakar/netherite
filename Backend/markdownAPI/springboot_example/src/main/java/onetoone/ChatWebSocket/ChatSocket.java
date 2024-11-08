package onetoone.ChatWebSocket;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.json.JSONObject;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

@ServerEndpoint("/chat/{fileId}/{username}")
@Component
public class ChatSocket {

	private static MessageRepository msgRepo; // static reference to MessageRepository

	@Autowired
	public void setMessageRepository(MessageRepository repo) {
		msgRepo = repo;
	}

	private static Map<String, Map<Session, String>> fileIdSessionMap = new Hashtable<>();
	private static Map<String, Map<String, Session>> fileIdUsernameSessionMap = new Hashtable<>();

	private final Logger logger = LoggerFactory.getLogger(ChatSocket.class);

	@OnOpen
	public void onOpen(Session session, @PathParam("fileId") String fileId, @PathParam("username") String username) throws IOException {
		logger.info("User {} connected to fileId {}", username, fileId);

		fileIdSessionMap.putIfAbsent(fileId, new Hashtable<>());
		fileIdUsernameSessionMap.putIfAbsent(fileId, new Hashtable<>());

		fileIdSessionMap.get(fileId).put(session, username);
		fileIdUsernameSessionMap.get(fileId).put(username, session);

		String chatHistory = getChatHistory(fileId);
		broadcast(fileId, "System", chatHistory, "sourceHISTORY");
	}

	@OnMessage
	public void onMessage(Session session, @PathParam("fileId") String fileId, String message) throws IOException {
		logger.info("Received message: {} from user: {} in fileId: {}", message, session.getId(), fileId);

		String username = fileIdSessionMap.get(fileId).get(session);
		String escapedMessage = escapeSpecialCharacters(message);

		if (escapedMessage.startsWith("/AI:")) {
			String prompt = escapedMessage.substring(4);
			String aiResponse = getAIResponse(prompt);
			broadcast(fileId, "AI", aiResponse, "sourceAI");
		} else if (escapedMessage.startsWith("@")) {
			String destUsername = escapedMessage.split(" ")[0].substring(1);
			if (fileIdUsernameSessionMap.get(fileId).containsKey(destUsername)) {
				sendMessageToParticularUser(fileId, destUsername, "[DM] " + username + ": " + escapedMessage, "sourceLIVE");
			}
		} else {
			broadcast(fileId, username, escapedMessage, "sourceLIVE");
		}

		msgRepo.save(new Message(username, message, new Date(), fileId));
	}

	@OnClose
	public void onClose(Session session, @PathParam("fileId") String fileId) throws IOException {
		logger.info("User disconnected from fileId: {}", fileId);

		String username = fileIdSessionMap.get(fileId).get(session);
		fileIdSessionMap.get(fileId).remove(session);
		fileIdUsernameSessionMap.get(fileId).remove(username);
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		logger.error("Error occurred in session: {}", session.getId(), throwable);
	}

	private void broadcast(String fileId, String username, String message, String source) {
		String jsonResponse = new JSONObject()
				.put("content", message)
				.put("username", username)
				.put("source", source)
				.toString();

		fileIdSessionMap.get(fileId).forEach((session, sessionUsername) -> {
			try {
				session.getBasicRemote().sendText(jsonResponse);
			} catch (IOException e) {
				logger.error("Error sending message to session {}: {}", session.getId(), e.getMessage());
			}
		});
	}

	private void sendMessageToParticularUser(String fileId, String username, String message, String source) {
		String jsonResponse = new JSONObject()
				.put("content", message)
				.put("username", username)
				.put("source", source)
				.toString();
		try {
			fileIdUsernameSessionMap.get(fileId).get(username).getBasicRemote().sendText(jsonResponse);
		} catch (IOException e) {
			logger.error("Error sending direct message to {}: {}", username, e.getMessage());
		}
	}

	private String getChatHistory(String fileId) {
		List<Message> messages = msgRepo.findByFileId(fileId);
		JSONArray historyArray = new JSONArray();

		if (messages != null && !messages.isEmpty()) {
			for (Message message : messages) {
				JSONObject messageJson = new JSONObject()
						.put("content", message.getContent())
						.put("username", message.getSender())
						.put("source", "sourceHISTORY");
				historyArray.put(messageJson);
			}
		}
		return historyArray.toString();
	}

	private String getAIResponse(String prompt) {
		String openaiApiUrl = "https://api.openai.com/v1/chat/completions";
		String apiKey = "your-api-key";

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + apiKey);
		headers.set("Content-Type", "application/json");

		JSONObject requestBody = new JSONObject();
		requestBody.put("model", "gpt-3.5-turbo-0125");
		requestBody.put("messages", new JSONArray()
				.put(new JSONObject().put("role", "system").put("content", "You are a helpful assistant."))
				.put(new JSONObject().put("role", "user").put("content", prompt))
		);

		requestBody.put("max_tokens", 50);
		requestBody.put("temperature", 0.7);

		HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(openaiApiUrl, HttpMethod.POST, entity, String.class);
			JSONObject responseBody = new JSONObject(response.getBody());
			return responseBody.getJSONArray("choices")
					.getJSONObject(0)
					.getJSONObject("message")
					.getString("content").trim();
		} catch (Exception e) {
			logger.error("Error calling OpenAI API: {}", e.getMessage());
			return "Error calling OpenAI API: " + e.getMessage();
		}
	}

	private String escapeSpecialCharacters(String message) {
		return message.replace("\\", "/\\")
				.replace("{", "/{")
				.replace("}", "/}")
				.replace("[", "/[")
				.replace("]", "/]")
				.replace(",", "/,");
	}
}
