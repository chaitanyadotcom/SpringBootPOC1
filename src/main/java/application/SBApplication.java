package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"application"})
public class SBApplication {

    public static void main(String[] args) {
    	System.setProperty("java.net.preferIPv6Addresses", "true");  
		
        SpringApplication.run(SBApplication.class, args);
    }
}
