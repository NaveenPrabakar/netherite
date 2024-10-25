package onetoone.Access;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import onetoone.signupAPI.signEntity;
import onetoone.signupAPI.signup;
import onetoone.editProfile.editRepository;
import onetoone.FileEntity;

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

    public AccessEntity(){

    }

    public AccessEntity(signEntity sign, FileEntity file, signEntity access){
        this.sign = sign;
        this.file = file;
        this.access = access;
    }
}