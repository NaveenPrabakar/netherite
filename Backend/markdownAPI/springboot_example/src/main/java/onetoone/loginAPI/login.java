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

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/userLogin")
public class login{

    @Autowired
    private loginRepository login;

    @Autowired
    private JavaMailSender mailSender; // Autowire the mail sender

    //global variables
    //chance is for login chance
    int chance= 3;

    //checking if the username and password corret to login
    @PostMapping("/searchUsername")
    public Map<String, String> getUserByUsername(@RequestBody logs l ){
        HashMap<String, String> response = new HashMap<>();

        System.out.println(l.getUsername());
        signEntity temp = login.findByUsername(l.getUsername());

        if (temp == null) {
            response.put("response404", "User Profile does not exist");
            return response;
        }

        if(temp.getPassword().equals(l.getPassword())){
            response.put("response", "Password is correct, login success");
            return response;
        }

        response.put("response404", "password is incorrect");
        return response;
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

    @PostMapping("/forgot-password")
    public Map<String, String> requestPasswordReset(@RequestBody logs request) {
        Map<String, String> response = new HashMap<>();
        signEntity user = login.findByEmail(request.getUsername()); // Assuming username holds the email

        if (user == null) {
            response.put("response404", "User with this email does not exist");
            return response;
        }

        // Generate a token
        String token = UUID.randomUUID().toString();
        user.setResetToken(token); // Make sure to have this field in your signEntity
        login.save(user); // Save user with the reset token

        // Send the email
        sendPasswordResetEmail(user.getEmail(), token);
        response.put("response", "Password reset link has been sent to your email.");
        return response;
    }

    @PostMapping("/reset-password")
    public Map<String, String> resetPassword(@RequestParam String token, @RequestBody logs newPasswordRequest) {
        Map<String, String> response = new HashMap<>();
        signEntity user = login.findByResetToken(token); // Ensure you have a method to find by reset token

        if (user == null) {
            response.put("response404", "Invalid or expired token");
            return response;
        }

        // Update the password
        user.setPassword(newPasswordRequest.getPassword());
        user.setResetToken(null); // Clear the reset token
        login.save(user); // Save the updated user

        response.put("response", "Password has been successfully reset.");
        return response;
    }

    private void sendPasswordResetEmail(String toEmail, String token) {
        String resetUrl = "http://your-domain.com/reset-password?token=" + token; // Change to your domain
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click the link below:\n" + resetUrl);

        mailSender.send(message);
    }
}
