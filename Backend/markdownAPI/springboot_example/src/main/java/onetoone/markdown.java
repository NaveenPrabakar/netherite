package onetoone;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.StandardCopyOption;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;

@RestController
@RequestMapping("/files")
public class markdown {

    //Main directory for the files
    private final Path location = Paths.get("uploads");

    @Autowired
    private FileRepository fileRepository;

    @PostMapping("/upload")
    public ResponseEntity<String> store(@RequestParam("file") MultipartFile file) {
        try {

            if (!Files.exists(location)) {//Creates Directory if it doesn't exist
                Files.createDirectory(location);
            }
            String fileName = file.getOriginalFilename();
            Path filePath = location.resolve(fileName);
            //Copies the contents of the file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            FileEntity fileEntity = new FileEntity(fileName);
            fileRepository.save(fileEntity);

        } catch (IOException e) {
            System.out.println("Could Not save the file");
        }

        return ResponseEntity.ok("File uploaded successfully");

    }

    @GetMapping("/{name}")
    public ResponseEntity<Resource> store(@PathVariable String name) {
        FileEntity fileEntity = fileRepository.findByFileName(name);

        if (fileEntity == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path filePath = location.resolve(name);
            Resource resource = new FileSystemResource(filePath.toFile());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"");

            return ResponseEntity.ok().headers(headers).body(resource);
            //Returns in JSON format

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/update/{name}")
    public ResponseEntity<String> updateFile(@PathVariable String name, @RequestParam("file") MultipartFile file) {
        FileEntity fileEntity = fileRepository.findByFileName(name);

        if (fileEntity == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path filePath = location.resolve(name);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            fileRepository.save(fileEntity);

            return ResponseEntity.ok("File updated successfully");
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("delete/{name}")
    public ResponseEntity<String> deleteFile(@PathVariable String name){
        FileEntity files = fileRepository.findByFileName(name);

        if(files == null){
            return ResponseEntity.notFound().build();
        }

        try{
            Path filePath = location.resolve(name);

            Files.delete(filePath);

            fileRepository.deleteByFileName(name);

            return ResponseEntity.ok("File has been deleted");
        } catch (IOException e){
            return ResponseEntity.status(500).build();
        }
    }
}