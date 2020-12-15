package com.stephenschafer.ami;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.stephenschafer.ami.jpa.UserDao;
import com.stephenschafer.ami.jpa.UserEntity;

@SpringBootApplication
public class Application {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner init(final UserDao userDao) {
		return args -> {
			if (args.length >= 1 && args[0].equals("init")) {
				final UserEntity user1 = new UserEntity();
				user1.setFirstName("Foobar");
				user1.setLastName("Foobar");
				user1.setUsername("foobar");
				user1.setPassword(passwordEncoder.encode("foobar"));
				userDao.save(user1);
			}
			else {
			}
		};
	}
}
