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
import onetoone.signupAPI.signup;
import onetoone.loginAPI.logs;

import java.util.*;

@RestController
@RequestMapping("/edit")
public class edit{

    @Autowired
    private editRepository edits;

    @PutMapping("/changeusernmae/{name}")
    public Map<String, String> ChangeUsername(@RequestBody logs l, @PathVariable String name){
        HashMap<String, String> response = new HashMap<>();
        signEntity user = edits.findByEmail(l.getUsername());

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

    @PutMapping("/changepassword/{password}")
    public Map<String, String> ChangePassword(@RequestBody logs l, @PathVariable String password){
        HashMap<String, String> response = new HashMap<>();
        signEntity user = edits.findByEmail(l.getUsername());

        if(user == null){
            response.put("response", "your email or password is wrong");
            return response;
        }

        if(!user.getPassword().equals(l.getPassword())){
            response.put("response", "Your password is wrong");
            return response;
        }

        long id = user.getId();
        edits.updatepassword(id, password);
        response.put("response", "Your password is changed");
        return response;
    }

}