package onetoone.Recent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import onetoone.*;
import onetoone.loginAPI.*;
import onetoone.signupAPI.*;
import java.util.ArrayList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Service
@RestController
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

    @GetMapping("recent/{email}")
    public ResponseEntity<List<String>> names(@PathVariable String email){
        signEntity user = loginRepository.findByEmail(email);

        List<RecentActivity> userRecents = recentRepository.findRecentByUserId(user.getId());
        List<String> name = new ArrayList<>();

        for(RecentActivity r : userRecents){
            name.add(r.getFileEntity().getName());
        }

        Collections.reverse(name);


        return ResponseEntity.ok(name);
    }
}
