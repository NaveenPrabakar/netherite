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
public class Translatetest {

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void french(){
        String prompt = "Translate this text to french:";
        String textContent = "Hello, how are you?";
        String expectedTranslation = "Hola, ¿cómo estás?";

        Response response = RestAssured.given()
                .queryParam("prompt", prompt)
                .queryParam("text", textContent)
                .post("/translateText/translate");

        assertEquals(200, response.getStatusCode());
    }

}
