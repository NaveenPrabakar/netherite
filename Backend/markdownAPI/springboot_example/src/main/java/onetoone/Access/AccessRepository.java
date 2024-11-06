package onetoone.Access;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import onetoone.FileEntity;
import onetoone.signupAPI.signEntity;

import java.util.List;
import org.springframework.data.repository.query.Param;

//Stores into data base
public interface AccessRepository extends JpaRepository<AccessEntity, Long> {

    @Query("SELECT a.access.username FROM AccessEntity a WHERE a.file = :file AND a.sign = :owner")
    List<String> sent(@Param("file") FileEntity file, @Param("owner") signEntity owner);

    @Query("SELECT f.fileName FROM AccessEntity a JOIN a.files f WHERE a.access.id = :accessId")
    List<String> findFileNamesByAccessId(Long accessId);

    @Query("SELECT a.access FROM AccessEntity a WHERE a.file = :file AND a.sign = :owner")
    List<signEntity> sents(@Param("file") FileEntity file, @Param("owner") signEntity owner);


    @Modifying
    @Transactional
    @Query("DELETE FROM AccessEntity ae WHERE ae.sign = :signEntity")
    void deleteBySignEntity(signEntity signEntity);
}