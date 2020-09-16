package com.genhub.service.blog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.genhub.domain.dto.blog.PostTagVO;
import com.genhub.domain.dto.blog.TagVO;

public interface TagService {
	Page<TagVO> pagingQueryTags(Pageable pageable);

	Page<PostTagVO> pagingQueryPosts(Pageable pageable, String tagName);

	void batchUpdate(String names, long latestPostId);

	void deteleMappingByPostId(long postId);
}
