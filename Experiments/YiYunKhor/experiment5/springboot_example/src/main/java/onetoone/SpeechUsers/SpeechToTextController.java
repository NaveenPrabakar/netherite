package onetoone.SpeechUsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

import onetoone.signupAPI.signRepository;//need to find the id of user
import onetoone.signupAPI.signEntity;
import onetoone.SpeechUsers.SpeechToTextRepository;
import onetoone.SpeechUsers.SpeechUserEntity;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//GET MApping is get the text from the speech
//Post Create User


@RestController
@RequestMapping("/SpeechToTextAIuse")
public class SpeechToTextController{

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

    //post-CreateUser
    //front end passing parameter (email and file)
    //parameter need to have the user email in order to access to sign entity to find the userid
    //return
    @PostMapping("/createSpeechUser")
    public Map<String, String> createSpeechUser(@RequestParam String email, @RequestParam("file") MultipartFile file){

        Map<String, String> response = new HashMap<>();

        try {
            //calling the speech to text method
            //Map <

            //need to go to signrepository to signentity to find username in order to find userid
            signEntity temp = sign.findByEmail(email);

            //check for if the user exist in the sign entity
            //if the user not exist in the table that means user never use it before
            if (temp==null){
                response.put("error", "the user is not found");
                return response;
            }

            //get the file name
            String fileName = file.getOriginalFilename();

            // Validate the file type and if the file name is null
            if (fileName == null || isValidAudioFile(fileName) == false) {
                responseMap.put("error", "Invalid file type. Only MP3, MP4, MPEG, MPGA, M4A, WAV, and WEBM files are accepted.");
                return response;
            }

            //store the id value in the userID
            Long userID = temp.getId();

            //need to create the table entity
            SpeechUserEntity sm = new SpeechUserEntity(userID, fileName);
            //need to save it
            api.save(sm);

            //need the extension from actual file name and type of tht file
            String extension = fileName.substring(filename.lastIndexOf("."));
            String fileType = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
            fileType += '-';

            //create the path with both of the info (file name and file type)
            Path tempFile = Files.createTempFile(fileType, extension);

            //add the file using the path
            file.transferTo(tempFile.toFile());

            //save the file into the server
            //folder named "upload_speech" in server
            String uploadDir = "upload_Speech/";

            File uploadDirFile = new File(uploadDir);

            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs(); // Create the directory if it doesn't exist
            }

            File savedSpeechFile = new File(uploadDir + fileName);
            Files.copy(tempFile, savedSpeechFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            //delete the path
            Files.delete(tempFile);

            response.put("ok", "Successfully Store Your Speech File");

            return response;
        }

        catch (IOException | TesseractException e) {
            response.put("error", "Cannot saved your file");
            return response;
        }
    }

    // This parameter accepts a file input from the user, specifically an MP3 file in this case.
    @GetMapping("/transcribe")
    private Map<String, String> transcribeAudio(@RequestParam("file") MultipartFile file) throws IOException {

        // JSON response
        Map<String, String> responseMap = new HashMap<>();

        // Validate the file type
        String fileName = file.getOriginalFilename();
        if (fileName == null || isValidAudioFile(fileName)==false) {
            responseMap.put("error", "Invalid file type. Only MP3, MP4, MPEG, MPGA, M4A, WAV, and WEBM files are accepted.");
            return responseMap;
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


        // Transcribe the audio using Whisper API
        String transcription;

        //HTTP Client Creation: CloseableHttpClient is created to make HTTP requests.
        //HTTP POST Setup: A POST request (HttpPost) is initialized with the Whisper API URL (openAiApiUrl).
        //Authorization Header: Adds an authorization header with the OpenAI API key to authenticate the request.

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost uploadFile = new HttpPost(openAiApiUrl);

            //The Authorization header is a standard HTTP header used to send credentials to authenticate a user or an application when making a request to a secured API.
            //The term Bearer indicates that the client (your application) is presenting a token that grants access to the API on behalf of a user.
            uploadFile.setHeader("Authorization", "Bearer " + openAiApiKey);

            //Create multipart request with the MP3 file
            //MultipartEntityBuilder: Constructs the multipart form data for the API request.
            //addBinaryBody("file", tempFile, ...): Attaches the MP3 file as a binary file in the "file" field. This is the format expected by Whisper API.
            //addTextBody("model", "whisper-1"): Specifies the transcription model to be used ("whisper-1" in this case).
            HttpEntity entity = MultipartEntityBuilder.create()
                    .addBinaryBody("file", tempFile, ContentType.DEFAULT_BINARY, tempFile.getName())
                    .addTextBody("model", "whisper-1")
                    .build();
            uploadFile.setEntity(entity);

            // Send request and handle response
            try (CloseableHttpResponse response = httpClient.execute(uploadFile)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    JsonNode jsonResponse = objectMapper.readTree(response.getEntity().getContent());
                    transcription = jsonResponse.path("text").asText();
                    responseMap.put("transcription", transcription);
                } else {
                    responseMap.put("error", "Failed to connect to Whisper API. Please check the API key and try again.");
                }
            }
        } catch (IOException e) {
            responseMap.put("error", "Cannot connect to the Whisper API. Possible issue with the API key or network connectivity.");
        } finally {
            // Clean up the temporary file
            tempFile.delete();
        }
        return responseMap;

        //return ResponseEntity.ok(responseMap);
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

        //just get the targeted one speech file
        //front end need to send me the email and file name
        @GetMapping("/getSpeechFile")
        public Map<String, String> getSpeechFile(@RequestParam String email, @RequestParam String speechFile) {

            Map<String, String> response = new HashMap<>();

            try{
            String uploadDir ="upload_Speech/";
            File file = new File(uploadDir + filename);

            if (!file.exists()) {
                response.put("error", "upload_Speech folder not found")
                return  response;
            }

            //need to go to signrepository to signentity to find username in order to find userid
            signEntity temp = sign.findByEmail(email);

            //check for if the user exist in the sign entity
            //if the user not exist in the table that means user never use it before
            if (temp==null){
                response.put("error", "the user is not found");
                return response;
            }

            //store the id value in the userID
            Long userID = temp.getId();

            // Find the user entity based on userId
            SpeechUserEntity speechUserEntity = api.findBySpeechUserId(userID).orElse(null);

            // Check if the entity exists
            if (speechUserEntity != null) {
                response.put("fileName", speechUserEntity.findBySpeechFile(speechFile));
            } else {
                response.put("error", "file is not found");
            }
            return response;
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
