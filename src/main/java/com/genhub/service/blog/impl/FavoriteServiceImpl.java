package com.genhub.service.blog.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.genhub.domain.blog.Favorite;
import com.genhub.domain.dto.blog.FavoriteVO;
import com.genhub.domain.dto.blog.PostVO;
import com.genhub.exceptions.ResourceAlreadyExistException;
import com.genhub.exceptions.ResourceNotFoundException;
import com.genhub.repository.blog.FavoriteRepository;
import com.genhub.service.blog.FavoriteService;
import com.genhub.service.blog.PostService;
import com.genhub.utils.BeanMapUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class FavoriteServiceImpl implements FavoriteService {
	@Autowired
	private FavoriteRepository favoriteRepository;
	@Autowired
	private PostService postService;

	@Override
	public Page<FavoriteVO> pagingByUserId(Pageable pageable, long userId) {
		Page<Favorite> page = favoriteRepository.findAllByUserId(pageable, userId);

		List<FavoriteVO> rets = new ArrayList<>();
		Set<Long> postIds = new HashSet<>();
		for (Favorite po : page.getContent()) {
			rets.add(BeanMapUtils.copy(po));
			postIds.add(po.getPostId());
		}

		if (postIds.size() > 0) {
			Map<Long, PostVO> posts = postService.findMapByIds(postIds);

			for (FavoriteVO t : rets) {
				PostVO p = posts.get(t.getPostId());
				t.setPost(p);
			}
		}
		return new PageImpl<>(rets, pageable, page.getTotalElements());
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public Favorite add(long userId, long postId) {
		Optional<Favorite> po = favoriteRepository.findByUserIdAndPostId(userId, postId);

		if (po.isPresent())
			throw new ResourceAlreadyExistException("Favorited", postId);

		Favorite favorite = new Favorite();
		favorite.setUserId(userId);
		favorite.setPostId(postId);
		favorite.setCreated(new Date());

		favoriteRepository.save(favorite);

		return favorite;
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void delete(long userId, long postId) {
		Favorite po = favoriteRepository.findByUserIdAndPostId(userId, postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "Id", postId));
		favoriteRepository.delete(po);
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void deleteByPostId(long postId) {
		int rows = favoriteRepository.deleteByPostId(postId);
		log.info("favoriteRepository delete {}", rows);
	}

}
