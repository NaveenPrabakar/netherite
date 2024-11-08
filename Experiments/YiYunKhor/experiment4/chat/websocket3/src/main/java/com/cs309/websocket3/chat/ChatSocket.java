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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;
import org.json.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import java.util.Date;

// this is needed for this to be an endpoint to springboot
@ServerEndpoint("/chat/{fileId}/{username}")  // this is Websocket url
@Component //make sures tht spring will scan this class
public class ChatSocket {

  // cannot autowire static directly (instead we do it by the below
  // msgRepo: A static reference to the MessageRepository, which is used to save and retrieve messages.
	private static MessageRepository msgRepo; 

	/*
   * Grabs the MessageRepository singleton from the Spring Application
   * Context.  This works because of the @Controller annotation on this
   * class and because the variable is declared as static.
   * There are other ways to set this. However, this approach is
   * easiest.
	 */
	@Autowired
	public void setMessageRepository(MessageRepository repo) {
		msgRepo = repo;  // we are setting the static variable
	}

	//store session information by fileId (group chat)
	//each fileId will have its own list of sessions and associated usernames.
	private static Map<String, Map<Session, String>> fileIdSessionMap = new Hashtable<>();
	private static Map<String, Map<String, Session>> fileIdUsernameSessionMap = new Hashtable<>();

	//Logger: An instance of the SLF4J Logger for logging messages throughout the code.
	private final Logger logger = LoggerFactory.getLogger(ChatSocket.class);

	//@OnOpen: Triggered when a user connects.
	//session is important because it passed to the websocket and it could send back message to client
	//This session is unique to each client connection and is essential for sending messages back to the
	//specific client who opened the connection.
	//Functionality:
	//Logs the connection.
	//Stores the username and session in the maps.
	//Sends the chat history to the new user.
	//Broadcasts a message to all users that someone has joined the chat.
	@OnOpen
	public void onOpen(Session session, @PathParam("fileId") String fileId, @PathParam("username") String username)
			throws IOException {
		//logger.info is a method used in Java logging frameworks, particularly with SLF4J
		//(Simple Logging Facade for Java) and Logback or Log4j as the underlying logging implementation.
		// It is used to log informational messages at the INFO leve
		//info method is used to log messages that provide general information
		//about the application's flow or state.
		logger.info("Entered into Open");

		// Ensure that the map for the fileId exists
		fileIdSessionMap.putIfAbsent(fileId, new Hashtable<>());
		fileIdUsernameSessionMap.putIfAbsent(fileId, new Hashtable<>());

		// Store user session and username by fileId
		fileIdSessionMap.get(fileId).put(session, username);
		fileIdUsernameSessionMap.get(fileId).put(username, session);

		// Send chat history to the newly connected user
		sendMessageToPArticularUser(fileId, username, getChatHistory(fileId), "sourceHistory");

		// Broadcast that new user joined the chat
//		String message = "User: " + username + " has Joined the Chat";
//		broadcast(fileId, message);
	}

	//@OnMessage: Triggered when a message is received.
	//Functionality:
	//Logs the received message.
	//Checks if the message is a direct message (starts with "@"). If so, it sends it to the specified user.
	//Otherwise, it broadcasts the message to all users.
	//Saves the message to the message repository.
	@OnMessage
	public void onMessage(Session session, @PathParam("fileId") String fileId, String message) throws IOException {
		logger.info("Entered into Message: Got Message: " + message);
		String username = fileIdSessionMap.get(fileId).get(session);

		if (message.startsWith("/AI: ")) {
			String prompt = message.substring(4);
			String aiResponse = getAIResponse(prompt);
			broadcast(fileId, "AI", aiResponse, "sourceCHAT");
		} else if (message.startsWith("@")) {
			String destUsername = message.split(" ")[0].substring(1);
			if (fileIdUsernameSessionMap.get(fileId).containsKey(destUsername)) {
				sendMessageToPArticularUser(fileId, destUsername, "[DM] " + username + ": " + message, "sourceCHAT");
			}
		} else {
			broadcast(fileId, username, message, "sourceCHAT");
		}

		msgRepo.save(new Message(username, message, new Date(), fileId));  // Ensure Message entity has a fileId field
	}

	//Triggered when a user disconnects from the WebSocket.
	//Parameters:
	//Session session: The session of the user disconnecting.
	//Functionality:
	//Logs the disconnection.
	//Removes the user’s session and username from the maps.
	//Broadcasts a message that the user has disconnected.

	@OnClose
	public void onClose(Session session, @PathParam("fileId") String fileId) throws IOException {
		logger.info("Entered into Close");

		String username = fileIdSessionMap.get(fileId).get(session);

		// Remove the user from the session map
		fileIdSessionMap.get(fileId).remove(session);
		fileIdUsernameSessionMap.get(fileId).remove(username);

		// Broadcast that the user disconnected
//		String message = username + " disconnected";
//		broadcast(fileId, message);
	}

	//onError:
	//Triggered when an error occurs in the WebSocket connection.
	//Parameters:
	//Session session: The session where the error occurred.
	//Throwable throwable: The error that occurred.
	//Functionality:
	//Logs the error for debugging purposes.
	@OnError
	public void onError(Session session, Throwable throwable) {
		// Do error handling here
		logger.info("Entered into Error");
		throwable.printStackTrace();
	}

	// make sure that the broadcast and sendMessageToPArticularUser methods now accept the fileId as a
	// parameter to ensure that messages are sent to the correct group.
	private void broadcast(String fileId, String username, String message, String source) {
		String jsonResponse = new JSONObject()
				.put("content", message)
				.put("username", username)
				.put("source", source)
				.toString();

		fileIdSessionMap.get(fileId).forEach((session, sessionUsername) -> { // Renamed 'username' to 'sessionUsername'
			try {
				session.getBasicRemote().sendText(jsonResponse);
			} catch (IOException e) {
				logger.info("Exception: " + e.getMessage());
				e.printStackTrace();
			}
		});
	}

	private void sendMessageToPArticularUser(String fileId, String username, String message, String source) {
		String jsonResponse = new JSONObject()
				.put("content", message)
				.put("username", username)
				.put("source", source)
				.toString();
		try {
			fileIdUsernameSessionMap.get(fileId).get(username).getBasicRemote().sendText(jsonResponse);
		} catch (IOException e) {
			logger.info("Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	//getChatHistory:
	//Retrieves the chat history from the message repository.
	//Functionality:
	//Gets all messages, formats them as a string, and returns it.
  // Gets the Chat history from the repository
	private String getChatHistory(String fileId) {
		List<Message> messages = msgRepo.findByFileId(fileId);
		JSONArray historyArray = new JSONArray();

		if (messages != null && !messages.isEmpty()) {
			for (Message message : messages) {
				JSONObject messageJson = new JSONObject()
						.put("content", message.getContent())
						.put("username", message.getSender())
						.put("source", "sourceCHAT");
				historyArray.put(messageJson);
			}
		}
		return historyArray.toString();

	}

	// Method to interact with the AI service
	private String getAIResponse(String prompt) {
		String openaiApiUrl = "https://api.openai.com/v1/chat/completions"; // Endpoint for chat-based models
		String apiKey = "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"; // Replace with your OpenAI API key

		// Initialize RestTemplate
		RestTemplate restTemplate = new RestTemplate();

		// Set up headers
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + apiKey);
		headers.set("Content-Type", "application/json");

		// Create the request body
		JSONObject requestBody = new JSONObject();
		requestBody.put("model", "gpt-3.5-turbo-0125"); // Using GPT-3.5 Turbo model
		requestBody.put("messages", new JSONArray()
				.put(new JSONObject().put("role", "system").put("content", "You are a helpful assistant."))
				.put(new JSONObject().put("role", "user").put("content", prompt))
		);

		requestBody.put("max_tokens", 50); // Set max tokens for the response
		requestBody.put("temperature", 0.7); // Adjust creativity level

		// Create an HTTP entity with headers and body
		HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

		// Make the POST request
		try {
			ResponseEntity<String> response = restTemplate.exchange(
					openaiApiUrl,
					HttpMethod.POST,
					entity,
					String.class
			);

			// Extract the response body
			JSONObject responseBody = new JSONObject(response.getBody());
			String generatedText = responseBody.getJSONArray("choices")
					.getJSONObject(0)
					.getJSONObject("message")
					.getString("content");

			return generatedText.trim(); // Return the trimmed response text

		} catch (Exception e) {
			e.printStackTrace();
			return "Error calling OpenAI API: " + e.getMessage();
		}
	}
} // end of Class
