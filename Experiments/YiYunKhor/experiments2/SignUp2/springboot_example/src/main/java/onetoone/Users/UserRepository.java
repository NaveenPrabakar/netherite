//A repository is an interface used to interact with the database. It abstracts away the actual database operations
// (like saving, retrieving, updating, or deleting entities). Spring Data provides built-in repositories so you
// don’t have to write common database operations manually.
package onetoone.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Integer> {

//    This method will retrieve a User object by its id
    User findByEmailId(String email);

//    This method will delete a user by their id. The
//    @Transactional annotation ensures that the delete operation occurs within a transaction.
    @Transactional
    void deleteByEmailId(String email);
}