package coms309.people;

public class Bank {

    private String fullname;
    private int credit;

    public Bank() {
    }

    public Bank(String fullname, int credit) {
        this.fullname = fullname;
        this.credit = credit;
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
    }

    //withdraws
    public void minusCredit(int minus){
        this.credit -= minus;
    }

    //gets account info
    public String account() {
        return "Credit: " + credit ;
    }
}
