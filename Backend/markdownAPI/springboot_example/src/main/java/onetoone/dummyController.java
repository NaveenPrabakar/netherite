package onetoone;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import onetoone.speechToTextAPI.*;
import onetoone.signupAPI.*;
import onetoone.ImageToText.*;
import onetoone.Access.*;

/**
 * To display the schemes
 */
@RestController
public class dummyController {

    @GetMapping("/api/dummy-file")
    public FileEntity getDummyFile() {
        return new FileEntity("example.txt", 123L);
    }

    @GetMapping("/api/dummy-json")
    public JsonEntity getDummyJson() {
        return new JsonEntity("example/path/to/file", 123L);
    }

    @GetMapping("/api/dummy-speech-user")
    public SpeechUserEntity getDummySpeechUser() {
        return new SpeechUserEntity(1L, "speechfile.wav");
    }

    @GetMapping("/api/dummy-image")
    public ImageEntity getDummyImage() {
        // Assuming you have an instance of signEntity ready for testing
        signEntity sign = new signEntity();  // Replace with actual object initialization
        return new ImageEntity(sign, "imagefile.jpg");
    }

    @GetMapping("/api/dummy-access")
    public AccessEntity getDummyAccess() {
        // You’ll need to instantiate the required entities to pass into the constructor
        signEntity sign = new signEntity();  // Replace with actual initialization
        signEntity access = new signEntity();  // Replace with actual initialization
        FileEntity file = new FileEntity("samplefile.txt", 123L);  // Replace with actual file entity

        return new AccessEntity(sign, file, access);
    }
}