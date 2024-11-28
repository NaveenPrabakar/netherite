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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class markTest {

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void uploadFileTest() {
        // Create request payload
        String requestBody = """
                {
                    "fileName": "test.txt",
                    "content": "Hello, this is a test file.",
                    "json": "{\"root\":[]}",
                    "email": "user@example.com",
                    "password": "password123"
                }
                """;

        // Send request and receive response
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/files/upload");

        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        // Check response body
        String returnString = response.getBody().asString();
        try {
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("File saved", returnObj.get("response"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void pullFileContentTest() {
        // Send request and receive response
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .param("email", "user@example.com")
                .param("password", "password123")
                .when()
                .get("/files/pull");

        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        // Check response body
        String returnString = response.getBody().asString();
        try {
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("Hello, this is a test file.", returnObj.get("content"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
