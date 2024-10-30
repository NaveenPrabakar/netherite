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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Arrays;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SpeechToTextController{

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${openai.api.url}")
    private String openAiApiUrl;


    private final ObjectMapper objectMapper = new ObjectMapper();

    //for the files format
    private static final String[] ALLOWED_AUDIO_EXTENSIONS = {
            "mp3", "mp4", "mpeg", "mpga", "m4a", "wav", "webm"
    };

    // This parameter accepts a file input from the user, specifically an MP3 file in this case.
    @GetMapping("/transcribe")
    public Map<String, String> transcribeAudio(@RequestParam("file") MultipartFile file) throws IOException {

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
                JsonNode jsonResponse = objectMapper.readTree(response.getEntity().getContent());
                transcription = jsonResponse.path("text").asText();
            }
        } finally {
            // Clean up the temporary file
            tempFile.delete();
        }


       responseMap.put("transcription", transcription);
        return responseMap;

        //return ResponseEntity.ok(responseMap);
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
