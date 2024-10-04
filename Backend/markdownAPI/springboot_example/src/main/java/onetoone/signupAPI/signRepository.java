package onetoone.signupAPI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


public interface signRepository extends JpaRepository<signEntity, Long> {

    signEntity findByEmail(String email);
    signEntity findByPassword(String password);

    @Transactional
    void deleteByEmail(String email);
}


