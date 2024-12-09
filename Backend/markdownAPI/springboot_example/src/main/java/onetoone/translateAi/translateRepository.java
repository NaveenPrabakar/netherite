package onetoone.translateAi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

public interface translateRepository extends JpaRepository<translateEntity, Long> {

   translateEntity findBytranslateId(Long translateId);
   translateEntity findBytranslateUserId(Long translateUserId);
   translateEntity findBytranslateFile(String translateFile);
    //summarizeAPIEntity findByeventDateTime(LocalDateTime eventDateTime);
   //translateEntity findByUsageAPICount(Integer usageAPICount);
}
