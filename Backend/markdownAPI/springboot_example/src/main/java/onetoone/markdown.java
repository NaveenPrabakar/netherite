package onetoone;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.StandardCopyOption;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import onetoone.loginAPI.loginRepository;
import java.util.*;

import java.nio.charset.StandardCharsets;


@RestController
@RequestMapping("/files")
public class markdown {

    //Main directory for the files
    private final Path location = Paths.get("root");

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private loginRepository logs;

    @Autowired
    private JsonRepository j;

    @PostMapping("/upload")
    public HashMap<String, String> store(@RequestParam("fileName") String fileName, @RequestParam("content") String content, @RequestParam("json") String json,  @RequestParam("username") String username, @RequestParam("password") String password) {
        HashMap<String, String> response = new HashMap<>();

        System.out.println(json);

        try {

            if (!Files.exists(location)) {//Creates Directory if it doesn't exist
                Files.createDirectory(location);
            }

            signEntity user = logs.findByEmail(username);

            if(user == null){
                response.put("response", "user does not exist");
                return response;
            }

            String path = j.getSystem(user.getId());

            if(path != null){
                j.updatepath(user.getId(), json);
            }
            else{

                JsonEntity je = new JsonEntity(json, user.getId());
                j.save(je);
            }

            Path filePath = location.resolve(fileName);
            Files.write(filePath, content.getBytes(StandardCharsets.UTF_8));

            FileEntity fileEntity = new FileEntity(fileName, user.getId());
            fileRepository.save(fileEntity);

        } catch (IOException e) {
            response.put("response", "Could not save the file");
            return response;
        }

        response.put("response", "File saved");
        return response;
    }

    @GetMapping("/pull")
    public String pull(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("fileName") String fileName){

        FileEntity fileEntity = fileRepository.findByFileName(fileName);
        signEntity user = logs.findByEmail(username);

        if(fileEntity == null){
            return "response: file does not exist";
        }

        try {
            Path filePath = location.resolve(fileName);

            if (!Files.exists(filePath)) {
                return "File not found in the directory";
            }

            String content = new String(Files.readAllBytes(filePath));
            return content;

        } catch (IOException e) {
            return "Failed to retrieve file content due to an IO error: ";
        }
    }

    @GetMapping("/system")
    public String system(@RequestParam("username") String username, @RequestParam("password") String password){
        signEntity user = logs.findByEmail(username);

        if(user == null){
            return "User does not exist";
        }
        System.out.println(user.getId());

        String path = j.getSystem(user.getId());

        return path;
    }
}