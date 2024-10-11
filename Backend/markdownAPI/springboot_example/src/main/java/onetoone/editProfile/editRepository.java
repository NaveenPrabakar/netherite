package onetoone.editProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import onetoone.signupAPI.signEntity;
import onetoone.signupAPI.signup;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.*;


public interface editRepository extends JpaRepository<signEntity, Long> {

    signEntity findByEmail(String email);
    signEntity findByPassword(String password);

    @Modifying
    @Transactional
    @Query("UPDATE signEntity s SET s.username = :newUsername WHERE s.id = :id")
    void updateusername(Long id, String newUsername);

    @Modifying
    @Transactional
    @Query("UPDATE signEntity s Set s.password = :newPassword WHERE s.id = :id")
    void updatepassword(Long id, String newPassword);

    @Modifying
    @Transactional
    @Query("UPDATE signEntity s Set s.email = :newEmail WHERE s.id = :id")
    void updateEmail(Long id, String newEmail);

    @Query("SELECT fileName from FileEntity where userID = :id")
    List<String> findallFiles(Long id);

    @Modifying
    @Transactional
    @Query("DELETE from FileEntity where userID = :id")
    void deleteAll(Long id);

}