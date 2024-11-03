package onetoone;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import onetoone.Access.*;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(mappedBy = "files", cascade = CascadeType.ALL)
    private Set<AccessEntity> accessEntities = new HashSet<>();

    private Long userID;

    private String fileName;

    public FileEntity() {}

    public FileEntity(String fileName, Long userID)
    {
        this.fileName = fileName;
        this.userID = userID;
    }

    public Long getId(){
        return userID;
    }

    public Long getfileId(){
        return id;
    }

    public Set<AccessEntity> getAccessEntities() {
        return accessEntities;
    }

    public void addAccessEntity(AccessEntity accessEntity) {
        this.accessEntities.add(accessEntity);
        accessEntity.getFiles().add(this);
    }

}
