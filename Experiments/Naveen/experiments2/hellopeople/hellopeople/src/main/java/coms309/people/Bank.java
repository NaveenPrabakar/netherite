package coms309.people;

import java.util.*;

public class Bank {

    private String fullname;
    private int credit;

    private List<String> history = new ArrayList<>();

    public Bank() {
    }

    public Bank(String fullname, int credit) {
        this.fullname = fullname;
        this.credit = credit;

        history.add("Account created with intial deposit: " + credit);
    }


    //gets name
    public String getFullname() {
        return fullname;
    }

   //sets name
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }


    //gets credit
    public int getCredit() {
        return credit;
    }

    //sets credit
    public void setCredit(int cre) {
        this.credit += cre;
        history.add("Deposited: " + cre);
    }

    //withdraws
    public void minusCredit(int minus){

        this.credit -= minus;
        history.add("Withdraw: " + minus);
    }

    //gets account info
    public String account() {
        return "Credit: " + credit ;
    }

    public String history(){
        String s = "";

        for(int i = 0; i < history.size(); i++){
            s += history.get(i);
            s += "\n";
        }

        return s;
    }
}
