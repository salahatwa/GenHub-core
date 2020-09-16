package com.genhub.repository.blog;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.genhub.domain.blog.Channel;

public interface ChannelRepository extends JpaRepository<Channel, Integer>, JpaSpecificationExecutor<Channel> {
	
	List<Channel> findAllByStatus(int status, Sort sort);
	Page<Channel> findAllByStatus(int status, Pageable pageable);

	@Query("select coalesce(max(weight), 0) from Channel")
	int maxWeight();
}
