package onetoone.Recent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import onetoone.*;
import onetoone.loginAPI.*;
import onetoone.signupAPI.*;

@Service
public class RecentController {

    @Autowired
    private RecentRepo recentRepository;

    @Autowired
    private FileRepository fileEntityRepository;

    @Autowired
    private loginRepository loginRepository;

    @Transactional
    public void addRecentRecord(Long fileEntityId, Long userId) {
        FileEntity fileEntity = fileEntityRepository.findById(fileEntityId)
                .orElseThrow(() -> new RuntimeException("FileEntity not found"));
        signEntity user = loginRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RecentActivity existingRecent = recentRepository.findByFileAndSign(fileEntity, user);


        List<RecentActivity> userRecents = recentRepository.findRecentByUserId(userId);

        if (existingRecent != null) {
            // If the record exists, update the access time
            existingRecent.setAccessTime(LocalDateTime.now());
            recentRepository.save(existingRecent);
            return;
        }


        if (userRecents.size() >= 5) {
            RecentActivity oldest = userRecents.get(0);
            recentRepository.deleteById(oldest.getId());
        }


        RecentActivity recent = new RecentActivity(fileEntity, user, LocalDateTime.now());
        recentRepository.save(recent);
    }
}
