package ru.synesis.kipod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * 
 * @author arseny.kovalchuk
 *
 */
@SpringBootApplication(
    scanBasePackages = {
        "ru.synesis"
    }
)
public class Application implements ApplicationRunner {
    
    private final Logger logger = LoggerFactory.getLogger(Application.class);
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Application satrted!");
    }

}
