package onetoone.summarizeAPI;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import onetoone.signupAPI.signEntity;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Query;

public interface OpenAISummarizeAPIRepository extends JpaRepository<summarizeAPIEntity, Long> {

    summarizeAPIEntity findByAIid(Long AIid);

    @Query("SELECT s FROM summarizeAPIEntity s WHERE s.AIUserId = :AIUserId")
    summarizeAPIEntity findByAIUserId(Long AIUserId);
    summarizeAPIEntity findByeventDateTime(LocalDateTime eventDateTime);
    summarizeAPIEntity findByUsageAPICount(Integer usageAPICount);
}