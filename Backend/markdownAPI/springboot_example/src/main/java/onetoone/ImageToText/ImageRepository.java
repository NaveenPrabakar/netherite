package onetoone.ImageToText;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import onetoone.signupAPI.*;
import java.util.List;

//Stores into data base
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {

    @Query("SELECT i.fileName FROM ImageEntity i WHERE i.sign = :sign")
    List<String> findImageNamesByUser(@Param("sign") signEntity sign);
}