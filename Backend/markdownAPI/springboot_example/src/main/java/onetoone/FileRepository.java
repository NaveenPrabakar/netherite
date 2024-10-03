package onetoone;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

//Stores into data base
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    FileEntity findByFileName(String fileName);

    @Modifying
    @Transactional
    void deleteByFileName(String fileName);
}
