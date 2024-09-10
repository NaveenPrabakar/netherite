package coms309.people;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.Set;

public class hangrunner{

    private HashMap<String, hangman> players = new HashMap<>();


    //gets a list of all players, playing the game
    @GetMapping("/StartGame")
    public Set<String> personinfo(){
        return players.keySet();
    }

    //Adds player to the lobby
    @PostMapping("/StartGame")
    public String createGame(@RequestBody hangman h){
        players.put(h.name(), h);
        return "Player " + h.name() + " has been added to lobby.";
    }
}