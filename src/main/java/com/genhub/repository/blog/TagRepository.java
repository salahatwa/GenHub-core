package com.genhub.repository.blog;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.genhub.domain.blog.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag> {
	Tag findByName(String name);

	@Modifying
	@Query("update Tag set posts = posts - 1 where id in (:ids) and posts > 0")
	int decrementPosts(@Param("ids") Collection<Long> ids);
}
