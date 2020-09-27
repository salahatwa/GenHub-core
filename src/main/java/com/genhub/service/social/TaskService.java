package com.genhub.service.social;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.genhub.domain.social.Provider;
import com.genhub.domain.social.Task;
import com.genhub.exceptions.ResourceNotFoundException;
import com.genhub.repository.social.ProviderRepository;
import com.genhub.repository.social.TaskRepository;
import com.genhub.service.social.schedule.ScheduleService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TaskService {

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private TaskRepository taskRepo;

	@Autowired
	private ProviderRepository providerRepository;

	public Task getTaskForUser(long userId, long taskId) {

		Task task = taskRepo.findByIdAndCreatedBy(taskId, userId).orElseThrow(() -> new ResourceNotFoundException(""));

		return task;
	}

	public Page<Task> getAllTasksForUser(long userId, Integer pageNo, Integer pageSize, String sortBy) {
		Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, sortBy));

		Page<Task> pagedResult = taskRepo.findByCreatedBy(userId, paging);

		return pagedResult;
	}

	public Task save(long userId, Task taskDto) {
		log.info("Task:{}", taskDto);
		log.info("Task:{}", taskDto.getCreatedBy());

		Task task = new Task();

		if (Objects.nonNull(taskDto) && Objects.nonNull(taskDto.getId()) && taskDto.getId() != 0) {
			task = taskRepo.findById(taskDto.getId()).orElseThrow(() -> new ResourceNotFoundException("NotFound"));
		}

		task = taskCopy(task, taskDto);

		task = taskRepo.save(task);

		if (taskDto.isEnabled()) {
			List<Provider> providers = providerRepository.findAllProvidersByTaskId(task.getId());
			task = scheduleService.schedulePost(providers, task);
		}

		return task;
	}

	private Task taskCopy(Task task, Task taskDto) {
		task.setContent(taskDto.getContent());
		task.setDate(taskDto.getDate());
		task.setEnabled(taskDto.isEnabled());
		task.setTime(taskDto.getTime());
		task.setTimezoneOffset(taskDto.getTimezoneOffset());
		task.setImg(taskDto.getImg());
		task.setLatitude(taskDto.getLatitude());
		task.setLongitude(task.getLongitude());
		return task;
	}

	public void delete(long id) {
		taskRepo.deleteById(id);
	}

	public void addProviderToTask(long userId, long taskId, String providerId) {

		Task task = taskRepo.findByIdAndCreatedBy(taskId, userId).orElseThrow(() -> new ResourceNotFoundException(""));

		Provider provider = providerRepository.findById(providerId)
				.orElseThrow(() -> new ResourceNotFoundException(""));

		task.addProvider(provider);

		taskRepo.save(task);

		System.out.println(task);

//		return dto;
	}

	public void removeProviderToTask(long userId, long taskId, String providerId) {

		Task task = taskRepo.findByIdAndCreatedBy(taskId, userId).orElseThrow(() -> new ResourceNotFoundException(""));

		Provider provider = providerRepository.findById(providerId)
				.orElseThrow(() -> new ResourceNotFoundException(""));

		task.removeProvider(provider);

		taskRepo.save(task);

		System.out.println(task);

//		return dto;
	}

}
