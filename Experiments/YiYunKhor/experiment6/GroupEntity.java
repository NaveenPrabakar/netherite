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
import jakarta.persistence.OneToMany;

@Entity
@Table(name="group_entity")
public class GroupEntity{

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column
        private String groupName;

        //used in the context of a JPA (Java Persistence API) entity relationship to define a
        //one-to-many relationship between two entities, GroupEntity and Message
        // @OneToMany annotation indicates that one instance of the GroupEntity can be associated with many
        //instances of the Message entity.
        //This is typical in chat applications, where a group can have multiple messages sent to it.
        //One is group, many is the message because many message map to group
        @OneToMany(mappedBy = "group")
        private Set<Message> messages== new HashSet<>();

        //Usage: When you specify cascade = CascadeType.ALL, any operation performed on the parent entity will automatically
        //be cascaded to its associated child entities. For example, if you remove a parent entity,
        // all associated child entities will also be removed.
        //When orphanRemoval is set to true, it ensures that if you remove a child entity from a collection
        //(e.g., a List or Set), that entity will be deleted from the database.
        @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
        private Set<GroupMemberEntity> members = new HashSet<>();

        // Default constructor
        public GroupEntity() {}

        // Constructor with parameters
        public GroupEntity(String groupName) {
            this.groupName = groupName;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public Set<Message> getMessages() {
            return messages;
        }

        public void setMessages(Set<Message> messages) {
            this.messages = messages;
        }

        public Set<GroupMemberEntity> getMembers() {
            return members;
        }

        public void setMembers(Set<GroupMemberEntity> members) {
            this.members = members;
        }
}
