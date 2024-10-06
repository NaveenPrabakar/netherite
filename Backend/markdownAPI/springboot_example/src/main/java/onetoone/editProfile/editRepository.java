package onetoone.editProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import onetoone.signupAPI.signEntity;
import onetoone.signupAPI.signup;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface editRepository extends JpaRepository<signEntity, Long> {

    signEntity findByEmail(String email);
    signEntity findByPassword(String password);

    @Modifying
    @Transactional
    @Query("UPDATE signEntity s SET s.username = :newUsername WHERE s.id = :id")
    void updateusername(Long id, String newUsername);
}