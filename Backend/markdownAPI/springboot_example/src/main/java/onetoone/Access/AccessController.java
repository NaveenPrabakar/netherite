package onetoone.Access;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import onetoone.signupAPI.signEntity;
import onetoone.signupAPI.signup;
import onetoone.loginAPI.logs;
import onetoone.loginAPI.loginRepository;
import onetoone.FileRepository;
import onetoone.FileEntity;
import onetoone.Access.AccessRepository;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/share")

public class AccessController{

    @Autowired
    private FileRepository files;

    @Autowired
    private loginRepository logs;

    @Autowired
    private AccessRepository access;

    @PostMapping("/new")
    public ResponseEntity<String> share(@RequestParam("fromUser") String fromUser, @RequestParam ("toUser") String toUser, @RequestParam("docName") String docName ){

        HashMap<String, String> response = new HashMap<>();

        signEntity sign2 = logs.findByEmail(toUser);
        signEntity sign = logs.findByEmail(fromUser);

        if(sign2 == null){
            return ResponseEntity.badRequest().body("The user does not exist");
        }

        FileEntity file = files.findByFileName(docName);

        if(file.getId() != sign.getId()){
            return ResponseEntity.badRequest().body("The user does not have this file");
        }

        AccessEntity a = new AccessEntity(sign, file, sign2);
        access.save(a);

        return ResponseEntity.ok("The file was shared");
    }


    }