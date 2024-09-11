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

@RestController
public class BankController {

    private HashMap<String, Bank> accounts = new HashMap<>();

    @GetMapping("/accounts")
    public Set<String> getAccountNames() {
        return accounts.keySet(); // Returns list of account names
    }

    @GetMapping("/accounts/{name}")
    public String getAccountInfo(@PathVariable String name) {
        Bank account = accounts.get(name);
        if (account == null) { //Account hasn't been added yet
            return "Account not found";
        }
        return account.account();
    }

    @PostMapping("/accounts")
    public String createAccount(@RequestBody Bank account) {

        accounts.put(account.getFullname(), account);
        return "Account: " + account.getFullname() + " has been added";

    }

    @PutMapping("/deposit/{name}")
    public String deposit(@PathVariable String name, @RequestBody int cre){
        Bank account = accounts.get(name);

        if(account == null){
            return "Account does not exist";
        }

        account.setCredit(cre); //deposits money to account
        //put back new deposit
        accounts.replace(name, account);

        return "Money deposited: " + cre + "\n Total Money: " + account.getCredit();

    }

    @PutMapping("/withdraw/{name}")
    public String withdraw(@PathVariable String name, @RequestBody int minus){
        Bank account = accounts.get(name);

        if(account  == null){
            return "Account does not exist";
        }

        if(minus > account.getCredit()){
            return "You do not have the funds";
        }

        account.minusCredit(minus);
        accounts.replace(name, account);

        return "Money has been withdraw: " + minus;
    }

    @DeleteMapping("delete/{name}")
    public String end(@PathVariable String name){
        Bank account = accounts.get(name);

        if(account == null){
            return "The account doesn't exist";
        }

        accounts.remove(name);

        return "The account, " + name + ", has been removed";
    }
}
