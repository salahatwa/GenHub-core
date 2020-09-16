package com.genhub.controller.blog.admin;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.genhub.domain.Role;
import com.genhub.domain.dto.Result;
import com.genhub.exceptions.BlogException;
import com.genhub.service.blog.RoleService;
import com.genhub.utils.Constant;
import com.sun.istack.NotNull;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ssatwa
 *
 */
@Slf4j
@RestController
@RequestMapping("/admin/role")
@PreAuthorize("hasAuthority('admin')")
public class RoleController {
	@Autowired
	private RoleService roleService;

	@GetMapping("/list")
	public ResponseEntity<Page<Role>> paging(String name,
			@RequestParam(value = "page", defaultValue = Constant.DEFAULT_PAGE_NUMBER) int pageNo,
			@RequestParam(value = "size", defaultValue = Constant.DEFAULT_PAGE_SIZE) int size, ModelMap model) {
		Page<Role> page = roleService.paging(PageRequest.of(pageNo - 1, size), name);
		model.put("name", name);
		model.put("page", page);
		return ResponseEntity.ok(page);
	}

	@GetMapping("/view/{id}")
	public ResponseEntity<Role> view(@PathVariable @NotBlank @NotNull Long id) {
		Role role = null;
		if (id != null && id > 0) {
			role = roleService.get(id);
		}
		return ResponseEntity.ok(role);
	}

	@PutMapping("/update")
	public ResponseEntity<Role> update(@RequestBody Role role) {
		if (Role.ADMIN_ID == role.getId()) {
			throw new BlogException("Administrator role is not editable");
		} else {
			role = roleService.update(role);
		}
		return ResponseEntity.ok(role);
	}

	@PutMapping("/activate/{id}/{active}")
	public Result<Object> activate(@PathVariable Long id, @PathVariable Boolean active) {
		if (id != null && id != Role.ADMIN_ID) {
			roleService.activate(id, active);
		}
		return Result.success();
	}

	@DeleteMapping("/delete/{id}")
	public Result<Object> delete(@PathVariable Long id) {
		if (Role.ADMIN_ID == id)
			throw new BlogException("Administrator cannot operate");

		roleService.delete(id);

		return Result.success();
	}
}
