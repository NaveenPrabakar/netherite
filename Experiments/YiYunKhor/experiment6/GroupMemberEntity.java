package onetoone.ChatWebSocket;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "group_members")
public class GroupMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //nullable cannot accept NULL values. This means that when you create or update a record in the database
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private GroupEntity group;

    //signEntity userID
    private Long userId;

    // Default constructor
    public GroupMemberEntity() {}

    // Constructor
    public GroupMemberEntity(GroupEntity group, Long userId) {
        this.group = group;
        this.userId = userId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GroupEntity getGroup() {
        return group;
    }

    public void setGroup(GroupEntity group) {
        this.group = group;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}