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
        // Send request and receive response with query parameters
        Response response = RestAssured.given()
                .queryParam("fileName", "test.txt")
                .queryParam("content", "Hello, this is a test file.")
                .queryParam("json", "{\"root\":[]}")
                .queryParam("email", "nvnprabakar@gmail.com")
                .queryParam("password", "defg")
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
                .param("email", "takuli@iastate.edu")
                .param("password", "admin123!")
                .param("fileName", "yes")
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

    @Test
    public void testPulled_Success() {

        // Act
        Response response = RestAssured.given()
                .queryParam("email", "test@example.com")
                .queryParam("fileName", "test.txt")
                .when()
                .get("/files/fileid");

        // Assert
        assertEquals(200, response.getStatusCode());
        assertEquals("30", response.getBody().asString());
    }

    @Test
    public void testSystem_Success() {


        Response response = RestAssured.given()
                .queryParam("email", "nvnprabakar@gmail.com")
                .queryParam("password", "defg")
                .when()
                .get("/files/system");


        assertEquals(200, response.getStatusCode());
        assertEquals("{\"root\":[]}", response.getBody().asString());
    }

    @Test
    public void testUpdates_Success() {

        Response response = RestAssured.given()
                .queryParam("email", "nvnprabakar@gmail.com")
                .queryParam("json", "{\"root\":[]}")
                .when()
                .put("/files/update");

        assertEquals(200, response.getStatusCode());
        assertEquals("Update is done", response.getBody().asString());
    }

    @Test
    public void testDeleteFile_FileDoesNotExist() {

        Response response = RestAssured.given()
                .queryParam("email", "nvnprabakar@gmail.com")
                .queryParam("fileName", "test.txt")
                .queryParam("json", "{\"root\":[\"yo what's up\"]}")
                .when()
                .delete("/files/deleteFile");


        assertEquals(200, response.getStatusCode());
        assertEquals("the file was deleted", response.jsonPath().getString("response"));
    }



}
