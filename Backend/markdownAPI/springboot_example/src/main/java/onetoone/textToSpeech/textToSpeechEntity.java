package onetoone.textToSpeech;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Column;


@Entity
public class textToSpeechEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long textToSpeechId;

    //foreign key to signEntity table
    private Long textToSpeechUserId;


    //text filename
    private String textToSpeechFile;

    // Default constructor
    public textToSpeechEntity() {
    }

    public textToSpeechEntity(Long textToSpeechUserId, String textToSpeechFile) {
        this.textToSpeechUserId = textToSpeechUserId;
        this.textToSpeechFile = textToSpeechFile;
    }

    public Long getTextToSpeechId() {
        return textToSpeechId;
    }

    public void setTextToSpeechId(Long textToSpeechId) {
        this.textToSpeechId = textToSpeechId;
    }

    public Long getTextToSpeechUserId() {
        return textToSpeechUserId;
    }

    public void setTextToSpeechUserId(Long textToSpeechUserId) {
        this.textToSpeechUserId = textToSpeechUserId;
    }


    public String getTextToSpeechFile() {
        return textToSpeechFile;
    }

    public void setTextToSpeechFile(String textToSpeechFile) {
        this.textToSpeechFile = textToSpeechFile;
    }
}

