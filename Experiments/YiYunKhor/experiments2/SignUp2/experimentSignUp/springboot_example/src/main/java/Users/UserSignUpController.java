package com.example.SignUp.Users;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="user")
public class UserSignUpController{

    @Autowired
    private UserService userService;

    //  create the user
    @PostMapping (path="/save")
    public String createUser(@RequestBody User user) {
        if (user == null)
            return "User registered fail";
        userService.save(user);
        return"User registered successfully!";
    }
}