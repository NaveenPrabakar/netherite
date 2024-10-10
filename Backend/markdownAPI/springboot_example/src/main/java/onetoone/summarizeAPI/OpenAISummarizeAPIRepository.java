package onetoone.summarizeAPI;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import onetoone.signupAPI.signEntity;
import java.time.LocalDateTime;

public interface OpenAISummarizeAPIRepository extends JpaRepository<summarizeAPIEntity, Long> {

    summarizeAPIEntity findByAIid(Long AIid);
    summarizeAPIEntity findByAIUserId(Long AIUserId);
    summarizeAPIEntity findByeventDateTime(LocalDateTime eventDateTime);
    summarizeAPIEntity findByUsageAPICount(Integer usageAPICount);
}