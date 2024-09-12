package coms309.people;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class hangman {

    private String[] wordbank = {"software", "computer science", "java", "python"};
    private String word;
    private Set<Character> correctGuesses = new HashSet<>();
    private Set<Character> incorrectGuesses = new HashSet<>();
    private String[][] a = new String[6][6];
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

        create();
    }

    private void create() {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {

                a[i][j] = "*";
            }
        }
        // creates the "hang"
        a[5][0] = "";
        a[5][1] = "";
        a[5][2] = "";
        a[5][3] = "";
        a[5][4] = "";
        a[5][5] = "";

        a[0][1] = "|";
        a[1][1] = "|";
        a[2][1] = "|";
        a[3][1] = "|";
        a[4][1] = "|";
        a[5][1] = "|";

        a[0][2] = "";
        a[0][3] = "";
        a[1][3] = "|";
    }

    public void hang(){
        if (incorrect == 6) {

            a[2][3] = "O";
        }

        else if (incorrect == 5) {
            a[3][3] = "|";
        }

        else if (incorrect == 4) {

            a[3][2] = "/";
        }

        else if (incorrect == 3) {

            a[3][4] = "\\";
        }

        else if (incorrect == 2) {

            a[4][2] = "/";

        }

        else if (incorrect == 1) {

            a[4][4] = "\\";
        }
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
                return "Correct guess! Current word: " + picture() + "\n" + board();
            }
        } else {
            incorrectGuesses.add(letter);
            incorrect++;
            hang();
            if (incorrect >= 6) {
                return "Game Over! You've made too many incorrect guesses. The word was: " + word + "\n" + board();
            } else {
                return "Incorrect guess! Try again. Current word: " + picture() + "\n" + board();
            }
        }
    }

    public void reset(){
        Random r = new Random();
        int num = r.nextInt(wordbank.length);
        word = wordbank[num];
        this.incorrect = 0;
        create();

        incorrectGuesses.clear();
        correctGuesses.clear();
    }

    public String name() {
        return person;
    }


    public String board() {

        String build = "";

        for (int i = 0; i < a.length; i++) {

            build += "\n";

            for (int j = 0; j < a.length; j++) {

                if (!a[i][j].equals("*")) {

                    build += a[i][j];

                }

                else {

                    build += " ";
                }
            }
        }

        return build;

    }
}
