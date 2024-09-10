package coms309.people;

public class Bank {

    private String fullname;
    private int debit;
    private int credit;

    public Bank() {
    }


    public Bank(String fullname, int debit, int credit) {
        this.fullname = fullname;
        this.debit = debit;
        this.credit = credit;
    }


    public String getFullname() {
        return fullname;
    }


    public void setFullname(String fullname) {
        this.fullname = fullname;
    }


    public int getDebit() {
        return debit;
    }


    public void setDebit(int debit) {
        this.debit = debit;
    }


    public int getCredit() {
        return credit;
    }


    public void setCredit(int credit) {
        this.credit = credit;
    }

    public String account() {
        return "Credit: " + credit + "\nDebit: " + debit;
    }
}
