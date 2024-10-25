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
    public HashMap<String, String> share(@RequestParam("owner") String owner, @RequestParam("fileName") String fileName, @RequestParam("Access") String Access){

        HashMap<String, String> response = new HashMap<>();

        signEntity sign = logs.findByEmail(owner);

        if(sign == null){
            response.put("response", "User does not exist");
            return response;
        }

        FileEntity file = files.findByFileName(fileName);

        if(file.getId() != sign.getId()){
            response.put("response", "User does not have this file");
            return response;
        }

        signEntity acc = logs.findByEmail(Access);

        if(acc == null){
            response.put("response", "This user does not exist");
            return response;
        }

        AccessEntity a = new AccessEntity(sign, file, acc);
        access.save(a);

        response.put("response", "File has been shared");
        return response;
    }
}