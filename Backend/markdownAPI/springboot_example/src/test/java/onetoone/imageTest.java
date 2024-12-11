package onetoone;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class imageTest {

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void testExtractText_Success() {
        // Mock image file for testing
        File testImage = new File("C:/Users/nvnpr/Downloads/IMG_0572.jpg");

        Response response = RestAssured.given()
                .multiPart("image", testImage)
                .pathParam("email", "nvnprabakar@gmail.com")
                .pathParam("language", "eng")
                .when()
                .post("/extractText/{email}/{language}");

        assertEquals(200, response.getStatusCode());
        // Assert the response body contains expected text
        // Update this to the expected text from the image
        String extractedText = response.getBody().asString();
        System.out.println("Extracted Text: " + extractedText);
    }

    @Test
    public void testGetImage_Success() {
        Response response = RestAssured.given()
                .pathParam("filename", "Screenshot 2024-12-03 111745.png")
                .when()
                .get("/getImage/{filename}");

        assertEquals(200, response.getStatusCode());
        assertEquals("image/png", response.getHeader("Content-Type"));
    }

    @Test
    public void testGetImageNamesByUser_Success() {
        Response response = RestAssured.given()
                .pathParam("email", "nvnprabakar@gmail.com")
                .when()
                .get("/getImageNamesByUser/{email}");

        assertEquals(200, response.getStatusCode());
        // Assert the response contains expected filenames
        System.out.println("Image Names: " + response.jsonPath().getList("$"));
    }

    @Test
    public void testExtractText_Success2() {
        // Mock image file for testing
        File testImage = new File("C:/Users/nvnpr/OneDrive/Pictures/Screenshots/Screenshot 2024-12-10 203113.png");

        Response response = RestAssured.given()
                .multiPart("image", testImage)
                .pathParam("email", "nvnprabakar@gmail.com")
                .pathParam("language", "eng")
                .when()
                .post("/extractText/{email}/{language}");

        assertEquals(200, response.getStatusCode());
        // Assert the response body contains expected text
        // Update this to the expected text from the image
        String extractedText = response.getBody().asString();
        System.out.println("Extracted Text: " + extractedText);
    }
}

