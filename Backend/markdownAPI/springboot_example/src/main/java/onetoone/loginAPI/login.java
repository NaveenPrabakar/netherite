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
    @PostMapping("/searchEmail")
    String getUserByEmail(@RequestBody logs l ){

        System.out.println(l.getEmail());
        signEntity temp = login.findByEmail(l.getEmail());

        if (temp == null) {
            return "No";
        }

        if(temp.getPassword().equals(l.getPassword())){
            return "yes";
        }

        return "no";
    }

    //checking if the user email exist and also the password is correct (create)
    @PostMapping("/login/{email}/{password}")
    String searchUserByEmail(@PathVariable String email, @PathVariable String password){

        //store in temp
        signEntity temp = login.findByEmail(email);

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
                    return "Login Failed, wrong password. Attempt time:"+ chance;
                }
            }

        return "Reset Password";
        }

    //update the password with the email given (update)
    @PutMapping("/changePassword/{email}/{password}")
    String changeUserByEmail(@PathVariable String email, @PathVariable String password){

        //store in temp
        signEntity temp = login.findByEmail(email);

            if (temp == null) {
                return "Your Email Non-Exist\n";
            }
            else {
                temp.setPassword(password);
                return "Your email exists";
            }
    }
}
