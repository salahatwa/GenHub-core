package com.genhub.repository.blog;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.genhub.domain.blog.PostResource;

public interface PostResourceRepository
		extends JpaRepository<PostResource, Long>, JpaSpecificationExecutor<PostResource> {

	int deleteByPostId(long postId);

	int deleteByPostIdAndResourceIdIn(long postId, Collection<Long> resourceId);

	List<PostResource> findByResourceId(long resourceId);

	List<PostResource> findByPostId(long postId);

}
