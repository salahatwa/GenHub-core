/**
 *
 */
package com.genhub.controller.blog.user;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import com.genhub.aspect.blog.MessageEvent;
import com.genhub.config.secuirty.CurrentUser;
import com.genhub.config.secuirty.UserPrincipal;
import com.genhub.domain.blog.dto.CommentVO;
import com.genhub.domain.dto.Result;
import com.genhub.service.blog.CommentService;
import com.genhub.utils.BlogConsts;
import com.genhub.utils.Constant;

/**
 * @author ssatwa
 */
@RestController("userComment")
@RequestMapping("/user/comment")
@ConditionalOnProperty(name = "site.controls.comment", havingValue = "true", matchIfMissing = true)
public class CommentController {
	@Autowired
	private CommentService commentService;
	@Autowired
	private ApplicationContext applicationContext;

	@GetMapping("/list/{toId}")
	public Page<CommentVO> view(@PathVariable Long toId,
			@RequestParam(value = "page", defaultValue = Constant.DEFAULT_PAGE_NUMBER) int pageNo,
			@RequestParam(value = "size", defaultValue = Constant.DEFAULT_PAGE_SIZE) int size) {
		return commentService.pagingByPostId(PageRequest.of(pageNo - 1, size, Sort.by(Sort.Direction.DESC, "id")),
				toId);
	}

	@PostMapping("/submit/{toId}/{pid}")
	public Result<Object> post(@CurrentUser UserPrincipal currentUser, @PathVariable Long toId, @PathVariable long pid,
			String text) {

		if (toId <= 0 || StringUtils.isBlank(text)) {
			return Result.failure("operation failed");
		}

		CommentVO c = new CommentVO();
		c.setPostId(toId);
		c.setContent(HtmlUtils.htmlEscape(text));
		c.setAuthorId(currentUser.getId());

		c.setPid(pid);

		commentService.post(c);

		sendMessage(currentUser.getId(), toId, pid);

		return Result.successMessage("Published successfully");
	}

	@DeleteMapping("/delete/{id}")
	public Result<Object> delete(@CurrentUser UserPrincipal currentUser, @PathVariable Long id) {
		commentService.delete(id, currentUser.getId());
		return Result.success();
	}

	/**
	 *
	 * @param userId
	 * @param postId
	 */
	private void sendMessage(long userId, long postId, long pid) {
		MessageEvent event = new MessageEvent("MessageEvent");
		event.setFromUserId(userId);

		if (pid > 0) {
			event.setEvent(BlogConsts.MESSAGE_EVENT_COMMENT_REPLY);
			event.setCommentParentId(pid);
		} else {
			event.setEvent(BlogConsts.MESSAGE_EVENT_COMMENT);
		}
		event.setPostId(postId);
		applicationContext.publishEvent(event);
	}
}