package onetoone.Recent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import onetoone.*;
import org.springframework.data.repository.query.Param;
import onetoone.signupAPI.*;

import java.util.List;

public interface RecentRepo extends JpaRepository<RecentActivity, Long> {

    @Query("SELECT r FROM RecentActivity r WHERE r.sign.id = :userId ORDER BY r.accessTime ASC")
    List<RecentActivity> findRecentByUserId(Long userId);

    RecentActivity findByFileAndSign(FileEntity file, signEntity sign);

    void deleteByFileAndSign(FileEntity file, signEntity sign);

    void deleteById(Long id);

    @Query("SELECT r.id FROM RecentActivity r WHERE r.file.id = :fileId")
    Long findIdByFileId(@Param("fileId") Long fileId);

    void deleteByFile(FileEntity file);
}