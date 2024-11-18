package onetoone;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Set;

@Configuration
public class Config {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSchemas("FileEntity", new ObjectSchema()
                                .addProperty("id", new Schema<Long>()
                                        .type("integer")
                                        .description("Unique identifier for the file entity")
                                        .example(1L))
                                .addProperty("userID", new Schema<Long>()
                                        .type("integer")
                                        .description("Identifier of the user who owns the file")
                                        .example(123L))
                                .addProperty("fileName", new Schema<String>()
                                        .type("string")
                                        .description("Name of the file")
                                        .example("example.txt"))
                                .addProperty("accessEntities", new Schema<Set>()
                                        .type("array")
                                        .items(new Schema<>().$ref("#/components/schemas/AccessEntity"))
                                        .description("Set of AccessEntities associated with this file")
                                )
                        )
                );
    }
}





