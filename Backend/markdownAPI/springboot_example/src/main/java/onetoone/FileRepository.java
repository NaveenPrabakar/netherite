package onetoone;

import org.springframework.data.jpa.repository.JpaRepository;

//Stores into data base
public interface FileRepository extends JpaRepository<FileEntity, Long> {
}
