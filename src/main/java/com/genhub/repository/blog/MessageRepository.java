package com.genhub.repository.blog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.genhub.domain.blog.Message;

public interface MessageRepository extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message> {
	Page<Message> findAllByUserId(Pageable pageable, long userId);

	int countByUserIdAndStatus(long userId, int status);

	@Modifying
	@Query("update Message n set n.status = 1 where n.status = 0 and n.userId = :uid")
	int updateReadedByUserId(@Param("uid") long uid);

	int deleteByPostId(long postId);
}
