package com.socialfeed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EnableAutoConfiguration
public class SocialFeed implements CommandLineRunner {

	public static String TARGET_HOST;

    @Autowired
    public Environment env;

	@Override
	public void run(String... args) throws Exception {
		TARGET_HOST = env.getProperty("target_url");
		BackgroundWorker.startCoreControllerPing();
	}

	public static void main(String[] args) {
		SpringApplication.run(SocialFeed.class, args);
	}
}
