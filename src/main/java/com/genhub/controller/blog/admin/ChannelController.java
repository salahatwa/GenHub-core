package com.genhub.controller.blog.admin;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.genhub.config.blog.ContextStartup;
import com.genhub.domain.blog.Channel;
import com.genhub.domain.dto.Result;
import com.genhub.service.blog.ChannelService;
import com.genhub.utils.BlogConsts;
import com.genhub.utils.Constant;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ssatwa
 *
 */
@Slf4j
@RestController
@RequestMapping("/admin/channel")
public class ChannelController {
	@Autowired
	private ChannelService channelService;
	@Autowired
	private ContextStartup contextStartup;

	@GetMapping("/list")
	public ResponseEntity<List<Channel>> list() {
		return ResponseEntity.ok(channelService.findAll(BlogConsts.IGNORE));
	}

	@GetMapping("/list/page")
	public ResponseEntity<Page<Channel>> listPage(
			@RequestParam(value = "page", defaultValue = Constant.DEFAULT_PAGE_NUMBER) int pageNo,
			@RequestParam(value = "size", defaultValue = Constant.DEFAULT_PAGE_SIZE) int size) {
		Sort sort = Sort.by(Sort.Direction.DESC, "weight", "id");
		return ResponseEntity
				.ok(channelService.findAllPaging(PageRequest.of(pageNo , size, sort), BlogConsts.IGNORE));
	}

	@GetMapping("/view/{id}")
	public ResponseEntity<Channel> view(@PathVariable @NotBlank Integer id) {
		return ResponseEntity.ok(channelService.getById(id));
	}

	@PreAuthorize("hasAuthority('admin')")
	@PutMapping("/update")
	public ResponseEntity<Channel> update(@RequestBody Channel view) {
		if (view != null) {
			System.out.println(view);
			view = channelService.update(view);

//			contextStartup.resetChannels();
		}
		return ResponseEntity.ok(view);
	}

	@PreAuthorize("hasAuthority('admin')")
	@PutMapping("/weight/{id}/{weight}")
	public Result weight(@PathVariable Integer id, @PathVariable int weight) {

		weight = (weight != 0) ? weight : BlogConsts.FEATURED_ACTIVE;

		channelService.updateWeight(id, weight);
//		contextStartup.resetChannels();
		return Result.success();
	}

	@PreAuthorize("hasAuthority('admin')")
	@DeleteMapping("/delete/{id}")
	public Result delete(@PathVariable @NotBlank Integer id) {
		if (id != null) {
			channelService.delete(id);

			contextStartup.resetChannels();
		}
		return Result.success();
	}

}
