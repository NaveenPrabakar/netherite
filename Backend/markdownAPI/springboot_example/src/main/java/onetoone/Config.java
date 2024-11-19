package onetoone;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Set;
import onetoone.signupAPI.*;

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
                        .addSchemas("AccessEntity", new ObjectSchema()
                                .addProperty("id", new Schema<Long>()
                                        .type("integer")
                                        .description("Unique identifier for the access entity")
                                        .example(1L))
                                .addProperty("userID", new Schema<Long>()
                                        .type("integer")
                                        .description("User ID who has access to the file")
                                        .example(123L))
                                .addProperty("fileID", new Schema<Long>()
                                        .type("integer")
                                        .description("File ID associated with the access")
                                        .example(456L))
                                .addProperty("accessLevel", new Schema<String>()
                                        .type("string")
                                        .description("Access level granted to the user (e.g., read, write)")
                                        .example("read"))
                        )
                        .addSchemas("JsonEntity", new ObjectSchema()
                                .addProperty("id", new Schema<Long>()
                                        .type("integer")
                                        .description("Unique identifier for the JSON entity")
                                        .example(1L))
                                .addProperty("userID", new Schema<Long>()
                                        .type("integer")
                                        .description("Identifier of the user associated with the JSON path")
                                        .example(123L))
                                .addProperty("path", new Schema<String>()
                                        .type("string")
                                        .description("Path information stored in the JSON entity")
                                        .example("/home/user/data"))
                        )
                        .addSchemas("SpeechUserEntity", new ObjectSchema()
                                .addProperty("speechId", new Schema<Long>()
                                        .type("integer")
                                        .description("Unique identifier for the speech user entity")
                                        .example(1L))
                                .addProperty("speechUserId", new Schema<Long>()
                                        .type("integer")
                                        .description("User ID associated with the speech file")
                                        .example(123L))
                                .addProperty("speechFile", new Schema<String>()
                                        .type("string")
                                        .description("Name of the speech file")
                                        .example("audio-file.mp3"))
                        )
                        .addSchemas("summarizeAPIEntity", new ObjectSchema()
                                .addProperty("AIid", new Schema<Long>()
                                        .type("integer")
                                        .description("Unique identifier for the summarize API entity")
                                        .example(1L))
                                .addProperty("AIUserId", new Schema<Long>()
                                        .type("integer")
                                        .description("User ID associated with the summarize API")
                                        .example(123L))
                                .addProperty("eventDateTime", new Schema<String>()
                                        .type("string")
                                        .format("date-time")
                                        .description("Date and time when the summarize API was used")
                                        .example("2024-11-18T10:00:00"))
                                .addProperty("usageAPICount", new Schema<Integer>()
                                        .type("integer")
                                        .description("The count of times the summarize API was used")
                                        .example(5))
                        )
                        .addSchemas("signEntity", new ObjectSchema()
                                .addProperty("id", new Schema<Long>()
                                        .type("integer")
                                        .description("Unique identifier for the signEntity")
                                        .example(1L))
                                .addProperty("username", new Schema<String>()
                                        .type("string")
                                        .description("Username of the user")
                                        .example("john_doe"))
                                .addProperty("email", new Schema<String>()
                                        .type("string")
                                        .description("Email address of the user")
                                        .example("john.doe@example.com"))
                                .addProperty("password", new Schema<String>()
                                        .type("string")
                                        .description("Password of the user")
                                        .example("securepassword123"))
                        )
                        .addSchemas("logs", new ObjectSchema()
                                .addProperty("email", new Schema<String>()
                                        .type("string")
                                        .description("Email address of the user")
                                        .example("john.doe@example.com"))
                                .addProperty("password", new Schema<String>()
                                        .type("string")
                                        .description("Password of the user")
                                        .example("securepassword123"))
                        )
                        .addSchemas("ImageEntity", new ObjectSchema()
                                .addProperty("id", new Schema<Long>()
                                        .type("integer")
                                        .description("Unique identifier for the image entity")
                                        .example(1L))
                                .addProperty("sign", new Schema<signEntity>()
                                        .description("The user (signEntity) who owns the image")
                                        .example(new signEntity("john_doe", "john.doe@example.com", "securepassword123")))
                                .addProperty("fileName", new Schema<String>()
                                        .type("string")
                                        .description("Name of the image file")
                                        .example("image1.jpg"))
                        )








                );
    }
}





