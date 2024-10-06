package onetoone.loginAPI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import onetoone.signupAPI.signEntity;
import onetoone.signupAPI.signup;

public interface loginRepository extends JpaRepository<signEntity, Long> {

    signEntity findByUsername(String username);
    signEntity findByEmail(String email);
    signEntity findByPassword(String password);

}