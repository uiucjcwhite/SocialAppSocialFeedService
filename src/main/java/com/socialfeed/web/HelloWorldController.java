package com.socialfeed.web;

import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import com.socialfeed.SocialFeed;

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
	public SocialFeed helloJson(@RequestParam(value="name", required=false, defaultValue="World") String name) {
		return new SocialFeed();
	}
}
