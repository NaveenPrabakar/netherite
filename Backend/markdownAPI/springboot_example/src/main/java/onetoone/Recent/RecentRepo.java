package onetoone.Recent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecentRepo extends JpaRepository<RecentActivity, Long> {

    @Query("SELECT r FROM RecentActivity r WHERE r.sign.id = :userId ORDER BY r.accessTime ASC")
    List<RecentActivity> findRecentByUserId(Long userId);

    void deleteById(Long id);
}