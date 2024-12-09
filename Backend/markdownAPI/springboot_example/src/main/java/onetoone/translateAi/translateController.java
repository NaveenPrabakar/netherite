package onetoone.translateAi;

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
import onetoone.translateAi.translateRepository;
import onetoone.translateAi.translateEntity;

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



@RestController
@RequestMapping("/translateText")
@Tag(name = "translateText API", description = "Done By Yi Yun Khor")
public class translateController {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    //need to connect to the sign entity
    @Autowired
    private signRepository sign;

    //this is connected to the repository to use the method inside for getting valuye in the speech totext entity
    //and really store it
    @Autowired
    private translateRepository api;

    //private final ObjectMapper objectMapper = new ObjectMapper();


    //global variable
    //String uploadDir2 = "upload_translate/";

    //post-CreateUser
    //front end passing parameter (email and file)
    //parameter need to have the user email in order to access to sign entity to find the userid
    //return

//    /**
//     * Creates a new entry for the user in the translate text table with the uploaded file.
//     *
//     * @param email the user's email to identify or create a user entry.
//     * @param file  the audio file (MP3 or WAV) to be uploaded and processed.
//     * @return ResponseEntity with a success message if the file is uploaded successfully,
//     * or an error message if the file type is invalid or if any exception occurs.
//     */
//    @Operation(summary = "Create translate User",
//            description = "Creates a new entry for the user in th table with the e trabslate uploaded text file.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successfully stored your text file"),
//            @ApiResponse(responseCode = "400", description = "Invalid file type. Only  md, txt, json, csv files are accepted.",
//                    content = @Content(schema = @Schema(implementation = String.class))),
//            @ApiResponse(responseCode = "404", description = "The user is not found",
//                    content = @Content(schema = @Schema(implementation = String.class))),
//            @ApiResponse(responseCode = "500", description = "Internal Server Error",
//                    content = @Content(schema = @Schema(implementation = String.class)))
//    })
//    @PostMapping("/createTranslateUser/{email}")
//    public ResponseEntity<String> createSpeechUser(@PathVariable String email, @RequestParam("text") MultipartFile file) {
//        try {
//            //need to go to signrepository to signentity to find username in order to find userid
//            signEntity temp = sign.findByEmail(email);
//
//            //check for if the user exist in the sign entity
//            //if the user not exist in the table that means user never use it before
//            if (temp == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The user is not found");
//            }
//
//            //get the file name
//            String fileName = file.getOriginalFilename();
//
//            // Validate file type
//            if (fileName == null || !fileName.toLowerCase().endsWith(".txt") && !fileName.toLowerCase().endsWith(".md")) {
//                return ResponseEntity.badRequest().body("Invalid file type. Only text files (.txt or .md) are accepted.");
//            }
//
//            //store the id value in the userID
//            Long userID = temp.getId();
//
//            //need to create the table entity
//            translateEntity sm = new translateEntity(userID, fileName);
//
//            //need to save it
//            api.save(sm);
//
//            //need the extension from actual file name and type of tht file
//            String extension = fileName.substring(fileName.lastIndexOf("."));
//            String fileType = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase() + '-';
//
//            //create the path with both of the info (file type and file name)
//            Path tempFile = Files.createTempFile(fileType, extension);
//
//            // define the main upload directory
//            //String uploadDir2 = "upload_Speech/";
//            File uploadDir2File = new File(uploadDir2);
//            if (!uploadDir2File.exists()) {
//                uploadDir2File.mkdirs();
//            }
//
//            // create subdirectory for the user ID inside the upload_speech folder
//            String userSubDirPath = uploadDir2 + userID + "/";
//            File userSubDir = new File(userSubDirPath);
//            if (!userSubDir.exists()) {
//                userSubDir.mkdirs();
//            }
//
//            // create the full path for saving the file inside the user ID directory
//            File savedSpeechFile = new File(userSubDirPath + fileName);
//            Files.copy(tempFile, savedSpeechFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//
//            //delete the path
//            Files.delete(tempFile);
//
//            return ResponseEntity.ok("Successfully stored your text file");
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cannot save your file");
//        }
//    }

    /**
     * Translate the uploaded text file using OpenAI's API.
     *
     * @param file   the text file (e.g., .txt or .md) to be translated.
     * @param prompt the user's prompt specifying the target language or translation details.
     * @return ResponseEntity with the translated text if successful,
     * or an error message if the file type is invalid or translation fails.
     */
    @Operation(summary = "Translate Text File",
            description = "Translates an uploaded text file using OpenAI's API.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Translation successful",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file type. Only text files (.txt or .md) are accepted.",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Failed to process the file.",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/translate")
    public ResponseEntity<String> translateFile(
            @RequestParam("prompt") String prompt,
            @RequestParam("text") String textContent) {

        try {

            // Call OpenAI API for translation
            String translationPrompt = prompt + "\n\n";
            String translatedText = callOpenAI(translationPrompt, textContent);

            if (translatedText != null) {
                return ResponseEntity.ok(translatedText);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to translate the text file.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process the file.");
        }
    }

    private String callOpenAI(String prompt, String content) {
        String openaiApiUrl = "https://api.openai.com/v1/chat/completions";
        String apiKey = openAiApiKey;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        // Construct request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", new JSONArray()
                .put(new JSONObject().put("role", "system").put("content", "You are a helpful assistant."))
                .put(new JSONObject().put("role", "user").put("content", prompt + "\n\n" + content))
        );
        requestBody.put("max_tokens", 2000);
        requestBody.put("temperature", 0.7);

        // Log request body
        System.out.println("Request Body: " + requestBody.toString());

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    openaiApiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Log response body
            System.out.println("Response Body: " + response.getBody());

            JSONObject responseBody = new JSONObject(response.getBody());
            return responseBody.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}


