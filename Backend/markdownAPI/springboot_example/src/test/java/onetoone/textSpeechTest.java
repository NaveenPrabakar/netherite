package onetoone;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort; // Spring Boot 3
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class textSpeechTest {

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void testExtractText_Success() {
        // Mock speech file for testing
        File testSpeech = new File("/Users/annabelle/Desktop/New Recording 20.m4a");

        Response response = RestAssured.given()
                .header("Content-Type", "multipart/form-data")
                .multiPart("audio", testSpeech)
                .when()
                .post("/SpeechToTextAIuse/transcribe2");

        assertEquals(200, response.getStatusCode());
        String extractedText = response.getBody().asString();
        System.out.println("Extracted Text: " + extractedText);
    }

    @Test
    public void testExtractTextWrongFileType_Failed() {
        // Mock speech file for testing
        File testSpeech = new File("/Users/annabelle/Desktop/Screenshot 2024-11-17 at 1.18.18 AM.png");

        Response response = RestAssured.given()
                .header("Content-Type", "multipart/form-data")
                .multiPart("audio", testSpeech)
                .when()
                .post("/SpeechToTextAIuse/transcribe2");

        assertEquals(400, response.getStatusCode());
        //assertEquals("Invalid file type. Only MP3, MP4, MPEG, MPGA, M4A, WAV, and WEBM files are accepted.", response.jsonPath().getString("Response"));
        assertEquals("Invalid file type. Only MP3, MP4, MPEG, MPGA, M4A, WAV, and WEBM files are accepted.", response.getBody().asString());
//        String extractedText = response.getBody().asString();
//        System.out.println("Extracted Text: " + extractedText);
    }

    @Test
    public void testCreateSpeechUser_Successful() {

        // Mock speech file for testing
        File testSpeech = new File("/Users/annabelle/Desktop/New Recording 20.m4a");
//        String email = "asiandeady5@gmail.com";

        Response response = RestAssured.given()
                .header("Content-Type", "multipart/form-data")
                .pathParam("email", "asiandeady5@gmail.com")
                .multiPart("audio", testSpeech)
                .when()
                .post("/SpeechToTextAIuse/createSpeechUser/{email}");

        assertEquals(200, response.getStatusCode());
        assertEquals("Successfully stored your speech file", response.getBody().asString());
        //assertEquals("You have successfully registered", response.jsonPath().getString("Response"));
    }

    @Test
    public void testCreateWrongFileType_Failed() {

        // Mock speech file for testing
        File testSpeech = new File("/Users/annabelle/Desktop/Screenshot 2024-11-17 at 1.18.18 AM.png");
//        String email = "asiandeady5@gmail.com";

        Response response = RestAssured.given()
                .header("Content-Type", "multipart/form-data")
                .pathParam("email", "asiandeady5@gmail.com")
                .multiPart("audio", testSpeech)
                .when()
                .post("/SpeechToTextAIuse/createSpeechUser/{email}");

        assertEquals(400, response.getStatusCode());
//        assertEquals("Invalid file type. Only MP3, MP4, MPEG, MPGA, M4A, WAV, and WEBM files are accepted.", response.getBody().asString());
//        String extractedText = response.getBody().asString();
//        System.out.println("Extracted Text: " + extractedText);
        //assertEquals("You have successfully registered", response.jsonPath().getString("Response"));
    }

    @Test
    public void testCreateNonSpeechUser_Fail() {

        // Mock speech file for testing
        File testSpeech = new File("/Users/annabelle/Desktop/New Recording 20.m4a");
//        String email = "asiandeady5@gmail.com";

        Response response = RestAssured.given()
                .header("Content-Type", "multipart/form-data")
                .pathParam("email", "as@gmail.com")
                .multiPart("audio", testSpeech)
                .when()
                .post("/SpeechToTextAIuse/createSpeechUser/{email}");

        assertEquals(404, response.getStatusCode());
        assertEquals("The user is not found", response.getBody().asString());
//        String extractedText = response.getBody().asString();
//        System.out.println("Extracted Text: " + extractedText);
        //assertEquals("You have successfully registered", response.jsonPath().getString("Response"));
    }




}


