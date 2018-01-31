package com.socialfeed.web;

import java.util.LinkedList;

import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import com.socialfeed.SocialFeed;
import com.socialfeed.domain.entity.Entity;
import com.socialfeed.service.SocialFeedServicer;

@Controller
@EnableAutoConfiguration
public class SocialFeedController {

	@RequestMapping(value = "/feed", method = RequestMethod.GET)
	@ResponseBody
	public LinkedList<Entity> getSocialFeed(@RequestParam("userId") String userId) {
		return SocialFeedServicer.getSocialFeed(userId);
	}
}
