package onetoone.Access;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import onetoone.FileEntity;
import onetoone.signupAPI.*;
import java.util.List;
import org.springframework.data.repository.query.Param;

//Stores into data base
public interface AccessRepository extends JpaRepository<AccessEntity, Long> {

    @Query("SELECT a.access.username FROM AccessEntity a WHERE a.file = :file AND a.sign = :owner")
    List<String> sent(@Param("file") FileEntity file, @Param("owner") signEntity owner);
}