package onetoone;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


/**
 * 
 * @author Vivek Bengre
 * 
 */ 

@SpringBootApplication
public class SignUpApplication2{

    public static void main(String[] args) {
        SpringApplication.run(SignUpApplication2.class, args);
    }

}