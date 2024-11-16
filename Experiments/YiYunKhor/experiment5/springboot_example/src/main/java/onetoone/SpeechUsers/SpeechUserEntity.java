package onetoone.SpeechUsers;

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
    private Long SpeechId;

    //foreign key to signEntity table
    private Long SpeechUserId;

    //date time
   // private LocalDateTime eventDateTime;

    //speech filename
    private String speechFile;

    //amount using the speechai api
    //private int usageAPICount;

    // Default constructor
    public SpeechUserEntity() {
    }

    public SpeechUserEntity(Long SpeechUserId, String speechFile) {
        this.SpeechUserId = SpeechUserId;
        this.speechFile = speechFile;
    }

    public Long getSpeechId() {
        return SpeechId;
    }

    public void setSpeechId(Long speechId) {
        SpeechId = speechId;
    }

    public Long getSpeechUserId() {
        return SpeechUserId;
    }

    public void setSpeechUserId(Long speechUserId) {
        SpeechUserId = speechUserId;
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

