package store.sugyo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class SugyoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SugyoApplication.class, args);
    }

}
