package onetoone;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface JsonRepository extends JpaRepository<JsonEntity, Long> {

    @Query("Select path from JsonEntity where userID = :id")
    String getSystem(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE JsonEntity j Set j.path = :newPath WHERE j.userID = :id")
    void updatepath(Long id, String newPath);

}