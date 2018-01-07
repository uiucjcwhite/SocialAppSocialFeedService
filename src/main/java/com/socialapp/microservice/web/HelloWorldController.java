package com.socialapp.microservice.web;

import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import com.socialapp.microservice.domain.HelloWorld;

@Controller
@EnableAutoConfiguration
public class HelloWorldController {

	@RequestMapping("/")
	@ResponseBody
	public String hello() {
		return "Hello World!";
	}
	
	@RequestMapping(value = "/json", method = RequestMethod.GET)
	@ResponseBody
	public HelloWorld helloJson(@RequestParam(value="name", required=false, defaultValue="World") String name) {
		return new HelloWorld(name);
	}
}
