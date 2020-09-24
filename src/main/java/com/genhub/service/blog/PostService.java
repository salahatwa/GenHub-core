
package com.genhub.service.blog;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.genhub.domain.blog.dto.PostVO;
import com.genhub.utils.BlogConsts;

@CacheConfig(cacheNames = BlogConsts.CACHE_USER)
public interface PostService {
	@Cacheable
	Page<PostVO> paging(Pageable pageable, int channelId, Set<Integer> excludeChannelIds);

	Page<PostVO> paging4Admin(Pageable pageable, int channelId, String title);

	@Cacheable
	Page<PostVO> pagingByAuthorId(Pageable pageable, long userId);

	@Cacheable(key = "'latest_' + #maxResults")
	List<PostVO> findLatestPosts(int maxResults);

	@Cacheable(key = "'hottest_' + #maxResults")
	List<PostVO> findHottestPosts(int maxResults);

	Map<Long, PostVO> findMapByIds(Set<Long> ids);

	@CacheEvict(allEntries = true)
	long post(PostVO post);

	@Cacheable(key = "'post_' + #id")
	PostVO get(long id);

	@CacheEvict(allEntries = true)
	void update(PostVO p);

	@CacheEvict(allEntries = true)
	void updateFeatured(long id, int featured);

	@CacheEvict(allEntries = true)
	void updateWeight(long id, int weighted);

	@CacheEvict(allEntries = true)
	void delete(long id, long authorId);

	@CacheEvict(allEntries = true)
	void delete(Collection<Long> ids);

	@CacheEvict(key = "'view_' + #id")
	void identityViews(long id);

	@CacheEvict(key = "'view_' + #id")
	void identityComments(long id);

	@CacheEvict(key = "'view_' + #postId")
	void favor(long userId, long postId);

	@CacheEvict(key = "'view_' + #postId")
	void unfavor(long userId, long postId);

	long count();
}
