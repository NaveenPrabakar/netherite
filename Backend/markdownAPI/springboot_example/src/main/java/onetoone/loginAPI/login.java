package onetoone.loginAPI;

import java.util.List;
import onetoone.signupAPI.signEntity;
import onetoone.signupAPI.signup;

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
@RequestMapping("/userLogin")
public class login{

    @Autowired
    private loginRepository login;

    //global variables
    //chance is for login chance
    int chance=3;

    //checking if the user email exist testing
    @GetMapping("/searchEmail/{email}")
    signEntity getUserByEmail( @PathVariable String email){
       if (loginRepository.findByEmailId(emailId))==null{
           return "the user email non exist";
        }
       else{
           return "it exist";
        }
    }

    //checking if the user email exist and also the password is correct (create)
    @PostMapping("/login/{email}/{password}")
    signEntity searchUserByEmail(@PathVariable String email, @PathVariable String password){

        //store in temp
        signEntity temp = loginRepository.findByEmailId(emailId);

        //check the email first if it exist
        if (temp == null) {
            return "Your Email Non-Exist\n";
        }

        while(chance!=0) {
                if (temp.getPassword().equals(password)) {
                    return "Login Successfully\n";
                }
                else {
                    chance--;
                    return "Login Failed, wrong password, attempt time:"+ chance;
                }
            }
        }

    //update the password with the email given (update)
    @PutMapping("/changePassword/{email}/{password}")
    signEntity searchUserByEmail(@PathVariable String email, @PathVariable String password){

        //store in temp
        signEntity temp = loginRepository.findByEmailId(emailId);

            if (temp == null) {
                return "Your Email Non-Exist\n";
            }
            else {
                temp.setPassword(password);
            }
    }
}
