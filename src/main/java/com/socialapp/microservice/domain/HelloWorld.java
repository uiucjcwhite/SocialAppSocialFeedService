package com.socialapp.microservice.domain;

public class HelloWorld {
	private String greeting = "Hello";
	private String name;
	
	public HelloWorld(String recipient) {
		this.name = recipient;
	}
	
	public String getGreeting() {
		return this.greeting;
	}
	
	public String getRecipient() {
		return this.name;
	}
}
