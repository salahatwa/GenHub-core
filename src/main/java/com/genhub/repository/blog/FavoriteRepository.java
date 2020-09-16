package com.genhub.repository.blog;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.genhub.domain.blog.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long>, JpaSpecificationExecutor<Favorite> {
	Optional<Favorite> findByUserIdAndPostId(long userId, long postId);

	Page<Favorite> findAllByUserId(Pageable pageable, long userId);

	int deleteByPostId(long postId);
}
