package bg.propertymanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PropertyManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PropertyManagerApplication.class, args);
    }

}
