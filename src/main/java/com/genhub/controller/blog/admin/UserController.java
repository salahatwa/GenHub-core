package com.genhub.controller.blog.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.genhub.domain.Role;
import com.genhub.domain.blog.dto.UserVO;
import com.genhub.domain.dto.Result;
import com.genhub.service.blog.RoleService;
import com.genhub.service.blog.UserRoleService;
import com.genhub.service.blog.UserService;
import com.genhub.utils.BeanMapUtils;
import com.genhub.utils.BlogConsts;
import com.genhub.utils.Constant;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ssatwa
 *
 */
@Slf4j
@RestController
@RequestMapping("/admin/user")
@PreAuthorize("hasAuthority('admin')")
public class UserController {
	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserRoleService userRoleService;

	@GetMapping("/list")
	public ResponseEntity<Page<UserVO>> list(@RequestParam(required = false) String name,
			@RequestParam(value = "page", defaultValue = Constant.DEFAULT_PAGE_NUMBER) int pageNo,
			@RequestParam(value = "size", defaultValue = Constant.DEFAULT_PAGE_SIZE) int size) {
		Page<UserVO> page = userService.paging(PageRequest.of(pageNo - 1, size), name);

		List<UserVO> users = page.getContent();
		List<Long> userIds = new ArrayList<>();

		users.forEach(item -> {
			userIds.add(item.getId());
		});

		Map<Long, List<Role>> map = userRoleService.findMapByUserIds(userIds);
		users.forEach(item -> {
			item.setAuthorities(BeanMapUtils.roleToAuthiority(map.get(item.getId())));
		});

		return ResponseEntity.ok(page);
	}

	@GetMapping("/view/{id}")
	public ResponseEntity<UserVO> view(@PathVariable long id) {
		UserVO view = userService.get(id);
		view.setAuthorities(BeanMapUtils.roleToAuthiority(userRoleService.listRoles(view.getId())));
		return ResponseEntity.ok(view);
	}

	@PostMapping("/update_role/{id}")
	public Result<Object> postAuthc(@PathVariable long id,
			@RequestParam(value = "roleIds", required = false) Set<Long> roleIds, ModelMap model) {
		userRoleService.updateRole(id, roleIds);
		return Result.success();
	}

	@PostMapping("/pwd/{id}")
	public Result<Object> pwd(@PathVariable long id, String newPassword) {
		userService.updatePassword(id, newPassword);
		return Result.success();
	}

	@PutMapping("/open/{id}")
	public Result<Object> open(@PathVariable long id) {
		userService.updateStatus(id, BlogConsts.STATUS_NORMAL);
		return Result.success();
	}

	@PutMapping("/close/{id}")
	public Result<Object> close(@PathVariable long id) {
		userService.updateStatus(id, BlogConsts.STATUS_CLOSED);
		return Result.success();
	}
}
