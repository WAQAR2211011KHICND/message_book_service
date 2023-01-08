package com.glc.message_book_service;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MessageBookServiceApplication {

	@Bean
	public Queue queue(){
		return new Queue("book");
	}

	public static void main(String[] args) {
		SpringApplication.run(MessageBookServiceApplication.class, args);
	}

}
