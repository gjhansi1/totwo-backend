package com.ToTwo.ToTwo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;  // ✅ added this line

@EnableScheduling  // ✅ enables the scheduler (RoomScheduler will now run automatically)
@SpringBootApplication
public class ToTwoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToTwoApplication.class, args);
    }
}

