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




@RestController
public class TesseractTest {

    @Autowired
    private loginRepository logs;

    @Autowired
    private ImageRepository im;

    private ArrayList<String> types = new ArrayList<>(Arrays.asList("jpeg", "jpg", "png", "gif"));

    /**
     * The following method works well for printed images
     *
     * @param image
     * @return response
     */

    @PostMapping("/extractText/{email}/{language}")
    public ResponseEntity<String> extractText(@RequestParam("image") MultipartFile image, @PathVariable String email, @PathVariable String language) {

        // Initialize Tesseract instance
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("/usr/share/tesseract/tessdata");

        tesseract.setLanguage(language.substring(0,3));
        tesseract.setPageSegMode(3);

        signEntity s = logs.findByEmail(email);


        try {
            String originalFilename = image.getOriginalFilename();

            if (originalFilename == null) {
                return ResponseEntity.badRequest().body("Invalid file name.");
            }


            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
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

    /**
     * Method to get a specific photo by filename.
     *
     * @param filename the name of the image file to retrieve
     * @return the image file as a Resource
     */
    @GetMapping("/getImage/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {

            String uploadDir = "uploaded_images/";
            File file = new File(uploadDir + filename);

            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            MediaType mediaType;
            String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();

            //added
            if(types.contains(extension)){
                return ResponseEntity.notFound().build();
            }

            //cases to check which file extension
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
                    mediaType = MediaType.APPLICATION_OCTET_STREAM; // Fallback for unknown types
                    break;
            }

            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok().contentType(mediaType).body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/getImageNamesByUser/{email}")
    public ResponseEntity<List<String>> getImageNamesByUser(@PathVariable String email) {

        signEntity user = logs.findByEmail(email);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<String> imageNames = im.findImageNamesByUser(user);

        return ResponseEntity.ok(imageNames);
    }
}


