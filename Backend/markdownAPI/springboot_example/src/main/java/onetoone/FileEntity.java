package onetoone;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userID;

    private String fileName;

    public FileEntity() {}

    public FileEntity(String fileName, Long userID)
    {
        this.fileName = fileName;
        this.userID = userID;
    }

}
