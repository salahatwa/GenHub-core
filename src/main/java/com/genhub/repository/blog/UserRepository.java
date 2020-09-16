package com.genhub.repository.blog;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.genhub.domain.User;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
	Optional<User> findByUsernameOrEmail(String username, String email);

	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);

	@Modifying
	@Query("update User set posts = posts + :increment where id = :id")
	int updatePosts(@Param("id") long id, @Param("increment") int increment);

	@Modifying
	@Query("update User set comments = comments + :increment where id in (:ids)")
	int updateComments(@Param("ids") Collection<Long> ids, @Param("increment") int increment);

}
