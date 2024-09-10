package coms309.people;

import java.util.Random;


public class hangman{

    private String[] wordbank = {"Software", "Computer Science", "Java", "Python"};

    private String word = "";

    private int incorrect = 0;

    private String person;
    private String education;



    public hangman(String person, String education){
        Random r = new Random();
        int num = r.nextInt(wordbank.length);
        word = wordbank[num];

        this.person = person;
        this.education = education;
    }

    public String picture(){

        String s = "";
        for(int i = 0; i < word.length(); i++){
            s += "*";
        }
        return s;
    }

    public String info(){
        return "Name: " + person + "\nEducation Level: " + education;
    }

    public String name(){
        return person;
    }




}