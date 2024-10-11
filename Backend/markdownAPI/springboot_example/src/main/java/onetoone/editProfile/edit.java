package onetoone.editProfile;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.StandardCopyOption;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import onetoone.signupAPI.signEntity;
import onetoone.signupAPI.signRepository;
import onetoone.signupAPI.signup;
import onetoone.loginAPI.logs;
import onetoone.JsonRepository;

import java.util.*;

@RestController
@RequestMapping("/edit")
public class edit{

    private final Path location = Paths.get("root");

    @Autowired
    private editRepository edits;

    @Autowired
    private signRepository s;

    @Autowired
    private JsonRepository j;



    /**
     * The method allows the user to change their username
     *
     * @param l (email and password class)
     * @param name (the username they want to change to
     * @return a successful response or negative
     */
    @PutMapping("/changeusername/{name}")
    public Map<String, String> ChangeUsername(@RequestBody logs l, @PathVariable String name){
        HashMap<String, String> response = new HashMap<>();
        signEntity user = edits.findByEmail(l.getemail());



        //check if profile exists
        if(user == null){
            response.put("response", "user profile does not exist");
            return response;
        }

        //check if the password is correct
        if(!l.getPassword().equals(user.getPassword())){
            response.put("response", "password is incorrect");
            return response;
        }

        long id = user.getId();
        edits.updateusername(id, name);
        response.put("response", "username has been updated");

        return response;
    }

    /**
     * The method allows the user to change their password
     *
     * @param l (email and password body)
     * @param password -- password to be changed
     * @return Successful or failed response
     */
    @PutMapping("/changepassword/{password}")
    public Map<String, String> ChangePassword(@RequestBody logs l, @PathVariable String password){
        HashMap<String, String> response = new HashMap<>();
        signEntity user = edits.findByEmail(l.getemail());

        System.out.println(password);

        if(user == null){
            response.put("response", "your email or password is wrong");
            System.out.println("Wrong, user does not exist");
            return response;
        }

        if(!user.getPassword().equals(l.getPassword())){
            response.put("response", "Your password is wrong");
            System.out.println("check:" + user.getPassword() + "input: " + l.getPassword());
            return response;
        }

        long id = user.getId();
        edits.updatepassword(id, password);
        response.put("response", "Your password is changed");
        System.out.println("Correct");
        return response;
    }

    /**
     * The method changes the email
     *
     * @param l (email and password body)
     * @param email --email to be changed
     * @return successful or failed response
     */
    @PutMapping("/changeemail/{email}")
    public Map<String, String> ChangeEmail(@RequestBody logs l, @PathVariable String email){
        HashMap<String, String> response = new HashMap<>();
        signEntity user = edits.findByEmail(l.getemail());

        if(user == null){
            response.put("response", "Your email or password is wrong");
            return response;
        }

        if(!user.getPassword().equals(l.getPassword())){
            response.put("response", "Your password is wrong");
            return response;
        }

        long id = user.getId();
        edits.updateEmail(id, email);
        response.put("response", "Your Email has been updated");
        return response;

    }

    // DELETE EVERYTHING MUST BE DONE
    @DeleteMapping("/exterminateUser")
    public Map<String, String> Exterminate(@RequestParam("email") String email, @RequestParam("password") String password){
        HashMap<String, String> response = new HashMap<>();
        signEntity user = edits.findByEmail(email);

        if(user == null){
            response.put("response", "Email is wrong");
            return response;
        }

        if(!user.getPassword().equals(password)){
            response.put("response", "Your password is wrong");
            return response;
        }

        List<String> all = edits.findallFiles(user.getId());

        //Delete all the files from the springboot server
        for(int i = 0; i < all.size(); i++){

            Path filePath = location.resolve(all.get(i));

            if(Files.exists(filePath)){
                try{

                    Files.delete(filePath);
                }
                catch(IOException e){
                    response.put("response", "failed to delete");
                    return response;
                }
            }
        }

        edits.deleteAll(user.getId()); //Deletes all file names from the table
        j.deletepath(user.getId());
        s.deleteall(user.getId());

        return response;

    }
}