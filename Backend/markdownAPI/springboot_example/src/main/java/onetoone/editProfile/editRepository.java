package onetoone.editProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import onetoone.signupAPI.signEntity;
import onetoone.signupAPI.signup;


public interface editRepository extends JpaRepository<signEntity, Long> {

    signEntity findByEmail(String email);
    signEntity findByPassword(String password);

}