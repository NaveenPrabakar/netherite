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
import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class accessTest {

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void testShareFile_Success() {
        Response response = RestAssured.given()
                .param("fromUser", "nvnprabakar@gmail.com")
                .param("toUser", "takuli@iastate.edu")
                .param("docName", "tester4.txt")
                .when()
                .post("/share/new");

        assertEquals(200, response.getStatusCode());
        assertEquals("The file was shared", response.jsonPath().getString("response"));
    }

    @Test
    public void testShareFile_UserNotFound() {
        Response response = RestAssured.given()
                .param("fromUser", "test@example.com")
                .param("toUser", "nonexistent@example.com")
                .param("docName", "testFile.txt")
                .when()
                .post("/share/new");

        assertEquals(200, response.getStatusCode());
        assertEquals("The user does not exist", response.jsonPath().getString("response"));
    }

    @Test
    public void testGetSentFiles_Success() {
        Response response = RestAssured.given()
                .param("email", "nvnprabakar@gmail.com")
                .param("fileName", "tester4.txt")
                .when()
                .get("/share/sent");

        assertEquals(400, response.getStatusCode());
        List<String> sentUsers = response.jsonPath().getList("response");
        assertEquals(1, sentUsers.size());
        assertEquals("takuli@iastate.edu", sentUsers.get(0));
    }

    @Test
    public void testGetFileNamesByAccessId_Success() {
        String email = "nvnprabakar@gmail.com";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .get("/share/filenames/" + email);

        assertEquals(200, response.getStatusCode());
        List<String> fileNames = response.jsonPath().getList("response");
        assertEquals(0, fileNames.size());

    }




}
