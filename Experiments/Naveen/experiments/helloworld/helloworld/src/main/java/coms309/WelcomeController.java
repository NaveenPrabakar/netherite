package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.stereotype.Controller;

@Controller
class WelcomeController {

    /*
    changes I made:
    - Used Controller instead of restController to launch the html
    - Used html & css to better display screen
    - Changed dependencies in pom.xml to support html indexes
     */

    @GetMapping("/")
    public String welcome() {
        return "index"; //displays html template to show case springboot web
    }

    @GetMapping("/experience")
    public String wel() {
        return "work-experience";
    } //displays experience template
}
