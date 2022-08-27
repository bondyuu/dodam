package com.team1.dodam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DodamApplication {

    public static void main(String[] args) {
        SpringApplication.run(DodamApplication.class, args);
    }

}
