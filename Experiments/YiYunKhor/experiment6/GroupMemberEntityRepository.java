package onetoone.ChatWebSocket;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<GroupMemberEntity, Long>{

}
