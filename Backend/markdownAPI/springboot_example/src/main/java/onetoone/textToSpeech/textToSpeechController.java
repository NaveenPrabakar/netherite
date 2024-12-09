package onetoone.textToSpeech;

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
//import org.apache.http.HttpEntity;
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
import onetoone.textToSpeech.textToSpeechRepository;
import onetoone.textToSpeech.textToSpeechEntity;

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

//for the open aiapi
import java.nio.charset.StandardCharsets;
import org.springframework.web.client.RestTemplate; // For RestTemplate
import org.json.JSONObject; // For JSONObject
import org.json.JSONArray; // For JSONArray
import org.springframework.http.HttpEntity; // Use Spring's HttpEntity
import org.springframework.http.HttpMethod;

//for the text to speech
import java.io.IOException;
import java.nio.charset.StandardCharsets;



@RestController
@RequestMapping("/textToSpeech")
@Tag(name = "textTpSpeech API", description = "Done By Yi Yun Khor")
public class textToSpeechController {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    //need to connect to the sign entity
    @Autowired
    private signRepository sign;

    //this is connected to the repository to use the method inside for getting valuye in the speech totext entity
    //and really store it
    @Autowired
    private textToSpeechRepository api;

    private final ObjectMapper objectMapper = new ObjectMapper();

    //for the files format
    private static final String[] ALLOWED_FILE_EXTENSIONS = {
            "md", "txt", "json", "csv"
    };

    //global variable
    String uploadDir2 = "upload_textToSpeech/";

    //post-CreateUser
    //front end passing parameter (email and file)
    //parameter need to have the user email in order to access to sign entity to find the userid
    //return

    /**
     * Creates a new entry for the user in the translate text table with the uploaded file.
     *
     * @param email the user's email to identify or create a user entry.
     * @param file  the audio file (MP3 or WAV) to be uploaded and processed.
     * @return ResponseEntity with a success message if the file is uploaded successfully,
     * or an error message if the file type is invalid or if any exception occurs.
     */
    @Operation(summary = "Create textToSpeech User",
            description = "Creates a new entry for the user in th table with the e trabslate uploaded audio file.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully stored your audio file"),
            @ApiResponse(responseCode = "404", description = "The user is not found",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Cannot save your audio file",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/createTextToSpeechUser/{email}")
    public ResponseEntity<String> createSpeechUser(@PathVariable String email, @RequestParam("audio") MultipartFile file) {
        try {
            //need to go to signrepository to signentity to find username in order to find userid
            signEntity temp = sign.findByEmail(email);

            //check for if the user exist in the sign entity
            //if the user not exist in the table that means user never use it before
            if (temp == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The user is not found");
            }

            //get the file name
            String fileName = file.getOriginalFilename();

            //store the id value in the userID
            Long userID = temp.getId();

            //need to create the table entity
            textToSpeechEntity sm = new textToSpeechEntity(userID, fileName);

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

            return ResponseEntity.ok("Successfully stored your audio file");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cannot save your audio file");
        }
    }
    /**
     * Converts text to speech and returns the audio file.
     *
     * @param textContent the text to be converted into an audio file.
     * @return ResponseEntity containing the generated audio file or an error message.
     */
    @Operation(summary = "Convert text to audio",
            description = "Converts the provided text into an audio file and returns it as a downloadable response.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully generated audio file",
                    content = @Content(mediaType = "audio/mpeg", schema = @Schema(implementation = byte[].class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or request format",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })

    @PostMapping("/synthesizer")
    public ResponseEntity<byte[]> convertTextToAudio(@RequestParam("text") String textContent) {
        try {
            // Step 1: Escape the text to ensure valid JSON
            textContent = textContent.replace("\"", "\\\""); // Escape double quotes

            // Step 2: Create a payload for OpenAI API
            String openAiEndpoint = "https://api.openai.com/v1/audio/speech";
            String requestBody = String.format(
                    "{\"model\": \"tts-1\", \"input\": \"%s\", \"voice\": \"alloy\"}", textContent
            );

            // Step 3: Call OpenAI API
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<String> entity = new HttpEntity<>(requestBody, createHeaders());
            ResponseEntity<byte[]> response = restTemplate.postForEntity(openAiEndpoint, entity, byte[].class);

            // Step 4: Return the audio file as a response
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=audio.mp3")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(response.getBody());
            } else {
                System.out.println("Error from OpenAI API: " + response.getStatusCode());
                return ResponseEntity.status(response.getStatusCode())
                        .body(("Error generating audio: " + response.getStatusCode()).getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(("Unexpected error: " + e.getMessage()).getBytes());
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + openAiApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

}


