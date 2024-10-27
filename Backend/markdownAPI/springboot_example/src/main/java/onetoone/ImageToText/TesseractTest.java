package onetoone.ImageToText;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import onetoone.loginAPI.loginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import onetoone.signupAPI.signEntity;
import java.util.*;
import java.nio.file.StandardCopyOption;


@RestController
public class TesseractTest {

    @Autowired
    private loginRepository logs;

    @Autowired
    private ImageRepository im;

    /**
     * The following method works well for printed images
     *
     * @param image
     * @return response
     */

    @PostMapping("/extractText")
    public ResponseEntity<String> extractText(@RequestParam("email") String email, @RequestParam("image") MultipartFile image) {
        // Initialize Tesseract instance
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
        tesseract.setLanguage("eng");
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

            return ResponseEntity.ok(result);

        } catch (IOException | TesseractException e) {
            return ResponseEntity.status(500).body("Error processing image: " + e.getMessage());
        }
    }
}


