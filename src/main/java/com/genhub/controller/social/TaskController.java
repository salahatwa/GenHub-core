package com.genhub.controller.social;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.genhub.config.secuirty.CurrentUser;
import com.genhub.config.secuirty.UserPrincipal;
import com.genhub.domain.social.Task;
import com.genhub.service.social.TaskService;
import com.genhub.utils.Constant;

@RestController
@RequestMapping("/task")
public class TaskController {

	@Autowired
	private TaskService taskService;

	@GetMapping("/list")
	public ResponseEntity<Page<Task>> getAllUserTasks(@CurrentUser UserPrincipal currentUser,
			@RequestParam(value = "page", defaultValue = Constant.DEFAULT_PAGE_NUMBER) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = Constant.DEFAULT_PAGE_SIZE) int pageSize,
			@RequestParam(defaultValue = "createdAt") String sortBy) {

		return ResponseEntity.ok(taskService.getAllTasksForUser(currentUser.getId(), pageNo, pageSize, sortBy));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Task> getUserTask(@CurrentUser UserPrincipal currentUser, @PathVariable long id) {

		return ResponseEntity.ok(taskService.getTaskForUser(currentUser.getId(), id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@CurrentUser UserPrincipal currentUser, @PathVariable long id) {

		taskService.delete(id);

		return ResponseEntity.ok(true);
	}

	@PostMapping
	public ResponseEntity<Task> save(@CurrentUser UserPrincipal currentUser, @Valid @RequestBody Task taskDto) {

		return ResponseEntity.ok(taskService.save(currentUser.getId(), taskDto));
	}

	@GetMapping("/provider/{taskId}/{providerId}")
	public ResponseEntity<?> addProviderToTask(@CurrentUser UserPrincipal currentUser, @PathVariable long taskId,
			@PathVariable String providerId) {

//		addProviderToTask
		taskService.addProviderToTask(currentUser.getId(), taskId, providerId);

		return ResponseEntity.ok(true);
	}

	@DeleteMapping("/provider/{taskId}/{providerId}")
	public ResponseEntity<?> deleteProviderToTask(@CurrentUser UserPrincipal currentUser, @PathVariable long taskId,
			@PathVariable String providerId) {

		taskService.removeProviderToTask(currentUser.getId(), taskId, providerId);

		return ResponseEntity.ok(true);
	}

}
