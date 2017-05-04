package org.microbule.example.pcf;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = "org.microbule")
@PropertySource("classpath:/application.properties")
public class Startup {

    public static void main(String[] args) {
        System.setProperty("CF_INSTANCE_ADDR", "localhost:8080");
        SpringApplication.run(Startup.class, args);
    }
}
