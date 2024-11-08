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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Controller      // this is needed for this to be an endpoint to springboot
@ServerEndpoint(value = "/chat/{username}")  // this is Websocket url
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

	//Store all socket session and their corresponding username.
	//These mappings make it easy to locate a session based on a username or retrieve
	//the username associated with a session.
	private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
	private static Map<String, Session> usernameSessionMap = new Hashtable<>();

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
	public void onOpen(Session session, @PathParam("username") String username) 
      throws IOException {

		//logger.info is a method used in Java logging frameworks, particularly with SLF4J
		//(Simple Logging Facade for Java) and Logback or Log4j as the underlying logging implementation.
		// It is used to log informational messages at the INFO leve
		//info method is used to log messages that provide general information
		//about the application's flow or state.
		logger.info("Entered into Open");

    // store connecting user information
		sessionUsernameMap.put(session, username);
		usernameSessionMap.put(username, session);

		//Send chat history to the newly connected user
		//This line sends the complete chat history to the new user by calling sendMessageToPArticularUser,
		//ensuring they see past messages.
		sendMessageToPArticularUser(username, getChatHistory());
		
    // broadcast that new user joined
		String message = "User:" + username + " has Joined the Chat";
		broadcast(message);
	}

	//@OnMessage: Triggered when a message is received.
	//Functionality:
	//Logs the received message.
	//Checks if the message is a direct message (starts with "@"). If so, it sends it to the specified user.
	//Otherwise, it broadcasts the message to all users.
	//Saves the message to the message repository.
	@OnMessage
	public void onMessage(Session session, String message) throws IOException {

		// Handle new messages
		logger.info("Entered into Message: Got Message:" + message);
		String username = sessionUsernameMap.get(session);

		// Check if message is a username change command
		if (message.startsWith("!changeusername ")) {
			String newUsername = message.split(" ")[1];
			changeUsername(session, username, newUsername);
			return;
		}

    // Direct message to a user using the format "@username <message>"
		//message.startsWith("@")
		//This method checks if the message string begins with the character "@".
		//If it does, it indicates that the sender is trying to send a direct message to another user.
		//message.split(" "): This method splits the message into an array of strings using a space (" ") as the delimiter. For example, if the message is "@john Hello!", it would be split into an array: ["@john", "Hello!"].
		//[0]: This accesses the first element of the resulting array, which is expected to be the username (e.g., "@john").
		//substring(1): This method returns a substring of the username starting from index 1, effectively removing the "@" character. For example, substring(1) on "@john" would return "john".
		if (message.startsWith("@")) {
			String destUsername = message.split(" ")[0].substring(1); 

      // send the message to the sender and receiver
			sendMessageToPArticularUser(destUsername, "[DM] " + username + ": " + message);
			sendMessageToPArticularUser(username, "[DM] " + username + ": " + message);

		} 
    else { // broadcast
			broadcast(username + ": " + message);
		}

		// Saving chat history to repository
		msgRepo.save(new Message(username, message));
	}

	//Triggered when a user disconnects from the WebSocket.
	//Parameters:
	//Session session: The session of the user disconnecting.
	//Functionality:
	//Logs the disconnection.
	//Removes the user’s session and username from the maps.
	//Broadcasts a message that the user has disconnected.

	@OnClose
	public void onClose(Session session) throws IOException {
		logger.info("Entered into Close");

    // remove the user connection information
		String username = sessionUsernameMap.get(session);
		sessionUsernameMap.remove(session);
		usernameSessionMap.remove(username);

    // broadcase that the user disconnected
		String message = username + " disconnected";
		broadcast(message);
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

	//sendMessageToPArticularUser:
	//Sends a direct message to a specific user.
	//Parameters:
	//String username: The recipient's username.
	//String message: The message to send.
	//Functionality:
	//Retrieves the session associated with the username and sends the message.
	//Handles any IOException that may occur while sending.
	//This sendMessageToPArticularUser method is used to send a private (direct)
	// message to a specific user in the chat by their username.
	private void sendMessageToPArticularUser(String username, String message) {
		try {

			//getBasicRemote() obtains a RemoteEndpoint.Basic instance, which allows
			//sending messages synchronously to the client associated with this session.

			//sendText(message) sends the specified message as a text message to the client.
			usernameSessionMap.get(username).getBasicRemote().sendText(message);
		}

		//Error Handling with IOException
    catch (IOException e) {

			//If an exception occurs, it's logged using logger.info and the stack trace is printed,
			// which helps in diagnosing what went wrong.
			logger.info("Exception: " + e.getMessage().toString());
			e.printStackTrace();
		}
	}
	//broadcast:
	//Sends a message to all connected users.
	//Parameters:
	//String message: The message to broadcast.
	//Functionality:
	//Iterates over all sessions and sends the message to each.

	private void broadcast(String message) {
		sessionUsernameMap.forEach((session, username) -> {
			try {
				session.getBasicRemote().sendText(message);
			} 
      catch (IOException e) {
				logger.info("Exception: " + e.getMessage().toString());
				e.printStackTrace();
			}

		});

	}
	
	//getChatHistory:
	//Retrieves the chat history from the message repository.
	//Functionality:
	//Gets all messages, formats them as a string, and returns it.
  // Gets the Chat history from the repository
	private String getChatHistory() {
		List<Message> messages = msgRepo.findAll();
    
    // convert the list to a string
		StringBuilder sb = new StringBuilder();
		if(messages != null && messages.size() != 0) {
			for (Message message : messages) {
				sb.append(message.getUserName() + ": " + message.getContent() + "\n");
			}
		}
		return sb.toString();
	}

	//helper method
	private void changeUsername(Session session, String oldUsername, String newUsername) throws IOException {
		// Check if the new username is already taken
		if (usernameSessionMap.containsKey(newUsername)) {
			sendMessageToPArticularUser(oldUsername, "Username '" + newUsername + "' is already taken. Please choose another one.");
			return;
		}

		// Update the username in the mappings
		sessionUsernameMap.put(session, newUsername);
		usernameSessionMap.remove(oldUsername);
		usernameSessionMap.put(newUsername, session);

		// Notify all users about the username change
		String message = oldUsername + " changed their username to " + newUsername;
		broadcast(message);
	}

} // end of Class
