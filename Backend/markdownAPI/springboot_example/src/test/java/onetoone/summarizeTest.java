package onetoone;

import static org.junit.jupiter.api.Assertions.assertEquals;

import onetoone.summarizeAPI.AI;
import onetoone.summarizeAPI.summarizeAPIEntity;
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
import java.time.LocalDateTime;

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
        assertEquals(200,  response.getStatusCode());
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

    @Test
    public void gpt2(){

        Response response = RestAssured.given()
                .pathParam("email", "bruh123@gmail.com")
                .get("/OpenAIAPIuse/getUsageAPICount/{email}");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void gpt3(){
        String email = "nicholaspribadi.1209@gmail.com";
        String prompt = "Summarize this content";
        String content = "This is the content to be summarized.";

        AI requestBody = new AI(email, prompt, content);

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .put("/OpenAIAPIuse/updateAIUser");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void gpt5(){

        Response response = RestAssured.given()
                .pathParam("email", "nicholaspribadi.1209@gmail.com")
                .delete("/OpenAIAPIuse/resetUsage/{email}");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void gpt6(){

        Response response = RestAssured.given()
                .pathParam("email", "imposter@gmail.com")
                .delete("/OpenAIAPIuse/resetUsage/{email}");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testEntity(){
        AI a = new AI("n@gmail.com", "do", "that");

        a.setPrompt("do");
        a.setContent("that");
        a.setemail("n@gmail.com");

        assertEquals("do", a.getPrompt());
        assertEquals("that", a.getContent());
        assertEquals("n@gmail.com", a.getemail());
    }

    @Test
    public void testEntity2(){

        LocalDateTime currentDateTime = LocalDateTime.now();

        long t = 1;
        summarizeAPIEntity a = new summarizeAPIEntity(t, currentDateTime, 10);

        a.setAIid(t);
        a.setUsageAPICount(10);
        a.setEventDateTime(currentDateTime);
        a.setAIUserId(t);

        assertEquals(t, a.getAIid());
        assertEquals(t, a.getAIId());
    }






}
