package com.genhub.repository.social;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.genhub.domain.social.Task;

public interface TaskRepository extends CrudRepository<Task, Long> {

//	Task findByIdAndUserId(int postId, int userId);

//	List<Task> findByScheduledAndUserId(boolean scheduled, int userId);
//
//	List<Task> findByExecutedAndUserId(boolean posted, int userId);
//
//	List<Task> findByEnabledAndUserId(boolean enabled, int userId);
//
//	List<Task> findByErrorAndUserId(boolean error, int userId);
//
//	List<Task> findByEnabledAndExecutedAndUserId(boolean enabled, boolean posted, int userId);

	List<Task> findByEnabledAndExecutedAndError(boolean enabled, boolean posted, boolean error);

	List<Task> findByEnabled(boolean enabled);

	List<Task> findByExecuted(boolean enabled);

	List<Task> findByScheduled(boolean enabled);

	List<Task> findByError(boolean error);

	Page<Task> findByCreatedBy(String userId, Pageable paging);

	Optional<Task> findByIdAndCreatedBy(long taskId, String userId);

}
