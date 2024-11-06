package onetoone.ChatWebSocket;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long>{
// The method List<Message> findByFileId(String fileId); is typically used in a Spring Data repository or similar
// persistence layer to retrieve all messages associated with a specific file
    List<Message> findByFileId(String fileId);
}
