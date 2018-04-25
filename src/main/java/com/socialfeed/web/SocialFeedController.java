package com.socialfeed.web;

import java.util.LinkedList;

import org.springframework.boot.autoconfigure.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import com.controller.models.entities.Entity;
import com.socialfeed.SocialFeed;
import com.socialfeed.service.SocialFeedServicer;

@Controller
@EnableAutoConfiguration
@CrossOrigin(origins = "*")
public class SocialFeedController {

	@RequestMapping("*")
	@ResponseBody
	public ResponseEntity<String> catchAll() {
		String message = "Ack. You have successfully pinged the social feed.";
		System.out.println(message);

		return ResponseEntity.status(HttpStatus.OK).body(message);
	}

	@RequestMapping(value = "/feed", method = RequestMethod.GET)
	@ResponseBody
	public LinkedList<Entity> getSocialFeed(@RequestParam("userId") String userId,
			@RequestParam("address") String address) {
		return SocialFeedServicer.getSocialFeed(userId, address);
	}
}
