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


@RestController
@RequestMapping("/files")
public class markdown {

    //Main directory for the files
    private final Path location = Paths.get("root");

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private loginRepository logs;

    @PostMapping("/upload")
    public HashMap<String, String> store(@RequestParam("fileName") String fileName, @RequestParam("content") String content, @RequestBody logs l ) {
        HashMap<String, String> response = new HashMap<>();

        try {

            if (!Files.exists(location)) {//Creates Directory if it doesn't exist
                Files.createDirectory(location);
            }

            signEntity user = logs.findByEmail(l.getUsername());

            if(user == null){
                response.put("response", "user does not exist");
                return response;
            }

            Path filePath = location.resolve(fileName);
            Files.write(filePath, content.getBytes());

            FileEntity fileEntity = new FileEntity(fileName, user.getId());
            fileRepository.save(fileEntity);

        } catch (IOException e) {
            response.put("response", "Could not save the file");
            return response;
        }

        response.put("response", "File saved");

        return response;
    }
}