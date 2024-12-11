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
public class signTest {

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void testCreateUser_Successful() {
        // Prepare request body in the required format
        String requestBody = """
                {
                    "username" : "tests",
                    "email": "MoreTestsssss@example.com",
                    "password": "passwords"
                }
                """;


        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/user/create");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testCreateUser_UserAlreadyExists() {

        String requestBody = """
                {
                    "username" : "test",
                    "email": "test@example.com",
                    "password": "password"
                }
                """;


        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/user/create");

        assertEquals(200, response.getStatusCode());
        assertEquals("User with email already exists", response.jsonPath().getString("Response"));
    }

    @Test
    public void testCreateUser_Successful2() {
        // Prepare request body in the required format
        String requestBody = """
                {
                    "username" : "tests",
                    "email": "MoreTests@example.com",
                    "password": "passwords"
                }
                """;


        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/user/create");

        assertEquals(200, response.getStatusCode());
    }



}
