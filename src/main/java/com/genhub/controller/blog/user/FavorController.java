package com.genhub.controller.blog.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.genhub.aspect.blog.MessageEvent;
import com.genhub.config.secuirty.CurrentUser;
import com.genhub.config.secuirty.UserPrincipal;
import com.genhub.domain.dto.Result;
import com.genhub.service.blog.PostService;
import com.genhub.utils.BlogConsts;

/**
 * @author ssatwa
 */
@RestController
@RequestMapping("/user")
public class FavorController {
	@Autowired
	private PostService postService;
	@Autowired
	private ApplicationContext applicationContext;

	/**
	 * @param id
	 * @return
	 */
	@GetMapping("/favor/{id}")
	public Result<Object> favor(@CurrentUser UserPrincipal currentUser, @PathVariable Long id) {
		postService.favor(currentUser.getId(), id);
		sendMessage(currentUser.getId(), id);
		return Result.success();
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/unfavor/{id}")
	public Result<Object> unfavor(@CurrentUser UserPrincipal currentUser, @PathVariable Long id) {
		postService.unfavor(currentUser.getId(), id);
		return Result.success();
	}

	/**
	 * @param userId
	 * @param postId
	 */
	private void sendMessage(long userId, long postId) {
		MessageEvent event = new MessageEvent("MessageEvent" + System.currentTimeMillis());
		event.setFromUserId(userId);
		event.setEvent(BlogConsts.MESSAGE_EVENT_FAVOR_POST);
		// I don’t know the author of the article here, let the notification event
		// system complete
		event.setPostId(postId);
		applicationContext.publishEvent(event);
	}
}
