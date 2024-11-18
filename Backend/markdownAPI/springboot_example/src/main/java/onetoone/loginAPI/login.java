package onetoone.loginAPI;


import java.util.List;
import onetoone.signupAPI.signEntity;
import onetoone.signupAPI.signup;
import onetoone.editProfile.editRepository;

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

    @Autowired
    private editRepository edit;

    //global variables
    //chance is for login chance
    int chance= 3;

    /**
     * Logs in a user by verifying the email and password.
     *
     * @param l The login details containing email and password
     * @return A map containing the response message and username
     */
    @Operation(summary = "Login User", description = "Checks if the username and password are correct")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok"),
            @ApiResponse(responseCode = "404", description = "User Profile does not exist"),
            @ApiResponse(responseCode = "401", description = "Password is incorrect")
    })
    @PostMapping("/searchemail")
    public Map<String, String> getUserByUserEmail(@RequestBody logs l ){
        HashMap<String, String> response = new HashMap<>();

        signEntity temp = login.findByEmail(l.getemail());

        // IMPLEMENT ACTUAL ERRORS
        if (temp == null) {
            response.put("response", "User Profile does not exist");
            return response;
        }

        String userName= temp.getUsername();

        if(temp.getPassword().equals(l.getPassword())){
            response.put("response", "ok");
            response.put("userName", userName);
            return response;
        }

        response.put("response", "Password is incorrect");
        return response;
    }

    // Forget password, need the username only (one argument) to find the email
    //send the generate code to front end as json
    //send the generate code to the user email
    /**
     * Initiates the password reset process by sending a verification code to the user's email.
     *
     * @param l The login details containing the user's email
     * @return A map with the status of the email sending process and the verification code
     */
    @Operation(summary = "Forgot Password", description = "Sends a verification code to the user's email for password reset")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification code sent successfully"),
            @ApiResponse(responseCode = "404", description = "User with this username does not exist"),
            @ApiResponse(responseCode = "500", description = "Failed to send verification code")
    })
    @PostMapping("/forgotPassword")
    public Map<String, String> requestPasswordReset(@RequestBody logs l) {
        Map<String, String> response = new HashMap<>();

            signEntity temp = login.findByEmail(l.getemail());

            if (temp == null) {
                response.put("response404", "User with this username does not exist");
                return response;
            }

            // Generate a random code
            String generatedCode = UUID.randomUUID().toString(); // Generate a unique code
            generatedCode = generatedCode.substring(0,4);  //UPDATED SOME CODE HERE (MADE PASSKEY 4 LETTERS)
           // response.put(temp.getEmail(), generatedCode);
        try {
            // Send the verification code to the user's email
            String value=sendVerificationCodeEmail(temp.getEmail(), generatedCode);

            // Optionally, send the generated code to the front end
            response.put("emailText status:",value );
            response.put("emailCode",temp.getEmail() );
            response.put("verificationCode", generatedCode);
            response.put("message", "Verification code has been sent to your email.");
        } catch (Exception e) {
            response.put("Error", "Failed to send verification code: " + e.getMessage());
            e.printStackTrace(); // For debugging purposes, you might want to log this instead
        }

        return response;
    }

    // Update the newPassword with username, the new password
    //send front end with the responses and success
    /**
     * Resets the user's password using their email.
     *
     * @param l The login details containing the email and new password
     * @return A map with the status of the password reset operation
     */
    @Operation(summary = "Reset Password", description = "Resets the password for the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password has been reset successfully."),
            @ApiResponse(responseCode = "404", description = "User with this username does not exist")
    })
    @PutMapping("/resetPassword")
    public Map<String, String> resetPassword(@RequestBody logs l) {

        Map<String, String> response = new HashMap<>();

        signEntity temp = login.findByEmail(l.getemail());

        if (temp == null) {
            response.put("response404", "User with this username does not exist");
            return response;
        }

        temp.setPassword(l.getPassword()); // Set the new password
        edit.updatepassword(temp.getId(), l.getPassword());
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
    /**
     * Helper method to send a verification code to the user's email.
     *
     * @param toEmail        The recipient's email address
     * @param generatedCode  The generated verification code
     * @return A string indicating the status of the email sending process
     */
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
