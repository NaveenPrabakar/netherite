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

    @GetMapping("/transcribe")
    public ResponseEntity<Map<String, String>> transcribeAudio(@RequestParam("file") MultipartFile file) throws IOException {

        // Save the MP3 file locally
        File tempFile = File.createTempFile("audio", ".m4a");
        file.transferTo(tempFile);

        // Transcribe the audio using Whisper API
        String transcription;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost uploadFile = new HttpPost(openAiApiUrl);
            uploadFile.setHeader("Authorization", "Bearer " + openAiApiKey);

            // Create multipart request with the MP3 file
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

        // Prepare the JSON response
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("transcription", transcription);

        return ResponseEntity.ok(responseMap);
    }
}
