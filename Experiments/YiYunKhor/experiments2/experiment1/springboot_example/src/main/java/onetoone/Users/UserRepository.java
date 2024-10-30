package onetoone.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * In your UserRepository interface, which extends JpaRepository, you're defining two methods for interacting with the User entity:
 */
//
public interface UserRepository extends JpaRepository<User, Long> {

//    findById(int id): This method will retrieve a User object by its id.
    User findById(int id);

//deleteById(Long id): This method will delete a user by their id. The @Transactional annotation ensures that the delete operation occurs within a transaction.
    @Transactional
    void deleteById(int id);
}

