package com.socialfeed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
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

import com.socialapplibrary.endpoints.EndpointConstants;

@EnableAsync
@SpringBootApplication
@EnableAutoConfiguration
public class SocialFeed implements CommandLineRunner {

	public static String TARGET_HOST;
	public static String PROFILE;

    @Autowired
    public Environment env;

	@Override
	public void run(String... args) {
		TARGET_HOST = env.getProperty("target_url");
		PROFILE = env.getProperty("spring.profiles.active");
	}

	public static void main(String[] args) {
		SpringApplication.run(SocialFeed.class, args);

		if (StringUtils.isBlank(PROFILE) || !PROFILE.equals("build")) {
			BackgroundWorker.startCoreControllerPing();

//	  		String finalData = "";
//			String output;
//			String endpoint;
//			URL url;
//			HttpURLConnection connection;
//	        endpoint = SocialFeed.TARGET_HOST + ":" + EndpointConstants.CORE_PORT;
//			String pingMessage = "Pinging the Core Controller at " + endpoint;
//			System.out.println(pingMessage);
//			try {
//				finalData = "";
//				url = new URL(endpoint);
//				connection = (HttpURLConnection) url.openConnection();
//				connection.setRequestMethod("GET");
//				connection.setRequestProperty("Accept", "application/json");
//				int status = connection.getResponseCode();
//				if (status == 200) {
//					BufferedReader br = new BufferedReader(new InputStreamReader(
//							(connection.getInputStream())));
//					while ((output = br.readLine()) != null) {
//						finalData += output;
//					}
//
//					System.out.println("200 OK. Server response: " + finalData);
//				} else {
//					System.out.println("Server status code: " + status);
//				}	
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		
		}
	}
}
