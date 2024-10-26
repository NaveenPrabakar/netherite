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
import java.util.*;

@RestController
public class TesseractTest {

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Service is up!");
    }


    @PostMapping("/extractText")
    public ResponseEntity<String> extractText(@RequestParam("image") MultipartFile image) {
        // Initialize Tesseract instance
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
        tesseract.setLanguage("eng");
        tesseract.setConfigs(Arrays.asList("tessedit_char_whitelist=ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"));


        try {
            // Save the MultipartFile to a temporary file
            Path tempFile = Files.createTempFile("ocr-", ".png");
            image.transferTo(tempFile.toFile());

            // Use Tesseract to extract text
            String result = tesseract.doOCR(tempFile.toFile());

            // Clean up: delete the temporary file
            Files.delete(tempFile);

            return ResponseEntity.ok(result);

        } catch (IOException | TesseractException e) {
            return ResponseEntity.status(500).body("Error processing image: " + e.getMessage());
        }
    }
}


