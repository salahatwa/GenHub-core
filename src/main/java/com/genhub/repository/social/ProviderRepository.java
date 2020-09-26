package com.genhub.repository.social;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.genhub.domain.social.Provider;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, String> {

	List<Provider> findByIdIn(List<String> ids);

	List<Provider> findByCreatedBy(long createdBy);

	Page<Provider> findByCreatedBy(long createdBy, Pageable paging);

	@Query(value = "SELECT * FROM provider provider LEFT join tasks_providers tasks_providers ON tasks_providers.task_id= :taskId", nativeQuery = true)
	List<Provider> findAllProvidersByTaskId(@Param("taskId") long taskId);

}
