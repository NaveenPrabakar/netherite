package onetoone.ImageToText;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import onetoone.loginAPI.loginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import onetoone.signupAPI.signEntity;
import java.util.*;
import java.nio.file.StandardCopyOption;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;




@RestController
@Tag(name = "OCR API", description = "Done By Naveen Prabakar")
public class TesseractTest {

    @Autowired
    private loginRepository logs;

    @Autowired
    private ImageRepository im;

    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyAXgDC5yw5RKrNo4tyXeCIfGGstAgzId4c";
    private static final String prompt = "Convert this image to text and replicate the format best as possible";

    private ArrayList<String> types = new ArrayList<>(Arrays.asList("jpeg", "jpg", "png", "gif"));

    /**
     * The following method works well for printed images
     *
     * @param image
     * @return response
     */

    @Operation(
            summary = "Extract text from an image using Tesseract OCR",
            description = "Uploads an image and extracts text using Tesseract OCR. Returns the extracted text or error messages.",
            parameters = {
                    @Parameter(name = "email", description = "The email of the user", required = true),
                    @Parameter(name = "language", description = "The language for OCR (e.g., 'eng' for English)", required = true),
                    @Parameter(name = "image", description = "The image file for text extraction", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully extracted text from the image"),
            @ApiResponse(responseCode = "500", description = "Error processing the image")
    })
    @PostMapping("/extractText/{email}/{language}")
    public ResponseEntity<String> extractText(@RequestParam("image") MultipartFile image, @PathVariable String email, @PathVariable String language) {

        // Initialize Tesseract instance
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");

        tesseract.setLanguage(language.substring(0,3));
        tesseract.setPageSegMode(3);

        signEntity s = logs.findByEmail(email);


        try {
            String originalFilename = image.getOriginalFilename();

//            if (originalFilename == null) {
//                return ResponseEntity.badRequest().body("Invalid file name.");
//            }


            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            if(originalFilename.contains("jpg")){

                originalFilename = originalFilename.replace(".jpg", ".png");
                ImageEntity i = new ImageEntity (s, originalFilename);
                im.save(i);

                Path tempFile = Files.createTempFile("ocr-", extension);
                String uploadDir = "uploaded_images/";

                File uploadDirFile = new File(uploadDir);

                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdirs(); // Create the directory if it doesn't exist
                }

                File savedImageFile = new File(uploadDir + originalFilename);
                Files.copy(tempFile, savedImageFile.toPath(), StandardCopyOption.REPLACE_EXISTING);


                Files.delete(tempFile);


                return ResponseEntity.ok(callGeminiApiWithImage(image));
            }


            Path tempFile = Files.createTempFile("ocr-", extension);
            image.transferTo(tempFile.toFile());


            String result = tesseract.doOCR(tempFile.toFile());

            ImageEntity i = new ImageEntity (s, originalFilename);
            im.save(i);

            String uploadDir = "uploaded_images/";

            File uploadDirFile = new File(uploadDir);

            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs(); // Create the directory if it doesn't exist
            }

            File savedImageFile = new File(uploadDir + originalFilename);
            Files.copy(tempFile, savedImageFile.toPath(), StandardCopyOption.REPLACE_EXISTING);


            Files.delete(tempFile);

            System.out.println("Result: " + result);

            return ResponseEntity.ok(result);

        } catch (IOException | TesseractException e) {
            return ResponseEntity.status(500).body("Error processing image: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Retrieve an image by filename",
            description = "Retrieves an image file by its filename and returns it as a response.",
            parameters = {
                    @Parameter(name = "filename", description = "The name of the image file", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image successfully retrieved"),
            @ApiResponse(responseCode = "500", description = "Error retrieving image")
    })
    @GetMapping("/getImage/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            String uploadDir = "uploaded_images/";  // Folder where images are saved
            File file = new File(uploadDir + filename);  // Use the filename passed in the URL


            // Get the extension and set the appropriate media type
            MediaType mediaType;
            String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();


            // Determine content type based on the extension
            switch (extension) {
                case "jpg":
                case "jpeg":
                    mediaType = MediaType.IMAGE_JPEG;
                    break;
                case "png":
                    mediaType = MediaType.IMAGE_PNG;
                    break;
                case "gif":
                    mediaType = MediaType.IMAGE_GIF;
                    break;
                default:
                    mediaType = MediaType.APPLICATION_OCTET_STREAM;
                    break;
            }

            // Serve the image file as a resource
            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok().contentType(mediaType).body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);  // Return 500 if there is an internal server error
        }
    }


    @Operation(
            summary = "Retrieve image filenames associated with a user",
            description = "Returns a list of image filenames that are associated with the specified user by email.",
            parameters = {
                    @Parameter(name = "email", description = "The email of the user", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of image filenames retrieved"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/getImageNamesByUser/{email}")
    public ResponseEntity<List<String>> getImageNamesByUser(@PathVariable String email) {

        signEntity user = logs.findByEmail(email);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<String> imageNames = im.findImageNamesByUser(user);

        return ResponseEntity.ok(imageNames);
    }

    private String callGeminiApiWithImage(MultipartFile image) {
        try {
            // Convert the image to base64
            byte[] imageBytes = image.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // Prepare the JSON payload
            String jsonPayload = String.format("""
                {
                    "contents": [{
                        "parts": [
                            {"text": "%s"},
                            {
                                "inline_data": {
                                    "mime_type": "image/jpeg",
                                    "data": "%s"
                                }
                            }
                        ]
                    }]
                }
                """, prompt, base64Image);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.body());
                JsonNode textNode = rootNode.at("/candidates/0/content/parts/0/text");

                return textNode.asText();

            } else {
                return "Error: Failed to fetch content. Status Code: " + response.statusCode();
            }

        } catch (Exception e) {
            return "Error occurred: " + e.getMessage();
        }
    }
}


