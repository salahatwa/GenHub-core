package com.genhub.controller.blog.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.genhub.config.blog.SiteOptions;
import com.genhub.config.secuirty.CurrentUser;
import com.genhub.config.secuirty.UserPrincipal;
import com.genhub.domain.dto.Result;
import com.genhub.domain.dto.blog.PostVO;
import com.genhub.service.blog.ChannelService;
import com.genhub.service.blog.PostService;
import com.genhub.utils.BlogConsts;
import com.genhub.utils.Constant;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ssatwa
 *
 */
@Slf4j
@RestController
@RequestMapping("/admin/post")
@PreAuthorize("hasAuthority('admin')")
public class PostController {
	@Autowired
	private PostService postService;
	@Autowired
	private ChannelService channelService;

	@Autowired
	protected SiteOptions siteOptions;

	@GetMapping("/list")
	public ResponseEntity<Page<PostVO>> list(@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "page", defaultValue = Constant.DEFAULT_PAGE_NUMBER) int pageNo,
			@RequestParam(value = "size", defaultValue = Constant.DEFAULT_PAGE_SIZE) int size,
			@RequestParam(value = "channelId", required = false) int channelId) {

		Page<PostVO> page = postService.paging4Admin(
				PageRequest.of(pageNo - 1, size, Sort.by(Sort.Direction.DESC, "weight", "created")), channelId, title);
		return ResponseEntity.ok(page);
	}

	/**
	 * @param id
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/view")
	public ResponseEntity<PostVO> toUpdate(Long id) {
		PostVO view = null;
		if (null != id && id > 0) {
			view = postService.get(id);
		}
		return ResponseEntity.ok(view);
	}

	@PostMapping("/update")
	public Result<Object> subUpdate(@CurrentUser UserPrincipal currentUser, @RequestBody PostVO post) {
		if (post != null) {
			if (post.getId() > 0) {
				postService.update(post);
			} else {
				post.setAuthorId(currentUser.getId());
				postService.post(post);
			}
		}
		return Result.success();
	}

	@PutMapping("/featured/{id}/{featured}")
	public Result<Object> featured(@PathVariable Long id, @PathVariable int featured) {

		featured = (featured > 0) ? featured : BlogConsts.FEATURED_ACTIVE;

		if (id != null) {
			postService.updateFeatured(id, featured);

		}
		return Result.success();
	}

	@PutMapping("/weight/{id}")
	public Result<Object> weight(@PathVariable Long id, @PathVariable int weight) {
		weight = (weight > 0) ? weight : BlogConsts.FEATURED_ACTIVE;

		if (id != null) {
			postService.updateWeight(id, weight);
		}
		return Result.success();
	}

	@DeleteMapping("/delete")
	public Result<Object> delete(@RequestParam("id") List<Long> id) {
		if (id != null) {
			postService.delete(id);
		}
		return Result.success();
	}
}
