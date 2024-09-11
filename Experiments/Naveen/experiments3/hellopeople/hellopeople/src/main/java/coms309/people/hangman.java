package coms309.people;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class hangman {

    private String[] wordbank = {"Software", "Computer Science", "Java", "Python"};
    private String word;
    private Set<Character> correctGuesses = new HashSet<>();
    private Set<Character> incorrectGuesses = new HashSet<>();
    private int incorrect;
    private String person;
    private String education;

    public hangman(String person, String education) {
        Random r = new Random();
        int num = r.nextInt(wordbank.length);
        word = wordbank[num];

        this.person = person;
        this.education = education;
        this.incorrect = 0;
    }

    public String picture() {
        String s = "";
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (correctGuesses.contains(c)) {
                s += c;
            } else {
                s += "*";
            }
        }
        return s;
    }

    public String guess(char letter) {
        if (word.indexOf(letter) != -1) {
            correctGuesses.add(letter);
            if (picture().equals(word)) {
                return "Congratulations! You've guessed the word: " + word;
            } else {
                return "Correct guess! Current word: " + picture();
            }
        } else {
            incorrectGuesses.add(letter);
            incorrect++;
            if (incorrect >= 6) {
                return "Game Over! You've made too many incorrect guesses. The word was: " + word;
            } else {
                return "Incorrect guess! Try again. Current word: " + picture();
            }
        }
    }

    public String name() {
        return person;
    }
}
