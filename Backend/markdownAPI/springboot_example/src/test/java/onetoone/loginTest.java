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

import java.io.File;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class loginTest {

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void findEmail_Successful() {
        // Prepare request body in the required format
        String requestBody = """
                {
                    "email": "MoreTests@example.com",
                    "password": "passwords"
                }
                """;

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/userLogin/searchemail");

        //assertEquals(200, response.getStatusCode());
        assertEquals("ok", response.jsonPath().getString("response"));
    }

    @Test
    public void findEmail_PasswordFailed() {
        // Prepare request body in the required format
        String requestBody = """
                {
                    "email": "MoreTests@example.com",
                    "password": "jkjghgh"
                }
                """;

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/userLogin/searchemail");

        //assertEquals(200, response.getStatusCode());
        assertEquals("Password is incorrect", response.jsonPath().getString("response"));
    }

    @Test
    public void findEmail_NotFoundFailed() {
        // Prepare request body in the required format
        String requestBody = """
                    {
                        "email": "MoreTe@example.com",
                        "password": "jkjghgh"
                    }
                    """;

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/userLogin/searchemail");

        //assertEquals(404, response.getStatusCode());
        assertEquals("User Profile does not exist", response.jsonPath().getString("response"));
    }

    @Test
    public void resetPassword_SuccessfulReset() {
        String requestBody = """
            {
                "email": "MoreTests@example.com",
                "password": "newpassword123"
            }
            """;

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .put("/userLogin/resetPassword");

        //assertEquals(200, response.getStatusCode());
        assertEquals("Password has been reset successfully.", response.jsonPath().getString("responses"));
    }
}