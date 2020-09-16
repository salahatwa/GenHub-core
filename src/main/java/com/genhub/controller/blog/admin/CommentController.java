package com.genhub.controller.blog.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.genhub.domain.dto.Result;
import com.genhub.domain.dto.blog.CommentVO;
import com.genhub.service.blog.CommentService;
import com.genhub.utils.Constant;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ssatwa
 *
 */
@Slf4j
@RestController("adminComment")
@RequestMapping("/admin/comment")
@PreAuthorize("hasAuthority('admin')")
public class CommentController {
	@Autowired
	private CommentService commentService;

	@GetMapping("/list")
	public ResponseEntity<Page<CommentVO>> list(
			@RequestParam(value = "page", defaultValue = Constant.DEFAULT_PAGE_NUMBER) int pageNo,
			@RequestParam(value = "size", defaultValue = Constant.DEFAULT_PAGE_SIZE) int size) {
		Page<CommentVO> page = commentService.paging4Admin(PageRequest.of(pageNo - 1, size));
		return ResponseEntity.ok(page);
	}

	@DeleteMapping("/delete")
	public Result delete(@RequestParam("id") List<Long> id) {
		if (id != null) {
			commentService.delete(id);
		}
		return Result.success();
	}
}
