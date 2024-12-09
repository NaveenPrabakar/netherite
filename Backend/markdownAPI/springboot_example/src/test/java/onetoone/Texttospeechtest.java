package onetoone;

import static org.junit.jupiter.api.Assertions.assertEquals;

import onetoone.textToSpeech.Text;
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
import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class Texttospeechtest {

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void convert(){
        String inputText = "This is a test.";
        Text requestBody = new Text(inputText);

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .post("/textToSpeech/synthesizer");

        assertEquals(200, response.getStatusCode());
    }
}
