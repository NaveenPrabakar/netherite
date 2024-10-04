package onetoone.loginAPI;

import java.util.List;
import onetoone.signupAPI.signEntity;
import onetoone.signupAPI.signup;
import java.util.Map;
import java.util.HashMap;

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
    int chance= 3;

    //checking if the user email exist testing
    @PostMapping("/searchEmail")
    Map<String, String> getUserByEmail(@RequestBody logs l ){

        HashMap<String, String> data = new HashMap<>();

        System.out.println(l.getUsername());
        signEntity temp = login.findByEmail(l.getUsername());

        if (temp == null) {
            data.put("response", "Email non-exist");
            return data;
        }

        if(temp.getPassword().equals(l.getPassword())){
            data.put("response", "Login Success");
            return data;
        }

        data.put("response", "Login Fail");
        return data;
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
