//A repository is an interface used to interact with the database. It abstracts away the actual database operations
// (like saving, retrieving, updating, or deleting entities). Spring Data provides built-in repositories so you
// don’t have to write common database operations manually.
package com.example.SignUp.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, int> {
    User findByEmail(String email);

}