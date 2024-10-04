package onetoone.loginAPI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


public interface loginRepository extends JpaRepository<signEntity, Long> {

    signEntity findByEmail(String email);
    signEntity findByPassword(String password);

}