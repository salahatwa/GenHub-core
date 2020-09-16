package com.genhub.controller.blog.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.genhub.domain.dto.SystemStatus;
import com.genhub.service.blog.ChannelService;
import com.genhub.service.blog.CommentService;
import com.genhub.service.blog.PostService;
import com.genhub.service.blog.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author ssatwa
 *
 */
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {
	@Autowired
	private ChannelService channelService;
	@Autowired
	private PostService postService;
	@Autowired
	private CommentService commentService;
	@Autowired
	private UserService userService;

//    @PreAuthorize(value = "hasAuthority('admin')")
	@PreAuthorize("hasAuthority('admin')")
	@GetMapping("/sys-status")
	public ResponseEntity<SystemStatus> status() {

		float freeMemory = (float) Runtime.getRuntime().freeMemory();
		float totalMemory = (float) Runtime.getRuntime().totalMemory();
		float usedMemory = (totalMemory - freeMemory);
		float memPercent = Math.round(freeMemory / totalMemory * 100);
		String os = System.getProperty("os.name");
		String javaVersion = System.getProperty("java.version");

		SystemStatus status = new SystemStatus();
		status.setChannelCount(channelService.count());
		status.setCommentCount(commentService.count());
		status.setPostCount(postService.count());
		status.setUserCount(userService.count());
		status.setMemPercent(memPercent);
		status.setTotalMemory(totalMemory / 1024 / 1024);
		status.setFreeMemory(freeMemory);
		status.setJavaVersion(javaVersion);
		status.setOs(os);
		status.setUsedMemory(usedMemory / 1024 / 1024);
		return ResponseEntity.ok(status);
	}

}
