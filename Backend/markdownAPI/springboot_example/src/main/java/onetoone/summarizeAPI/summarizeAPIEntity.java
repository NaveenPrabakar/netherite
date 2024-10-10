package onetoone.summarizeAPI;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

@Entity
public class summarizeAPIEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long AIid;

    //foreign key to signEntity table
    private Long AIUserId;

    //date time
    private LocalDateTime eventDateTime;

    //amount using the ai api
    private int usageAPICount;


    public summarizeAPIEntity(Long AIUserId, LocalDateTime eventDateTime, int usageAPICount) {
        this.AIUserId = AIUserId;
        this.eventDateTime = eventDateTime;
        this.usageAPICount = usageAPICount;
    }

    public Long getAIid() {
        return AIid;
    }

    public void setAIid(Long AIid) {
        this.AIid = AIid;
    }

    public Long getAIId() {
        return AIUserId;
    }

    public void setAIUserId(Long AIUserId) {
        this.AIUserId = AIUserId;
    }

    public LocalDateTime getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(LocalDateTime eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public int getUsageAPICount() {
        return usageAPICount;
    }

    public void setUsageAPICount(int usageAPICount) {
        this.usageAPICount = usageAPICount;
    }
}

