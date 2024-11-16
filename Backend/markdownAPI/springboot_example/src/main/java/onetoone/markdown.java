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
import onetoone.signupAPI.signup;
import onetoone.signupAPI.signRepository;
import onetoone.loginAPI.logs;
import org.springframework.beans.factory.annotation.Autowired;
import onetoone.loginAPI.loginRepository;
import java.util.*;
import onetoone.signupAPI.signEntity;  // Ensure the correct package path


import java.nio.charset.StandardCharsets;
import org.json.*;
import onetoone.*;
import onetoone.Access.*;


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

    @Autowired
    private AccessRepository access;

    @Autowired
    private signRepository signup;

    /**
     * The method saves a file to the approriate directory
     *
     * @param fileName -- Name of the file
     * @param content  -- content of the file
     * @param json     -- directory of the file
     * @param username -- email
     * @param password -- password
     * @return A successful response
     */

    @PostMapping("/upload")
    public HashMap<String, String> store(@RequestParam("fileName") String fileName, @RequestParam("content") String content, @RequestParam("json") String json, @RequestParam("email") String email, @RequestParam("password") String password) {
        HashMap<String, String> response = new HashMap<>();
        try {

            if (!Files.exists(location)) {//Creates Directory if it doesn't exist
                Files.createDirectory(location);
            }

            signEntity user = logs.findByEmail(email);

            if (user == null) {
                response.put("response", "user does not exist");

                return response;
            }

            String path = j.getSystem(user.getId());

            if (path != null) {
                j.updatepath(user.getId(), json);
            } else {

                JsonEntity je = new JsonEntity(json, user.getId());
                j.save(je);
            }

            Path filePath = location.resolve(fileName);
            Files.write(filePath, content.getBytes(StandardCharsets.UTF_8));

            FileEntity temp = fileRepository.findByFileName(fileName);

            if (temp == null) {
                FileEntity fileEntity = new FileEntity(fileName, user.getId());
                fileRepository.save(fileEntity);
            }

        } catch (IOException e) {
            response.put("response", "Could not save the file");
            return response;
        }

        response.put("response", "File saved");
        return response;
    }

    /**
     * The Get mapping grabs the contents of a certain file
     *
     * @param email    -- email
     * @param password --password
     * @param fileName -- name of file requested
     * @return -- the contents of the file
     */
    @GetMapping("/pull")
    public String pull(@RequestParam("email") String email, @RequestParam("password") String password, @RequestParam("fileName") String fileName) {


        FileEntity fileEntity = fileRepository.findByFileName(fileName);
        String temp = Long.toString(fileEntity.getfileId());
        signEntity user = logs.findByEmail(email);

        if (fileEntity == null) {
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

    @GetMapping("/fileid")
    public String pulled(@RequestParam("email") String email, @RequestParam("fileName") String fileName) {

        FileEntity fileEntity = fileRepository.findByFileName(fileName);
        String temp = Long.toString(fileEntity.getfileId());
        signEntity user = logs.findByEmail(email);

        return temp;

    }


    /**
     * The method grabs the User's associated Json path
     *
     * @param email    -- email of the user
     * @param password -- password of the user
     * @return the Json path
     */
    @GetMapping("/system")
    public String system(@RequestParam("email") String email, @RequestParam("password") String password) {
        signEntity user = logs.findByEmail(email);

        System.out.println(email);
        System.out.println(password);

        if (user == null) {
            return "User does not exist";
        }

        String path = j.getSystem(user.getId());
        System.out.println(path);
        return path;
    }

    @PutMapping("/update")
    public String updates(@RequestParam("email") String email, @RequestParam("json") String json) {

        signEntity user = logs.findByEmail(email);

        j.updatepath(user.getId(), json);

        return "Update is done";

    }


    @DeleteMapping("/deleteFile")
    public Map<String, String> delete(@RequestParam("email") String email, @RequestParam("fileName") String fileName, @RequestParam("json") String json) {
        signEntity user = logs.findByEmail(email);
        HashMap<String, String> response = new HashMap<>();

        System.out.println(fileName);

        if (user == null) {
            response.put("response", "this user does not exist");
            return response;
        }

        FileEntity file = fileRepository.findByFileName(fileName);

        //if it's owner
        if(file.getId() == user.getId()) {

            if (file == null) {
                response.put("response", "the file does not exist");
                return response;
            }

            Path filePath = location.resolve(fileName);

            if (user.getId() == file.getId()) {
                //deletes access from all users
                Optional<signEntity> allOptional = signup.findById(file.getId());
                signEntity all = allOptional.orElse(null);
                System.out.println(all.getId());
                List<signEntity> lists = access.sents(file, all);
                access.deleteBySignEntity(all);

                fileRepository.deleteByFileName(fileName); //deletes the file from the table
                j.updatepath(user.getId(), json);
                response.put("response", "The file was deleted");

                for(int i = 0; i < lists.size(); i++){
                    signEntity s = lists.get(i);
                    String ss = j.getSystem(s.getId());
                    ss = delete(ss, fileName);
                    j.updatepath(s.getId(), ss);

                }



                if (Files.exists(filePath)) {//Deletes the file from the springboot_server
                    try {
                        Files.delete(filePath);
                    } catch (IOException e) {
                        response.put("response", "file was not found");
                        return response;

                    }


                }


                return response;
            }
        }
        else{

            String deleted = delete(json, fileName);
            j.updatepath(user.getId(), deleted);

        }

        response.put("response", "the file was deleted.");
        return response;
    }

    /**
     * Parse through the json
     * @param json
     * @param name
     * @return
     */
    private String delete(String json, String name){
        JSONObject fs = new JSONObject(json);
        JSONArray fsArr = fs.getJSONArray("root");

        for(int i = 0; i < fsArr.length(); i++){
            Object item = fsArr.get(i);
            if(item instanceof JSONObject){
                JSONObject temp = (JSONObject) item;
                String internalKey = temp.keys().next();
                if(internalKey.equals("share")){
                    JSONArray sha  = temp.getJSONArray("share");

                    for(int j = 0; j < sha.length(); i++){
                        if(sha.get(j).equals(name)){
                            sha.remove(j);
                            return fs.toString();
                        }
                    }

                }
            }
        }
        return json;
    }


}

