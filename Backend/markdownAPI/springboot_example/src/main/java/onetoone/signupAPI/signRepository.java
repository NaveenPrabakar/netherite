package onetoone.signupAPI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface signRepository extends JpaRepository<signEntity, Long> {

    signEntity findByEmail(String email);
    signEntity findByUsername(String username);
    signEntity findByPassword(String password);


    @Modifying
    @Transactional
    @Query("DELETE from signEntity where id = :id")
    void deleteall(Long id);
}


