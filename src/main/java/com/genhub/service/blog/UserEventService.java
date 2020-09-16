package com.genhub.service.blog;

import java.util.Set;

import org.springframework.cache.annotation.CacheEvict;

import com.genhub.utils.BlogConsts;

public interface UserEventService {
	@CacheEvict(value = { BlogConsts.CACHE_USER, BlogConsts.CACHE_POST }, allEntries = true)
	void identityPost(long userId, boolean plus);

	@CacheEvict(value = { BlogConsts.CACHE_USER, BlogConsts.CACHE_POST }, allEntries = true)
	void identityComment(long userId, boolean plus);

	@CacheEvict(value = { BlogConsts.CACHE_USER, BlogConsts.CACHE_POST }, allEntries = true)
	void identityComment(Set<Long> userIds, boolean plus);
}
