package onetoone.speechToTextAPI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus; // Import for HttpStatus
import org.springframework.http.ResponseEntity; // Import for ResponseEntity
import org.springframework.web.bind.annotation.RestController; // Ensure you have this if using Spring annotations
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.http.HttpHeaders;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.MediaType;

import java.util.Arrays;

import onetoone.signupAPI.signRepository;//need to find the id of user
import onetoone.signupAPI.signEntity;
import onetoone.speechToTextAPI.SpeechToTextRepository;
import onetoone.speechToTextAPI.SpeechUserEntity;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.Map;

//GET MApping is get the text from the speech
//Post Create User

@RestController
@RequestMapping("/SpeechToTextAIuse")
public class SpeechToTextController{

    private static final MediaType APPLICATION_M4A = MediaType.parseMediaType("audio/mpeg");


    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${openai.api.url}")
    private String openAiApiUrl;

    //need to connect to the sign entity
    @Autowired
    private signRepository sign;

    //this is connected to the repository to use the method inside for getting valuye in the speech totext entity
    //and really store it
    @Autowired
    private SpeechToTextRepository api;

    private final ObjectMapper objectMapper = new ObjectMapper();

    //for the files format
    private static final String[] ALLOWED_AUDIO_EXTENSIONS = {
            "mp3", "mp4", "mpeg", "mpga", "m4a", "wav", "webm"
    };

    //global variable
    String uploadDir = "upload_Speech/";

    //post-CreateUser
    //front end passing parameter (email and file)
    //parameter need to have the user email in order to access to sign entity to find the userid
    //return
    @PostMapping("/createSpeechUser")
    public ResponseEntity<String> createSpeechUser(@RequestParam("email") String email, @RequestParam("file") MultipartFile file) {
        try {
            //calling the speech to text method
            //Map <

            //need to go to signrepository to signentity to find username in order to find userid
            signEntity temp = sign.findByEmail(email);

            //check for if the user exist in the sign entity
            //if the user not exist in the table that means user never use it before
            if (temp == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The user is not found");
            }

            //get the file name
            String fileName = file.getOriginalFilename();

            // Validate the file type and if the file name is null
            if (fileName == null || !isValidAudioFile(fileName)) {
                return ResponseEntity.badRequest().body("Invalid file type. Only MP3, MP4, MPEG, MPGA, M4A, WAV, and WEBM files are accepted.");
            }

            //store the id value in the userID
            Long userID = temp.getId();

            //need to create the table entity
            SpeechUserEntity sm = new SpeechUserEntity(userID, fileName);

            //need to save it
            api.save(sm);

            //need the extension from actual file name and type of tht file
            String extension = fileName.substring(fileName.lastIndexOf("."));
            String fileType = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase() + '-';

            //create the path with both of the info (file type and file name)
            Path tempFile = Files.createTempFile(fileType, extension);

            // define the main upload directory
            //String uploadDir = "upload_Speech/";
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }

            // create subdirectory for the user ID inside the upload_speech folder
            String userSubDirPath = uploadDir + userID + "/";
            File userSubDir = new File(userSubDirPath);
            if (!userSubDir.exists()) {
                userSubDir.mkdirs();
            }

            // create the full path for saving the file inside the user ID directory
            File savedSpeechFile = new File(userSubDirPath + fileName);
            Files.copy(tempFile, savedSpeechFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            //delete the path
            Files.delete(tempFile);

            return ResponseEntity.ok("Successfully stored your speech file");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cannot save your file");
        }
    }

    // This parameter accepts a file input from the user, specifically an MP3 file in this case
    @GetMapping("/transcribe")
    public ResponseEntity<String> transcribeAudio(@RequestParam("file") MultipartFile file) throws IOException {

        // Validate the file type
        String fileName = file.getOriginalFilename();

        if (fileName == null || !isValidAudioFile(fileName)) {
            return ResponseEntity.badRequest().body("Invalid file type. Only MP3, MP4, MPEG, MPGA, M4A, WAV, and WEBM files are accepted.");
        }

        // Save the MP3 file locally
        //File.createTempFile("audio", ".mp3"): Creates a temporary file with a random name that starts with "audio" and has an .mp3 extension.
        //file.transferTo(tempFile): Transfers the contents of the uploaded MP3 file to this
//        File tempFile = File.createTempFile("audio", ".m4a");
//        file.transferTo(tempFile);

        // Extract the file extension and create a temporary file
        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        File tempFile = File.createTempFile("audio", "." + fileExtension);
        file.transferTo(tempFile);

        //HTTP Client Creation: CloseableHttpClient is created to make HTTP requests.
        //HTTP POST Setup: A POST request (HttpPost) is initialized with the Whisper API URL (openAiApiUrl).
        //Authorization Header: Adds an authorization header with the OpenAI API key to authenticate the request.
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost uploadFile = new HttpPost(openAiApiUrl);
            uploadFile.setHeader("Authorization", "Bearer " + openAiApiKey);

            HttpEntity entity = MultipartEntityBuilder.create()
                    .addBinaryBody("file", tempFile, ContentType.DEFAULT_BINARY, tempFile.getName())
                    .addTextBody("model", "whisper-1")
                    .build();
            uploadFile.setEntity(entity);

            //Create multipart request with the MP3 file
            //MultipartEntityBuilder: Constructs the multipart form data for the API request.
            //addBinaryBody("file", tempFile, ...): Attaches the MP3 file as a binary file in the "file" field. This is the format expected by Whisper API.
            //addTextBody("model", "whisper-1"): Specifies the transcription model to be used ("whisper-1" in this case).
            // Send request and handle response
            try (CloseableHttpResponse response = httpClient.execute(uploadFile)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    JsonNode jsonResponse = objectMapper.readTree(response.getEntity().getContent());
                    String transcription = jsonResponse.path("text").asText();
                    return ResponseEntity.ok(transcription);
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to connect to Whisper API. Please check the API key and try again.");
                }
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cannot connect to the Whisper API. Possible issue with the API key or network connectivity.");
        } finally {
            tempFile.delete();
        }
    }


//    @GetMapping("/getFileNames")
//    public Map<String, String> getFileName(@RequestParam String email) {
//        Map<String, String> response = new HashMap<>();
//
//        //need to go to signrepository to signentity to find username in order to find userid
//        signEntity temp = sign.findByEmail(email);
//
//        //check for if the user exist in the sign entity
//        //if the user not exist in the table that means user never use it before
//        if (temp==null){
//            response.put("error", "the user is not found");
//            return response;
//        }
//
//        //store the id value in the userID
//        Long userID = temp.getId();
//
//        // Find the user entity based on userId
//        SpeechUserEntity speechUserEntity = api.findById(userID).orElse(null);
//
//        // Check if the entity exists
//        if (speechUserEntity != null) {
//            response.put("fileName", speechUserEntity.getSpeechFile());
//        } else {
//            response.put("error", "file is not found");
//        }
//        return response;
//    }

    @GetMapping("/getSpeechFile")
    public ResponseEntity<Resource> getSpeechFile(@RequestParam("email") String email, @RequestParam("speechFile") String speechFile) {
        try {
            signEntity temp = sign.findByEmail(email);
            if (temp == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return null for user not found
            }

            Long userID = temp.getId();
            //SpeechUserEntity speechUserEntity = api.findBySpeechUserId(userID);

            //String uploadDir = "upload_speech/";
            String filePath = uploadDir + userID + "/" + speechFile;
            File file = new File(filePath);

            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return null as there's no resource to send
            }

            SpeechUserEntity speechUserEntity = api.findBySpeechFile(speechFile);

            if (speechUserEntity != null) {

                //MediaType mediaType = MediaType.APPLICATION_MP3;
                Resource resource = new FileSystemResource(file);

                System.out.println("This sucks");
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)  // Use appropriate media type
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return null for file not found
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Return null in case of an error
        }
    }

    //helper method to check the validality
    private boolean isValidAudioFile(String fileName) {
        // Check if the file has a valid extension
        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        boolean found=Arrays.asList(ALLOWED_AUDIO_EXTENSIONS).contains(fileExtension);
        if (found==true){
            return true;
        }
        return false;
    }

}
