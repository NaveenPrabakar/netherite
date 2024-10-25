package com.cs309.websocket3.chat;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Random;

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

@Controller // this is needed for this to be an endpoint to springboot
@ServerEndpoint(value = "/chat/{username}") // this is Websocket URL
public class ChatSocket {

	private static MessageRepository msgRepo;
	private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
	private static Map<String, Session> usernameSessionMap = new Hashtable<>();
	private static String admin = null;
	private final Logger logger = LoggerFactory.getLogger(ChatSocket.class);
	private static List<String> questions = new ArrayList<>();
	private static List<String> answers = new ArrayList<>();
	private static int index = -1;



	@Autowired
	public void setMessageRepository(MessageRepository repo) {
		msgRepo = repo; // we are setting the static variable
	}

	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username) throws IOException {
		logger.info("Entered into Open");

		// store connecting user information
		sessionUsernameMap.put(session, username);
		usernameSessionMap.put(username, session);



		if (admin == null) {
			admin = username;
			sendMessageToParticularUser(username, "You are the admin.");
			questions.add("What is Java? A. OOP, B. Imperative, C. Logic");
			answers.add("A");

			questions.add("Which planet is known as the Red Planet? A. Venus, B. Mars, C. Jupiter");
			answers.add("B");

			questions.add("What is the largest ocean on Earth? A. Atlantic, B. Indian, C. Pacific");
			answers.add("C");

			questions.add("Who wrote '1984'? A. George Orwell, B. J.K. Rowling, C. Mark Twain");
			answers.add("A");
		}

		// Send chat history to the newly connected user
		sendMessageToParticularUser(username, getChatHistory());

		// broadcast that new user joined
		String message = "User: " + username + " has joined the chat";
		broadcast(message);
	}

	@OnMessage
	public void onMessage(Session session, String message) throws IOException {
		logger.info("Entered into Message: Got Message: " + message);
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
				String question = getRandomQuestion();
				broadcast("New Question: " + question);
				return;
			}
		}

		if (message.startsWith("!a")) {
			String answer = message.substring(2).trim();
			checkAnswer(username, answer);
			return;
		}

		// Direct message to a user using the format "@username <message>"
		if (message.startsWith("@")) {
			String destUsername = message.split(" ")[0].substring(1);
			sendMessageToParticularUser(destUsername, "[DM] " + username + ": " + message);
			sendMessageToParticularUser(username, "[DM] " + username + ": " + message);
		} else {
			// Broadcast to everyone
			broadcast(username + ": " + message);
		}

		// Save chat history to repository
		msgRepo.save(new Message(username, message));
	}

	@OnClose
	public void onClose(Session session) throws IOException {
		logger.info("Entered into Close");

		String username = sessionUsernameMap.get(session);
		sessionUsernameMap.remove(session);
		usernameSessionMap.remove(username);

		if (username.equals(admin)) {
			admin = null; // Reset admin if the admin leaves
		}

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
		logger.info("Entered into Error");
		throwable.printStackTrace();
	}

	private String getRandomQuestion() {
		if (questions.isEmpty()) {
			return "No questions available.";
		}
		Random rand = new Random();
		index = rand.nextInt(questions.size());
		return questions.get(index);
	}

	private void checkAnswer(String username, String answer) {
		String correctAnswer = answers.get(index);
		if (answer.equalsIgnoreCase(correctAnswer)) {
			broadcast(username + " got the correct answer!");
		} else {
			broadcast(username + " answered incorrectly.");
		}
	}

	private void sendMessageToParticularUser(String username, String message) {
		try {
			usernameSessionMap.get(username).getBasicRemote().sendText(message);
		} catch (IOException e) {
			logger.info("Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void broadcast(String message) {
		sessionUsernameMap.forEach((session, username) -> {
			try {
				session.getBasicRemote().sendText(message);
			} catch (IOException e) {
				logger.info("Exception: " + e.getMessage());
				e.printStackTrace();
			}
		});
	}

	private String getChatHistory() {
		List<Message> messages = msgRepo.findAll();
		StringBuilder sb = new StringBuilder();
		if (messages != null && !messages.isEmpty()) {
			for (Message message : messages) {
				sb.append(message.getUserName()).append(": ").append(message.getContent()).append("\n");
			}
		}
		return sb.toString();
	}
}