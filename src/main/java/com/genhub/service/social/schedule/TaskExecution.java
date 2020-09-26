package com.genhub.service.social.schedule;

import java.util.List;
import java.util.TimerTask;

import javax.validation.constraints.NotNull;

import org.springframework.transaction.annotation.Transactional;

import com.genhub.domain.social.Provider;
import com.genhub.domain.social.Task;
import com.genhub.exceptions.ResourceNotFoundException;
import com.genhub.repository.social.TaskRepository;

import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

/**
 * The task that is put into the @{@link ScheduleService}
 *
 *
 */
@Log4j2
@ToString
@NoArgsConstructor
@Transactional
public class TaskExecution extends TimerTask {

	@NotNull
	private Task task;

	private TaskRepository taskRepository;
	private TaskAction taskAction;
	private List<Provider> providers;

	public TaskExecution(List<Provider> providers,Task post, TaskAction taskAction, TaskRepository taskRepository) {
		this.task = post;
		this.taskAction = taskAction;
		this.taskRepository = taskRepository;
		this.providers=providers;
	}

	/**
	 * Publishes @{@link provider} and update database
	 */
	@Transactional
	@Override
	public void run() {

		log.info("Providers:{}",providers.size());
		if (task != null && !task.isExecuted() 
				&& task.isEnabled()) {

			long id = taskAction.execute(providers,task);
			Task posted = taskRepository.findById(task.getId()).orElseThrow(() -> new ResourceNotFoundException(""));
			if (id != 0 && id > 0) {
				posted.setExecuted(true);
				posted.setEnabled(false);
				posted.setScheduled(false);
				taskRepository.save(posted);
				log.info("Post-id: " + id);
			} else {
				log.error("No id callback");
				posted.setExecuted(false);
				posted.setEnabled(false);
				posted.setScheduled(false);
				posted.setError(true);
				taskRepository.save(posted);
			}

		} else {
			log.info("Not valid to post: " + task);
		}

	}
}
