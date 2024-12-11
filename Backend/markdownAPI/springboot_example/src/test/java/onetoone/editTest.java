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
public class editTest {

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void testChangePassword_Success() {
        String requestBody = """
            {
                "email": "nvnprabakar@gmail.com",
                "password": "def"
            }
            """;
        String newPassword = "def";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .put("/edit/changepassword/" + newPassword);

        assertEquals(200, response.getStatusCode());
        assertEquals("Your password is changed", response.jsonPath().getString("response"));
    }

    @Test
    public void testChangeEmail_Success() {
        String requestBody = """
            {
                "email": "nvnprabakar@gmail.com",
                "password": "def"
            }
            """;
        String newEmail = "Nvnprabakar@gmail.com";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .put("/edit/changeemail/" + newEmail);

        assertEquals(200, response.getStatusCode());
        assertEquals("Your Email has been updated", response.jsonPath().getString("response"));
    }

    @Test
    public void testChangeUsername_Success() {
        String requestBody = """
            {
                "email": "Nvnprabakar@gmail.com",
                "password": "def"
            }
            """;

        String newUsername = "newUsername123";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .put("/edit/changeusername/" + newUsername);

        assertEquals(200, response.getStatusCode());
        assertEquals("username has been updated", response.jsonPath().getString("response"));
    }


    @Test
    public void testExterminateUser_Success() {

        // Setup request parameters
        String email = "nvnprabakar@gmail.com";
        String password = "defg";

        // Perform DELETE request
        Response response = RestAssured.given()
                .queryParam("email", email)
                .queryParam("password", password)
                .when()
                .delete("/edit/exterminateUser");

        // Validate the response
        assertEquals(200, response.getStatusCode());
        assertEquals("Your password is wrong", response.jsonPath().getString("response"));
    }

    @Test
    public void testExterminateUser_UserNotFound() {

        // Setup request parameters
        String email = "nonexisasdasdasdastent@example.com";
        String password = "someddfdpassword";

        // Perform DELETE request
        Response response = RestAssured.given()
                .queryParam("email", email)
                .queryParam("password", password)
                .when()
                .delete("/edit/exterminateUser");

        // Validate the response
        assertEquals(200, response.getStatusCode());
        assertEquals("Email is wrong", response.jsonPath().getString("response"));
    }

    @Test
    public void testChangeUsername_Fail() {
        String requestBody = """
            {
                "email": "asaasaaprabakar@gmail.com",
                "password": "def"
            }
            """;

        String newUsername = "newUsername123";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .put("/edit/changeusername/" + newUsername);

        assertEquals(200, response.getStatusCode());
        assertEquals("user profile does not exist", response.jsonPath().getString("response"));
    }

    @Test
    public void testChangeUsername_Fail2() {
        String requestBody = """
            {
                "email": "nvnprabakar@gmail.com",
                "password": "defsdasdsadas"
            }
            """;

        String newUsername = "newUsername123";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .put("/edit/changeusername/" + newUsername);

        assertEquals(200, response.getStatusCode());
        assertEquals("password is incorrect", response.jsonPath().getString("response"));
    }

    @Test
    public void testChangeUsername_Fail3() {
        String requestBody = """
            {
                "email": "nvnprabakar@gmail.com",
                "password": "defsdasdsadas"
            }
            """;

        String newUsername = "newUsername123";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .put("/edit/changepassword/" + newUsername);

        assertEquals(200, response.getStatusCode());
        assertEquals("Your password is wrong", response.jsonPath().getString("response"));
    }

    @Test
    public void testChangeUsername_Fail4() {
        String requestBody = """
            {
                "email": "sas@gmail.com",
                "password": "defsdasdsadas"
            }
            """;

        String newUsername = "newUsername123";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .put("/edit/changepassword/" + newUsername);

        assertEquals(200, response.getStatusCode());
        assertEquals("your email or password is wrong", response.jsonPath().getString("response"));
    }

    @Test
    public void testChangeUsername_Fail5() {
        String requestBody = """
            {
                "email": "sas@gmail.com",
                "password": "defsdasdsadas"
            }
            """;

        String newUsername = "newUsername123";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .put("/edit/changeemail/" + newUsername);

        assertEquals(200, response.getStatusCode());
        assertEquals("Your email or password is wrong", response.jsonPath().getString("response"));
    }

    @Test
    public void testChangeUsername_Fail6() {
        String requestBody = """
            {
                "email": "nvnprabakar@gmail.com",
                "password": "defsdasdsadas"
            }
            """;

        String newUsername = "newUsername123";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .put("/edit/changeemail/" + newUsername);

        assertEquals(200, response.getStatusCode());
        assertEquals("Your password is wrong", response.jsonPath().getString("response"));
    }

    @Test
    public void testExterminateUser_UserNotFound2() {

        // Setup request parameters
        String email = "MoreTASASsestsssss@example.com";
        String password = "passwords";

        // Perform DELETE request
        Response response = RestAssured.given()
                .queryParam("email", email)
                .queryParam("password", password)
                .when()
                .delete("/edit/exterminateUser");

        // Validate the response
        assertEquals(200, response.getStatusCode());
    }





}
