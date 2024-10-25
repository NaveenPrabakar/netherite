package onetoone.Access;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;

//Stores into data base
public interface AccessRepository extends JpaRepository<AccessEntity, Long> {

}