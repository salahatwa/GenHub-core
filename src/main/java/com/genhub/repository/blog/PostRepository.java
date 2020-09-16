package com.genhub.repository.blog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.genhub.domain.blog.Post;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

	Page<Post> findAllByAuthorId(Pageable pageable, long authorId);

	@Query("select coalesce(max(weight), 0) from Post")
	int maxWeight();

	@Modifying
	@Query("update Post set views = views + :increment where id = :id")
	void updateViews(@Param("id") long id, @Param("increment") int increment);

	@Modifying
	@Query("update Post set favors = favors + :increment where id = :id")
	void updateFavors(@Param("id") long id, @Param("increment") int increment);

	@Modifying
	@Query("update Post set comments = comments + :increment where id = :id")
	void updateComments(@Param("id") long id, @Param("increment") int increment);

}
