package onetoone.speechToTextAPI;

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
public class SpeechUserEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  speechId;

    //foreign key to signEntity table
    private Long speechUserId;

    //date time
   // private LocalDateTime eventDateTime;

    //speech filename
    private String speechFile;

    //amount using the speechai api
    //private int usageAPICount;

    // Default constructor
    public SpeechUserEntity() {
    }

    public SpeechUserEntity(Long speechUserId, String speechFile) {
        this.speechUserId = speechUserId;
        this.speechFile = speechFile;
    }

    public Long getSpeechId() {
        return speechId;
    }

    public void setSpeechId(Long speechId) {
        this.speechId = speechId;
    }

    public Long getSpeechUserId() {
        return speechUserId;
    }

    public void setSpeechUserId(Long speechUserId) {
        speechUserId = speechUserId;
    }

//    public int getUsageAPICount() {
//        return usageAPICount;
//    }
//
//    public void setUsageAPICount(int usageAPICount) {
//        this.usageAPICount = usageAPICount;
//    }

    public String getSpeechFile() {
        return speechFile;
    }

    public void setSpeechFile(String speechFile) {
        this.speechFile = speechFile;
    }
}

