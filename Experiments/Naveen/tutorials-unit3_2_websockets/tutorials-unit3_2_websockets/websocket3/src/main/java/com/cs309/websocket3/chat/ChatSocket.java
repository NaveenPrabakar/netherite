package com.cs309.websocket3.chat;

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
import org.springframework.stereotype.Controller;
import java.util.*;

@Controller      // this is needed for this to be an endpoint to springboot
@ServerEndpoint(value = "/chat/{username}")  // this is Websocket url
public class ChatSocket {

  // cannot autowire static directly (instead we do it by the below
  // method
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

	// Store all socket session and their corresponding username.
	private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
	private static Map<String, Session> usernameSessionMap = new Hashtable<>();

	private static String admin = null;

	private final Logger logger = LoggerFactory.getLogger(ChatSocket.class);

	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username) 
      throws IOException {

		logger.info("Entered into Open");

    // store connecting user information
		sessionUsernameMap.put(session, username);
		usernameSessionMap.put(username, session);

		if(admin == null){
			admin = username;
			sendMessageToPArticularUser(username, "You are the admin.");
		}

		//Send chat history to the newly connected user
		sendMessageToPArticularUser(username, getChatHistory());
		
    // broadcast that new user joined
		String message = "User:" + username + " has Joined the Chat";
		broadcast(message);
	}


	@OnMessage
	public void onMessage(Session session, String message) throws IOException {

		// Handle new messages
		logger.info("Entered into Message: Got Message:" + message);
		String username = sessionUsernameMap.get(session);

		if (username.equals(admin)) {
			if (message.startsWith("!remove")) {
				String[] parts = message.split(" ");
				if (parts.length > 1) {
					String userToRemove = parts[1];
					removeUser(userToRemove);
				}
				return;
			} else if (message.equals("!question")) {
				String question = getQuestion();
				broadcast("New Question: " + question);
				return;
			} else if (message.equals("!next")) {
				//displayLeaderboard();
				return;
			}
		}

		if (message.startsWith("!a")) {
			String answer = message.substring(3).trim();
			checkAnswer(username, answer);
			return;
		}

    // Direct message to a user using the format "@username <message>"
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


	@OnClose
	public void onClose(Session session) throws IOException {
		logger.info("Entered into Close");

    // remove the user connection information
		String username = sessionUsernameMap.get(session);
		sessionUsernameMap.remove(session);
		usernameSessionMap.remove(username);

		if (username.equals(admin)) {
			admin = null; // Reset admin if the admin leaves
		}


		// broadcase that the user disconnected
		String message = username + " disconnected";
		broadcast(message);
	}

	private void removeUser(String username) throws IOException {
		Session session = usernameSessionMap.get(username);
		if (session != null) {
			session.close();
			sessionUsernameMap.remove(session);
			usernameSessionMap.remove(username);
			broadcast("User " + username + " has been removed by admin.");
		}
	}


	@OnError
	public void onError(Session session, Throwable throwable) {
		// Do error handling here
		logger.info("Entered into Error");
		throwable.printStackTrace();
	}

	private String getQuestion() {
		// Fetch a random question from the repository
		List<String> questions = new ArrayList<>();
		if (questions.isEmpty()) {
			return "No questions available.";
		}
		int index = (int) (Math.random() * questions.size());
		return questions.get(index);
	}

	private void checkAnswer(String username, String answer) {
		// Assume correctAnswer is fetched from the current question context
		String correctAnswer = "some answer"; // Replace with actual answer fetching logic
		if (answer.equalsIgnoreCase(correctAnswer)) {
			//leaderboard.put(username, leaderboard.getOrDefault(username, 0) + 1);
			broadcast(username + " got the correct answer!");
		} else {
			broadcast(username + " answered incorrectly.");
		}
	}


	private void sendMessageToPArticularUser(String username, String message) {
		try {
			usernameSessionMap.get(username).getBasicRemote().sendText(message);
		} 
    catch (IOException e) {
			logger.info("Exception: " + e.getMessage().toString());
			e.printStackTrace();
		}
	}


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

}
