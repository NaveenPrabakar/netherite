package onetoone.translateAi;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
//import java.time.LocalDateTime;

@Entity
public class translateEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  translateId;

    //foreign key to signEntity table
    private Long translateUserId;

    //date time
   // private LocalDateTime eventDateTime;

    //speech filename
    private String translateFile;

    //amount using the speechai api
    //private int usageAPICount;

    // Default constructor
    public translateEntity() {
    }

    public translateEntity(Long translateUserId, String translateFile) {
        this.translateUserId = translateUserId;
        this.translateFile = translateFile;
    }

    public Long getTranslateId() {
        return translateId;
    }

    public void setTranslateId(Long translateId) {
        this.translateId = translateId;
    }

    public Long gettranslateUserId() {
        return translateUserId;
    }

    public void settranslateUserId(Long translateUserId) {
        translateUserId = translateUserId;
    }

//    public int getUsageAPICount() {
//        return usageAPICount;
//    }
//
//    public void setUsageAPICount(int usageAPICount) {
//        this.usageAPICount = usageAPICount;
//    }

    public String gettranslateFile() {
        return translateFile;
    }

    public void settranslateFile(String translateFile) {
        this.translateFile = translateFile;
    }
}

