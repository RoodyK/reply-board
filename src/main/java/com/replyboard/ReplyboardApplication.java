package com.replyboard;

import com.replyboard.config.CustomProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CustomProperties.class)
public class ReplyboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReplyboardApplication.class, args);
	}

}
