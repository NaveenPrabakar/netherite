package onetoone;

import static org.junit.jupiter.api.Assertions.assertEquals;

import onetoone.speechToTextAPI.SpeechUserEntity;
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
        File testSpeech = new File("C:/Users/nvnpr/Downloads/audio.mp3");

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
        File testSpeech = new File("C:/Users/nvnpr/OneDrive/Pictures/Screenshots/Screenshot 2024-12-09 014707.png");

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
        File testSpeech = new File("C:/Users/nvnpr/Downloads/audio.mp3");
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
        File testSpeech = new File("C:/Users/nvnpr/OneDrive/Pictures/Screenshots/Screenshot 2024-12-09 014707.png");
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
        File testSpeech = new File("C:/Users/nvnpr/Downloads/audio.mp3");
//        String email = "asiandeady5@gmail.com";

        Response response = RestAssured.given()
                .header("Content-Type", "multipart/form-data")
                .pathParam("email", "asbakar@gmail.com")
                .multiPart("audio", testSpeech)
                .when()
                .post("/SpeechToTextAIuse/createSpeechUser/{email}");

        assertEquals(404, response.getStatusCode());
        assertEquals("The user is not found", response.getBody().asString());
//        String extractedText = response.getBody().asString();
//        System.out.println("Extracted Text: " + extractedText);
        //assertEquals("You have successfully registered", response.jsonPath().getString("Response"));
    }

    @Test
    public void testCreateSpeechUser_unSuccessful2() {

        Response response = RestAssured.given()
                .param("email", "takuli@iastate.edu")
                .param("speechFile", "New Recording 18.m4a")
                .when()
                .get("/SpeechToTextAIuse/getSpeechFile");

        assertEquals(404, response.getStatusCode());
    }

    @Test
    public void testCreateSpeechUser_unSuccessful3() {

        long t = 1;

        SpeechUserEntity s = new SpeechUserEntity(t, "hello");
        s.setSpeechId(t);
        s.setSpeechUserId(t);
        s.setSpeechFile("hello");

        assertEquals(1, s.getSpeechId());
        assertEquals(1, s.getSpeechUserId());
        assertEquals("hello", s.getSpeechFile());
    }


}


