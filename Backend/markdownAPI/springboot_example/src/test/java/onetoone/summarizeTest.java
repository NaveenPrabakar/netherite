package onetoone;

import static org.junit.jupiter.api.Assertions.assertEquals;

import onetoone.summarizeAPI.AI;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class summarizeTest {

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void getUsageAPICount_SuccessWithValidEmail() {
        String email = "asiandeady5@gmail.com";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .pathParam("email", email)
                .when()
                .get("/OpenAIAPIuse/getUsageAPICount/{email}");

        //assertEquals(200, response.getStatusCode());
        assertEquals("5",response.jsonPath().getString("reply"));
        //assertEquals("10", response.jsonPath().getString("reply"));
    }

    @Test
    public void getUsageAPICount_FailureWithInvalidEmail() {
        String email = "nonexistentuser@example.com";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .pathParam("email", email)
                .when()
                .get("/OpenAIAPIuse/getUsageAPICount/{email}");

        //assertEquals(404, response.getStatusCode());
        assertEquals("-1", response.jsonPath().getString("reply"));
        String extractedText = response.getBody().asString();
        System.out.println("Extracted Text: " + extractedText);
    }

    @Test
    public void gpt(){
        String email = "nvnprabakar@gmail.com";
        String prompt = "Summarize this content";
        String content = "This is the content to be summarized.";

        AI requestBody = new AI(email, prompt, content);

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .post("/OpenAIAPIuse/createAIUser");

        assertEquals(200, response.getStatusCode());
    }
}
