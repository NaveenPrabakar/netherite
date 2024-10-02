package onetoone.Users;

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
public class UserSignUpController{

    @Autowired
    private UserRepository userRepository;

    //return the user by emailid
    @GetMapping(path = "/usersGetting/{emailId}")
    User getUserByEmailId( @PathVariable String emailId){

        return userRepository.findByEmailId(emailId);
    }

    //return list of user
    @GetMapping(path = "/users")
    List<User> getAllUsers(){
        return userRepository.findAll();
    }

    //  create the user
    @PostMapping ("/usersCreating")
    String createUser(@RequestBody User user) {
        if (user == null)
            return "User registered fail";

        if(userRepository.findByEmailId(user.getEmailId()) != null){
            return "User already exists";
        }

        userRepository.save(user);
        return"User registered successfully!";
    }

    //update the users information
    @PutMapping("/usersUpdating/{emailId}")
    User updateUser(@PathVariable String emailId, @RequestBody User request){
        User user = userRepository.findByEmailId(emailId);
        if(user == null) {
            throw new RuntimeException("Person email does not exist");
        }
        else if (user.getEmailId()!= emailId){
            throw new RuntimeException("Person email does not match");
        }
        userRepository.save(request);
        return userRepository.findByEmailId(emailId);
    }

    //delete the user
    @DeleteMapping(path = "/usersDeleting/{emailId}")
    String deleteUser(@PathVariable String emailId){
        userRepository.deleteByEmailId(emailId);
        return "User deleted successfully!";
    }

}
