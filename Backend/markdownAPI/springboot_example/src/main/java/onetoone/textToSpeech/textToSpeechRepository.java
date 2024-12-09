package onetoone.textToSpeech;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

public interface textToSpeechRepository extends JpaRepository<textToSpeechEntity, Long> {

   textToSpeechEntity findBytextToSpeechId(Long textToSpeechId);
   textToSpeechEntity findBytextToSpeechUserId(Long textToSpeechUserId);
   textToSpeechEntity findBytextToSpeechFile(String textToSpeechFile);
    //summarizeAPIEntity findByeventDateTime(LocalDateTime eventDateTime);
   //translateEntity findByUsageAPICount(Integer usageAPICount);
}
