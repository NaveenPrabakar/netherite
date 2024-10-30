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
import java.util.*;

import onetoone.JsonRepository;
import onetoone.JsonEntity;

@RestController
@RequestMapping("/user")
public class signup{

    @Autowired
    private signRepository signup;

    @Autowired
    private JsonRepository j;

    @PostMapping("/create")
    public Map<String, String> createUser(@RequestBody signEntity sign){

        Map<String, String> check = new HashMap<>();
        if(sign == null){
            check.put("Response", "Invalid input. Please fillout all inputs");
            return check;
        }
        signEntity temp = signup.findByEmail(sign.getEmail());

        if(temp != null){
            check.put("Response", "User with email already exists");
            return check;
        }

        signup.save(sign); //Saves the users

        String path = "{'root' : [['shared']]}";

        temp = signup.findByEmail(sign.getEmail());
        JsonEntity je = new JsonEntity(path, temp.getId());
        j.save(je);

        check.put("Response", "You have successfully registered");
        return check;
    }
}





