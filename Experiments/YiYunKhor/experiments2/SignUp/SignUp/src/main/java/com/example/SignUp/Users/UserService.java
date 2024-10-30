//Purpose: This layer contains the business logic of your application.
// It performs operations like calculations, processing data, or enforcing business rules.
//Example: If the API layer says "Get all users," the Service layer decides how to do that,
// like calling the database, filtering results, or applying business rules.
//Key Point: The Service layer interacts with the Database layer to retrieve or save data,
// but it doesn't directly deal with how the data is stored.

package com.example.SignUp.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService{

//    if same folder, no need to import
    @Autowired
    private UserRepository userRepository;

    public User save(User user){
        return userRepository.save(user);
    }
}

