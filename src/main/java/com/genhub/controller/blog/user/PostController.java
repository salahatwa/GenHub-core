package com.genhub.controller.blog.user;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.genhub.config.secuirty.CurrentUser;
import com.genhub.config.secuirty.UserPrincipal;
import com.genhub.domain.dto.Result;
import com.genhub.domain.dto.blog.PostVO;
import com.genhub.service.blog.PostService;
import com.genhub.utils.BlogConsts;

/**
 * 
 * @author ssatwa
 *
 */
@RestController("userPost")
@RequestMapping("/user/post")
public class PostController {
	@Autowired
	private PostService postService;

	@GetMapping("/editing/{id}")
	public ResponseEntity<PostVO> view(@CurrentUser UserPrincipal currentUser, @PathVariable Long id) {
		PostVO view = postService.get(id);

		Assert.notNull(view, "The article has been deleted");
		Assert.isTrue(view.getAuthorId() == currentUser.getId(), "This article does not belong to you");

		Assert.isTrue(view.getChannel().getStatus() == BlogConsts.STATUS_NORMAL,
				"Please edit this article in the background");

		return ResponseEntity.ok(view);
	}

	/**
	 * 
	 * @param post
	 * @return
	 */
	@PostMapping("/submit")
	public Result<Object> post(@CurrentUser UserPrincipal currentUser, @RequestBody PostVO post) {

		Assert.notNull(post, "Incomplete parameters");
		Assert.state(StringUtils.isNotBlank(post.getTitle()), "The title can not be blank");
		Assert.state(StringUtils.isNotBlank(post.getContent()), "the content can not be blank");

		post.setAuthorId(currentUser.getId());

		if (post.getId() > 0) {
			PostVO exist = postService.get(post.getId());
			Assert.notNull(exist, "Article does not exist");
			Assert.isTrue(exist.getAuthorId() == currentUser.getId(), "This article does not belong to you");

			postService.update(post);
		} else {
			postService.post(post);
		}
		return Result.success();
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@DeleteMapping("/delete/{id}")
	public Result<Object> delete(@CurrentUser UserPrincipal currentUser, @PathVariable Long id) {
		postService.delete(id, currentUser.getId());
		return Result.success();
	}

}
