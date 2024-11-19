package onetoone.signupAPI;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.*;

import onetoone.JsonRepository;
import onetoone.JsonEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/user")
@Tag(name = "Sign-Up API", description = "Done By Naveen Prabakar")
public class signup{

    @Autowired
    private signRepository signup;

    @Autowired
    private JsonRepository j;



    @Operation(
            summary = "Create User",
            description = "Registers a new user with the provided details. If the email is already registered, an error is returned.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User successfully created"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping("/create")
    public Map<String, String> createUser(@RequestBody signEntity sign){

        Map<String, String> check = new HashMap<>();
        if(sign == null){
            check.put("Response", "Invalid input. Please fillout all inputs");
            return check;
        }
        signEntity temp = signup.findByEmail(sign.getEmail());

        if(temp != null){
            check.put("Response", "User with email already exists");
            return check;
        }

        signup.save(sign); //Saves the users

        String path = "{'root' : []}";

        temp = signup.findByEmail(sign.getEmail());
        JsonEntity je = new JsonEntity(path, temp.getId());
        j.save(je);

        check.put("Response", "You have successfully registered");
        return check;
    }
}





