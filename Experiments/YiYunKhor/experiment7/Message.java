package onetoone.ChatWebSocket;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.Data;

@Entity
@Table(name = "messages")
@Data
public class Message {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String sender;

    @Lob
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sent")
    private Date sent = new Date();

    public Message() {}

    public Message(String sender, String content, Date sent) {
        this.sender = sender;
        this.content = content;
        this.sent = sent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getSent() {
        return sent;
    }

    public void setSent(Date sent) {
        this.sent = sent;
    }

}
