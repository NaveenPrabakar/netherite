package onetoone.Access;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinColumn;
import onetoone.signupAPI.signEntity;
import onetoone.signupAPI.signup;
import onetoone.editProfile.editRepository;
import onetoone.FileEntity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import java.util.Set;
import java.util.*;

@Entity
public class AccessEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private signEntity sign;

    @ManyToOne
    @JoinColumn(name = "file_id", referencedColumnName = "id")
    private FileEntity file;

    @ManyToOne
    @JoinColumn(name = "access_id", referencedColumnName = "id")
    private signEntity access;

    @ManyToMany
    @JoinTable(
            name = "file_access", // Name of the join table
            joinColumns = @JoinColumn(name = "access_id", referencedColumnName = "access_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id", referencedColumnName = "id")
    )

    private Set<FileEntity> files = new HashSet<>();

    public AccessEntity(){

    }

    public AccessEntity(signEntity sign, FileEntity file, signEntity access){
        this.sign = sign;
        this.access = access;
        this.file = file;
        this.files.add(file);
    }

    public Set<FileEntity> getFiles() {
        return files;
    }
}