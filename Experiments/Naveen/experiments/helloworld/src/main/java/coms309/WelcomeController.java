package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

@RestController
class WelcomeController {

    @GetMapping("/")
    public String welcome() {
        ArrayList<Integer> check = new ArrayList<>();
        Random r = new Random();

        int x = r.nextInt(50);

        for(int i = 0; i < x; i ++){
            check.add(i);
        }
        return check.toString();
    }

    @GetMapping("/{name}")
    public String welcome(@PathVariable String name) {
        return "Hello and welcome to COMS 309: " + name;
    }
}
