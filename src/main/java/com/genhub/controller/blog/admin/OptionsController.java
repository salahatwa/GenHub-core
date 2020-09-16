package com.genhub.controller.blog.admin;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.genhub.config.blog.ContextStartup;
import com.genhub.domain.dto.Result;
import com.genhub.service.blog.OptionsService;
import com.genhub.service.blog.PostSearchService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author ssatwa
 *
 */

@Slf4j
@RestController
@RequestMapping("/admin/options")
@PreAuthorize("hasAuthority('admin')")
public class OptionsController {
	@Autowired
	private OptionsService optionsService;
	@Autowired
	private PostSearchService postSearchService;
	@Autowired
	private ContextStartup contextStartup;

	@PutMapping("/update")
	public Result<Object> update(@RequestParam Map<String, String> body) {
		optionsService.update(body);
		contextStartup.reloadOptions(false);
		return Result.success();
	}

	@GetMapping("/reload_options")
	public Result<Object> reloadOptions() {
		contextStartup.reloadOptions(false);
		contextStartup.resetChannels();
		return Result.success();
	}

	@GetMapping("/reset_indexes")
	public Result<Object> resetIndexes() {
		postSearchService.resetIndexes();
		return Result.success();
	}
}
