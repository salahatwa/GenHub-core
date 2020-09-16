package com.genhub.service.blog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.genhub.domain.blog.Favorite;
import com.genhub.domain.dto.blog.FavoriteVO;

public interface FavoriteService {
	Page<FavoriteVO> pagingByUserId(Pageable pageable, long userId);

	Favorite add(long userId, long postId);

	void delete(long userId, long postId);

	void deleteByPostId(long postId);
}
