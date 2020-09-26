package com.genhub.service.social.schedule;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.genhub.domain.social.Provider;
import com.genhub.domain.social.Task;
import com.genhub.repository.social.ProviderRepository;
import com.genhub.repository.social.TaskRepository;
import com.genhub.service.social.SocialPuplisher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class ScheduleService {

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	ProviderRepository providerRepository;

	@Autowired
	private SocialPuplisher publisher;

	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

	private Map<Long, ScheduledFuture<?>> tasks = new HashMap<>();

	/**
	 * Schedule task with [ specific date time ]
	 * 
	 * @param myTask
	 * @return
	 */
	@Transactional
	public Task schedulePost(List<Provider> providers, Task myTask) {

		int delay = getDelay(myTask);

		if (delay < 0) {
			myTask.setError(true);
			log.info("Delay " + delay + " post not scheduled");
			return myTask;
		}

		TimerTask task = new TaskExecution(providers, myTask, new TaskActionsSocialImpl(publisher), taskRepository);
		ScheduledFuture<?> scheduledFuture = scheduler.schedule(task, delay, TimeUnit.SECONDS);
		log.info("Post " + myTask.getId() + " scheduled");
		if (myTask.isScheduled()) {
			cancelScheduling(myTask);
			myTask.setScheduled(true);
			tasks.put(myTask.getId(), scheduledFuture);
			return myTask;
		} else if (myTask.isEnabled() && !myTask.isScheduled()) {
			myTask.setScheduled(true);
			tasks.put(myTask.getId(), scheduledFuture);
		}

		return myTask;
	}

	public Task cancelScheduling(Task task) {
		try {
			tasks.get(task.getId()).cancel(true);
			tasks.remove(task.getId());
			task.setScheduled(false);
			log.info("Scheduling of post " + task.getId() + " stopped");
		} catch (NullPointerException ne) {
			log.info("Post " + task.getId() + " already canceled");
		}

		return task;
	}

	public int getDelay(Task task) {
		log.info("" + getZoneIdFromTimezoneOffset(task.getTimezoneOffset()));
		String dateNow = DateTimeFormatter.ofPattern("yyyy-MM-dd")
				.format(LocalDateTime.now(getZoneIdFromTimezoneOffset(task.getTimezoneOffset())));
		String timeNow = DateTimeFormatter.ofPattern("HH:mm:ss")
				.format(LocalDateTime.now(getZoneIdFromTimezoneOffset(task.getTimezoneOffset())));
		log.info("SERVER-TIME: " + timeNow);
		log.info("SERVER-DATE: " + dateNow);
		String datePost = task.getDate();
		String timePost = task.getTime() + ":00";

		LocalDateTime d1 = LocalDateTime.parse(datePost + " " + timePost,
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LocalDateTime d2 = LocalDateTime.parse(dateNow + " " + timeNow,
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		Duration diff = Duration.between(d2, d1);
		log.info("Delay: " + (int) diff.getSeconds() + "s");
		return (int) diff.getSeconds();
	}

	public Map<Long, ScheduledFuture<?>> getTasks() {
		return tasks;
	}

	public void scheduleAll(String userId) {
		List<Task> toSchedule = taskRepository.findByEnabledAndExecutedAndError(true, false, false);

		if (toSchedule != null)
			toSchedule.forEach(post -> {
				List<Provider> providers = providerRepository.findAllProvidersByTaskId(post.getId());
				schedulePost(providers, post);
			});
	}

	public void killAllTasks() {

		List<Long> toRemove = new ArrayList<>();

		for (Map.Entry<Long, ScheduledFuture<?>> entry : tasks.entrySet()) {
			Optional<Task> taskOptional = taskRepository.findById(entry.getKey());
			Task task = taskOptional.get();
			tasks.get(task.getId()).cancel(true);
			toRemove.add(task.getId());
		}

		toRemove.forEach(id -> tasks.remove(id));
	}

	private ZoneId getZoneIdFromTimezoneOffset(int timeZone) {

		return ZoneId.ofOffset("UTC", ZoneOffset.ofTotalSeconds(timeZone * -60));

	}
}
