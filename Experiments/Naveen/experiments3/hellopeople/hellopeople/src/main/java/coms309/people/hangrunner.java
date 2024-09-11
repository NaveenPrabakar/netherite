package coms309.people;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.HashMap;
import java.util.Set;

@RestController
public class hangrunner {

    private HashMap<String, hangman> players = new HashMap<>();

    // Gets a list of all players playing the game
    @GetMapping("/StartGame")
    public Set<String> personinfo(){
        return players.keySet();
    }

    // Adds player to the lobby
    @PostMapping("/StartGame")
    public String createGame(@RequestBody hangman h){
        players.put(h.name(), h);
        return "Player " + h.name() + " has been added to the lobby.";
    }

    // Guess a letter for a specific player
    @PostMapping("/GuessLetter/{playerName}")
    public String guessLetter(@PathVariable String playerName, @RequestBody String letter) {
        hangman player = players.get(playerName);
        if (player == null) {
            return "Player not found!";
        }
        if (letter.length() != 1) {
            return "Please guess one letter at a time.";
        }
        char guessedLetter = letter.charAt(0);
        return player.guess(guessedLetter);
    }

    @DeleteMapping("/EndGame/{playerName}")
    public String endGame(@PathVariable String playerName) {
        hangman player = players.remove(playerName);
        if (player == null) {
            return "Player " + playerName + " not found!";
        }
        return "Player " + playerName + " has been removed from the game.";
    }



}
