package onetoone.Auto;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class AutoIndex {

    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyAXgDC5yw5RKrNo4tyXeCIfGGstAgzId4c";


    @PostMapping("/auto")
    public String callGeminiApi(@RequestParam("email") String email, @RequestParam("prompt") String prompt) {

        prompt = "Organize this (KEEP THE STRUCTURE, the {}, represent a folder, the [] represent files in the folder. You are not allowed to change any of the names. Move the files to folders that logically make sense. Return me only the output and the output should be in this format. Return as String json):" + prompt.replace("\"", "");
        try {

            String jsonPayload = """
                    {
                      "contents": [{
                        "parts": [{"text": "%s"}]
                      }]
                    }
                    """.formatted(prompt);


            HttpClient client = HttpClient.newHttpClient();


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();


            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


            if (response.statusCode() == 200) {

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.body());
                JsonNode textNode = rootNode.at("/candidates/0/content/parts/0/text");

                return textNode.asText();


            } else {
                System.err.println("Failed to fetch content. Status Code: " + response.statusCode());
                return null;
            }

        } catch (Exception e) {
            System.err.println("Error occurred while calling the Gemini API: " + e.getMessage());
            return null;
        }
    }
}

