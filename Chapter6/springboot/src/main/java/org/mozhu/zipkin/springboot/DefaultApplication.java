package org.mozhu.zipkin.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class DefaultApplication {

    public static void main(String[] args) {
        SpringApplication.run(DefaultApplication.class, args);
    }

}
