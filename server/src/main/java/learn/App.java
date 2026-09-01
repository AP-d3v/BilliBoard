package learn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // 1
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args); // 2
    }
}