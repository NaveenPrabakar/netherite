package onetoone.ImageToText;

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
public class ImageEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private signEntity sign;

    private String fileName;

    public ImageEntity(){

    }

    public ImageEntity(signEntity sign, String fileName){
        this.sign = sign;
        this.fileName = fileName;
    }


}