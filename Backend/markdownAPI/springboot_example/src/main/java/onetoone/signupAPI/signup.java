package onetoone.signupAPI;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/user")
public class signup{

    @Autowired
    private signRepository signup;

    @PostMapping("/create")
    public String createUser(@RequestBody signEntity sign){
        if(sign == null){
            return "Invalid input. Please fillout all inputs";
        }

        signup.save(sign); //Saves the users
        return "You have successfully registered";
    }
}





