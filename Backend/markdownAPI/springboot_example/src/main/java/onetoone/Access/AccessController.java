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

    @Autowired
    private FileAccess fileAccessService;

    @PostMapping("/new")
    public ResponseEntity<String> share(@RequestParam("fromUser") String fromUser, @RequestParam ("toUser") String toUser, @RequestParam("docName") String docName ){

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

        file.addAccessEntity(a);

        return ResponseEntity.ok("The file was shared");
    }

    /**
     * Returns a list of people the user shared the file with
     * @param email
     * @param fileName
     * @return
     */
    @GetMapping("/sent")
    public ResponseEntity<List<String>> sent(@RequestParam("email") String email, @RequestParam("fileName") String fileName){

        signEntity user = logs.findByEmail(email);
        FileEntity file = files.findByFileName(fileName);

        List<String> error = new ArrayList<>();

        if(file == null || user == null) {

            error.add("the user or file does not exist");
            return ResponseEntity.badRequest().body(error);
        }

        List<String> names = access.sent(file, user);

        return ResponseEntity.ok(names);


    }

    @GetMapping("/filenames/{email}")
    public List<String> getFileNamesByAccessId(@PathVariable String email) {

        signEntity user = logs.findByEmail(email);
        return fileAccessService.getFileNamesByAccessId(user.getId());
    }


    }