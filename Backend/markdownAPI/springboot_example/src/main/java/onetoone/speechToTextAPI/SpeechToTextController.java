package onetoone.speechToTextAPI;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;


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
import org.springframework.web.bind.annotation.PathVariable;

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
@Tag(name = "SpeechToText API", description = "Done By Yi Yun Khor")
public class SpeechToTextController{

    //System.out.println("Hi u r using the api for speech to text!");

    //private static final MediaType APPLICATION_M4A = MediaType.parseMediaType("audio/m4a");


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
    String uploadDir2 = "upload_speech/";

    //post-CreateUser
    //front end passing parameter (email and file)
    //parameter need to have the user email in order to access to sign entity to find the userid
    //return
    /**
     * Creates a new entry for the user in the speech-to-text table with the uploaded audio file.
     *
     * @param email the user's email to identify or create a user entry.
     * @param file the audio file (MP3 or WAV) to be uploaded and processed.
     * @return ResponseEntity with a success message if the file is uploaded successfully,
     * or an error message if the file type is invalid or if any exception occurs.
     */
    @Operation(summary = "Create Speech User",
            description = "Creates a new entry for the user in the speech-to-text table with the uploaded audio file.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully stored your speech file"),
            @ApiResponse(responseCode = "400", description = "Invalid file type. Only MP3, MP4, MPEG, MPGA, M4A, WAV, and WEBM files are accepted.",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "The user is not found",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/createSpeechUser/{email}")
    public ResponseEntity<String> createSpeechUser(@PathVariable String email, @RequestParam("audio") MultipartFile file) {
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
            //String uploadDir2 = "upload_Speech/";
            File uploadDir2File = new File(uploadDir2);
            if (!uploadDir2File.exists()) {
                uploadDir2File.mkdirs();
            }

            // create subdirectory for the user ID inside the upload_speech folder
            String userSubDirPath = uploadDir2 + userID + "/";
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
//    @PostMapping("/transcribesssss")
//    public ResponseEntity<String> transcribeAudio(@RequestParam("audio") MultipartFile file) throws IOException {
//
//        // Validate the file type
//        String fileName = file.getOriginalFilename();
//
//        if (fileName == null || !isValidAudioFile(fileName)) {
//            return ResponseEntity.badRequest().body("Invalid file type. Only MP3, MP4, MPEG, MPGA, M4A, WAV, and WEBM files are accepted.");
//        }
//
//        // Save the MP3 file locally
//        //File.createTempFile("audio", ".mp3"): Creates a temporary file with a random name that starts with "audio" and has an .mp3 extension.
//        //file.transferTo(tempFile): Transfers the contents of the uploaded MP3 file to this
////        File tempFile = File.createTempFile("audio", ".m4a");
////        file.transferTo(tempFile);
//
//        // Extract the file extension and create a temporary file
//        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
//        File tempFile = File.createTempFile("audio", "." + fileExtension);
//        file.transferTo(tempFile);
//
//        //HTTP Client Creation: CloseableHttpClient is created to make HTTP requests.
//        //HTTP POST Setup: A POST request (HttpPost) is initialized with the Whisper API URL (openAiApiUrl).
//        //Authorization Header: Adds an authorization header with the OpenAI API key to authenticate the request.
//        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//            HttpPost uploadFile = new HttpPost(openAiApiUrl);
//            uploadFile.setHeader("Authorization", "Bearer " + openAiApiKey);
//
//            HttpEntity entity = MultipartEntityBuilder.create()
//                    .addBinaryBody("file", tempFile, ContentType.DEFAULT_BINARY, tempFile.getName())
//                    .addTextBody("model", "whisper-1")
//                    .build();
//            uploadFile.setEntity(entity);
//
//            //Create multipart request with the MP3 file
//            //MultipartEntityBuilder: Constructs the multipart form data for the API request.
//            //addBinaryBody("file", tempFile, ...): Attaches the MP3 file as a binary file in the "file" field. This is the format expected by Whisper API.
//            //addTextBody("model", "whisper-1"): Specifies the transcription model to be used ("whisper-1" in this case).
//            // Send request and handle response
//            try (CloseableHttpResponse response = httpClient.execute(uploadFile)) {
//                int statusCode = response.getStatusLine().getStatusCode();
//                if (statusCode == 200) {
//                    JsonNode jsonResponse = objectMapper.readTree(response.getEntity().getContent());
//                    String transcription = jsonResponse.path("text").asText();
//                    return ResponseEntity.ok(transcription);
//                } else {
//                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to connect to Whisper API. Please check the API key and try again.");
//                }
//            }
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cannot connect to the Whisper API. Possible issue with the API key or network connectivity.");
//        } finally {
//            tempFile.delete();
//        }
//    }
    /**
     * Transcribes the uploaded audio file using OpenAI's Whisper API.
     *
     * @param file the audio file (MP3 or WAV) to be transcribed.
     * @return ResponseEntity with the transcribed text if successful,
     * or an error message if the file type is invalid or if the transcription fails.
     */
    @Operation(summary = "Transcribe Audio File",
            description = "Transcribes an uploaded audio file using OpenAI's Whisper API.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transcription successful",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file type or empty file",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Failed to transcribe audio",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/transcribe2")
    public ResponseEntity<String> transcribeAudio2(@RequestParam("audio") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file was uploaded");
        }

        try {
            String fileName = file.getOriginalFilename();
            long size = file.getSize();
            String contentType = file.getContentType();

            String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
            File tempFile = File.createTempFile("audio", "." + fileExtension);
            file.transferTo(tempFile);

            // Create temporary file with file extension
            String extension = fileName.substring(fileName.lastIndexOf("."));
            String fileType = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase() + '-';

            // Create the path with both file type and name
            Path tempFile2 = Files.createTempFile(fileType, extension);

            // Define the main upload directory
            File uploadDir2File = new File(uploadDir2);
            if (!uploadDir2File.exists()) {
                uploadDir2File.mkdirs();
            }

            // Create subdirectory for the user ID inside the upload_speech folder
            String userSubDirPath = uploadDir2 +  "/";
            File userSubDir = new File(userSubDirPath);
            if (!userSubDir.exists()) {
                userSubDir.mkdirs();
            }

            // Create the full path for saving the file inside the user ID directory
            File savedSpeechFile = new File(userSubDirPath + fileName);
            Files.copy(tempFile2, savedSpeechFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Delete the temporary file after processing
            Files.delete(tempFile2);

            // Log file info
            System.out.println("Received file: " + fileName + ", size: " + size + ", contentType: " + contentType);

            // OpenAI Whisper API integration for transcription
            String transcription = transcribeWithOpenAI(tempFile);

            // Return transcription or failure response
            if (transcription != null) {
                return ResponseEntity.ok(transcription);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to transcribe audio.");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process file");
        }
    }

    private String transcribeWithOpenAI(File audioFile) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost uploadFile = new HttpPost(openAiApiUrl);
            uploadFile.setHeader("Authorization", "Bearer " + openAiApiKey);

            HttpEntity entity = MultipartEntityBuilder.create()
                    .addBinaryBody("file", audioFile, ContentType.DEFAULT_BINARY, audioFile.getName())
                    .addTextBody("model", "whisper-1")
                    .build();
            uploadFile.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(uploadFile)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    JsonNode jsonResponse = objectMapper.readTree(response.getEntity().getContent());
                    return jsonResponse.path("text").asText();
                } else {
                    System.err.println("Failed to connect to Whisper API: " + statusCode);
                    return null;
                }
            }
        } catch (IOException e) {
            System.err.println("Error during API request: " + e.getMessage());
            return null;
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

    /**
     * Endpoint to retrieve a user's speech file.
     *
     * @param email      -- User's email address
     * @param speechFile -- Name of the speech file to be retrieved
     * @return ResponseEntity<Resource> -- The speech file as a response if found, otherwise an error response
     */
    @Operation(summary = "Retrieve a user's speech file", description = "Fetches a specific speech file based on user email and file name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User or file not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/getSpeechFile")
    public ResponseEntity<Resource> getSpeechFile(@RequestParam("email") String email, @RequestParam("speechFile") String speechFile) {
        try {
            signEntity temp = sign.findByEmail(email);
            if (temp == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return null for user not found
            }

            Long userID = temp.getId();
            //SpeechUserEntity speechUserEntity = api.findBySpeechUserId(userID);

            //String uploadDir2 = "upload_speech/";
            String filePath = uploadDir2 + userID + "/" + speechFile;
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
    /**
     * Helper method to check the validity of an audio file based on its extension.
     * It checks if the file extension is in the allowed list of audio extensions.
     *
     * @param fileName The name of the file to be checked
     * @return true if the file extension is valid, false otherwise
     */
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
