package com.socialfeed.web;

import java.util.LinkedList;

import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import com.controller.models.entities.Entity;
import com.socialfeed.SocialFeed;
import com.socialfeed.service.SocialFeedServicer;

@Controller
@EnableAutoConfiguration
public class SocialFeedController {

	@RequestMapping(value = "/feed", method = RequestMethod.GET)
	@ResponseBody
	public LinkedList<Entity> getSocialFeed(@RequestParam("userId") String userId, @RequestParam("address") String address) {
		return SocialFeedServicer.getSocialFeed(userId, address);
	}
}
