package onetoone.SpeechUsers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

public interface SpeechToTextRepository extends JpaRepository<SpeechUserEntity, Long> {

   SpeechUserEntity findBySpeechId(Long SpeechId);
   SpeechUserEntity findBySpeechUserId(Long SpeechUserId);
   SpeechUserEntity findBySpeechFile(String speechFile);
    //summarizeAPIEntity findByeventDateTime(LocalDateTime eventDateTime);
   //SpeechUserEntity findByUsageAPICount(Integer usageAPICount);
}
