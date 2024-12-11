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
                .param("fromUser", "takuli@iastate.edu")
                .param("toUser", "nvnprabakar@gmail.com")
                .param("docName", "yesTestOutNew")
                .when()
                .post("/share/new");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testShareFile_UserNotFound() {
        Response response = RestAssured.given()
                .param("fromUser", "nvnprabakar@gmail.com")
                .param("toUser", "nonexistesdasdnt@example.com")
                .param("docName", "testFile.txt")
                .when()
                .post("/share/new");

        assertEquals(400, response.getStatusCode());
    }
    @Test
    public void testShareFile_UserNotFound2() {
        Response response = RestAssured.given()
                .param("fromUser", "nvnprabakar@gmail.com")
                .param("toUser", "takuli@iastate.edu")
                .param("docName", "hello34")
                .when()
                .post("/share/new");

        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void testGetSentFiles_Success() {
        Response response = RestAssured.given()
                .param("email", "takuli@iastate.edu")
                .param("fileName", "yesTestOutNew")
                .when()
                .get("/share/sent");

        assertEquals(200, response.getStatusCode());
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
