package onetoone.Recent;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import onetoone.FileEntity;
import onetoone.signupAPI.*;
import java.time.LocalDateTime;


@Entity
public class RecentActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recent_id", referencedColumnName = "id")
    private FileEntity file;

    @ManyToOne
    @JoinColumn(name = "used_id", referencedColumnName = "id")
    private signEntity sign;

    private LocalDateTime accessTime;

    public RecentActivity(){

    }

    public RecentActivity(FileEntity file, signEntity sign, LocalDateTime accessTime){
        this.file = file;
        this.sign = sign;
        this.accessTime = accessTime;
    }
    public Long getId() {
        return id;
    }

    public FileEntity getFileEntity() {
        return file;
    }

    public signEntity getUser() {
        return sign;
    }

    public LocalDateTime getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(LocalDateTime accessTime) {
        this.accessTime = accessTime;
    }
}
