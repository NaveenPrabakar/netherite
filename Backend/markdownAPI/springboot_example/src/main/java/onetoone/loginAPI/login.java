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

    // Forget password, need the username only (one argument) to find the email
    //send the generate code to front end as json
    //send the generate code to the user email
    @PostMapping("/forgotPassword")
    public Map<String, String> requestPasswordReset(@RequestBody logs l) {
        Map<String, String> response = new HashMap<>();


            signEntity temp = login.findByUsername(l.getUsername());

            if (temp == null) {
                response.put("response404", "User with this username does not exist");
                return response;
            }

            // Generate a random code
            String generatedCode = UUID.randomUUID().toString(); // Generate a unique code
           // response.put(temp.getEmail(), generatedCode);
        try {
            // Send the verification code to the user's email
            String value=sendVerificationCodeEmail(temp.getEmail(), generatedCode);

            // Optionally, send the generated code to the front end
            response.put("emialText status:",value );
            response.put("emailCode",temp.getEmail() );
            response.put("verificationCode", generatedCode);
            response.put("message", "Verification code has been sent to your email.");
        } catch (Exception e) {
            response.put("error", "Failed to send verification code: " + e.getMessage());
            e.printStackTrace(); // For debugging purposes, you might want to log this instead
        }

        return response;
    }

    // Update the newPassword with username, the new password
    //send front end with the responses and success
    @PutMapping("/resetPassword")
    public Map<String, String> resetPassword(@RequestBody logs l) {

        Map<String, String> response = new HashMap<>();

        signEntity temp = login.findByUsername(l.getUsername());

        if (temp == null) {
            response.put("response404", "User with this username does not exist");
            return response;
        }

        temp.setPassword(l.getPassword()); // Set the new password
        response.put("responses", "Password has been reset successfully.");

        return response;
    }

    // Helper method to send the verification code via email
//    private void sendVerificationCodeEmail(String toEmail, String generatedCode) {
//
//        SimpleMailMessage message = new SimpleMailMessage();
//
//        return "hi";
//        message.setTo(toEmail);
//        message.setSubject("Password Reset Code");
//        message.setText("Your password reset code is: " + generatedCode);
//
//        mailSender.send(message); // Send the email
//    }

    // Helper method to send the verification code via email
    private String sendVerificationCodeEmail(String toEmail, String generatedCode) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("15776abk@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Password Reset Code");
            message.setText("Your password reset code is: " + generatedCode);

            mailSender.send(message); // Send the email


        } catch (Exception e) {

            System.err.println("Failed to send email: " + e.getMessage()); // Log the exception
            e.printStackTrace();
        }
        return "fail to send the email text";
    }


}
