package com.genhub.service.blog;

import java.util.Map;
import java.util.Set;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.genhub.domain.dto.blog.AccountProfile;
import com.genhub.domain.dto.blog.UserVO;
import com.genhub.utils.BlogConsts;

@CacheConfig(cacheNames = BlogConsts.CACHE_USER)
public interface UserService {

	Page<UserVO> paging(Pageable pageable, String name);

	Map<Long, UserVO> findMapByIds(Set<Long> ids);

	AccountProfile login(String username, String password);

	AccountProfile findProfile(long id);

	UserVO register(UserVO user);

	@CacheEvict(key = "#user.getId()")
	AccountProfile update(UserVO user);

	@CacheEvict(key = "#id")
	AccountProfile updateEmail(long id, String email);

	@Cacheable(key = "#userId")
	UserVO get(long userId);

	UserVO getByUsername(String username);

	UserVO findByUsernameOrEmail(String username);

	UserVO getByEmail(String email);

	@CacheEvict(key = "#id")
	AccountProfile updateAvatar(long id, String path);

	void updatePassword(long id, String newPassword);

	void updatePassword(long id, String oldPassword, String newPassword);

	void updateStatus(long id, int status);

	long count();

}
