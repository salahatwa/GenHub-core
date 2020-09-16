package com.genhub.service.blog.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.genhub.domain.Role;
import com.genhub.domain.User;
import com.genhub.domain.dto.blog.AccountProfile;
import com.genhub.domain.dto.blog.BadgesCount;
import com.genhub.domain.dto.blog.UserVO;
import com.genhub.exceptions.BlogException;
import com.genhub.exceptions.ResourceNotFoundException;
import com.genhub.repository.blog.UserRepository;
import com.genhub.service.blog.MessageService;
import com.genhub.service.blog.UserRoleService;
import com.genhub.service.blog.UserService;
import com.genhub.utils.BeanMapUtils;
import com.genhub.utils.EntityStatus;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private MessageService messageService;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	private UserRoleService userRoleService;

	@Override
	public Page<UserVO> paging(Pageable pageable, String name) {
		Page<User> page = userRepository.findAll((root, query, builder) -> {
			Predicate predicate = builder.conjunction();

			if (StringUtils.isNoneBlank(name)) {
				predicate.getExpressions().add(builder.like(root.get("name"), "%" + name + "%"));
			}

			query.orderBy(builder.desc(root.get("id")));
			return predicate;
		}, pageable);

		List<UserVO> rets = new ArrayList<>();
		page.getContent().forEach(n -> rets.add(BeanMapUtils.copy(n)));
		return new PageImpl<>(rets, pageable, page.getTotalElements());
	}

	@Override
	public Map<Long, UserVO> findMapByIds(Set<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyMap();
		}
		List<User> list = userRepository.findAllById(ids);
		Map<Long, UserVO> ret = new HashMap<>();

		list.forEach(po -> ret.put(po.getId(), BeanMapUtils.copy(po)));
		return ret;
	}

	@Override
	@Transactional
	public AccountProfile login(String username, String password) {
		User po = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException(""));

		if (null == po) {
			return null;
		}

		Assert.state(StringUtils.equals(po.getPassword(), password), "wrong password");

		po.setLastLogin(Calendar.getInstance().getTime());
		userRepository.save(po);
		AccountProfile u = BeanMapUtils.copyPassport(po);

		BadgesCount badgesCount = new BadgesCount();
		badgesCount.setMessages(messageService.unread4Me(u.getId()));

		u.setBadgesCount(badgesCount);
		return u;
	}

	@Override
	@Transactional
	public AccountProfile findProfile(long id) {
		User po = userRepository.findById(id).orElse(null);

		Assert.notNull(po, "Account does not exist");

		po.setLastLogin(Calendar.getInstance().getTime());

		AccountProfile u = BeanMapUtils.copyPassport(po);

		BadgesCount badgesCount = new BadgesCount();
		badgesCount.setMessages(messageService.unread4Me(u.getId()));

		u.setBadgesCount(badgesCount);

		return u;
	}

	@Override
	@Transactional
	public UserVO register(UserVO user) {
		Assert.notNull(user, "Parameter user can not be null!");

		Assert.hasLength(user.getUsername(), "Username cannot be empty!");
		Assert.hasLength(user.getPassword(), "Password cannot be empty!");

		boolean check = userRepository.findByUsername(user.getUsername()).isPresent();

		Assert.isTrue(!check, "Username already exists!");

		if (StringUtils.isNotBlank(user.getEmail())) {
			boolean emailCheck = userRepository.findByEmail(user.getEmail()).isPresent();
			Assert.isTrue(!emailCheck, "email already exists!");
		}
		User po = new User();

		BeanUtils.copyProperties(user, po);

		if (StringUtils.isBlank(po.getName())) {
			po.setName(user.getUsername());
		}

		po.setPassword(passwordEncoder.encode((user.getPassword())));
		po.setStatus(EntityStatus.ENABLED);

		try {

			po = userRepository.save(po);

			userRoleService.updateRole(po.getId(), Collections.singleton(Role.USER_ID));

			user = BeanMapUtils.copy(po);

			Role userRole = userRoleService.findRole(Role.USER_ID);
			user.setAuthorities(BeanMapUtils.roleToAuthiority(Collections.singletonList(userRole)));

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return user;
	}

	@Override
	@Transactional
	public AccountProfile update(UserVO user) {
		User po = userRepository.findById(user.getId()).get();
		po.setName(user.getName());
		po.setSignature(user.getSignature());
		userRepository.save(po);
		return BeanMapUtils.copyPassport(po);
	}

	@Override
	@Transactional
	public AccountProfile updateEmail(long id, String email) {
		User po = userRepository.findById(id).get();

		if (email.equals(po.getEmail())) {
			throw new BlogException("Email address has not been changed");
		}

		User check = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(""));

		if (check != null && check.getId() != po.getId()) {
			throw new BlogException("The email address has already been used");
		}
		po.setEmail(email);
		userRepository.save(po);
		return BeanMapUtils.copyPassport(po);
	}

	@Override
	public UserVO get(long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));
		UserVO vo = BeanMapUtils.copy(user);

		vo.setAuthorities(BeanMapUtils.roleToAuthiority(userRoleService.listRoles(vo.getId())));

		return vo;
	}

	@Override
	public UserVO findByUsernameOrEmail(String usernameOrEmail) {

		User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElseThrow(
				() -> new UsernameNotFoundException("User not found with username or email : " + usernameOrEmail));

		UserVO vo = BeanMapUtils.copy(user);

		System.out.println(userRoleService.listRoles(vo.getId()));
		vo.setAuthorities(BeanMapUtils.roleToAuthiority(userRoleService.listRoles(vo.getId())));

		return vo;
	}

	@Override
	public UserVO getByUsername(String username) {
		return BeanMapUtils
				.copy(userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("")));
	}

	@Override
	public UserVO getByEmail(String email) {
		return BeanMapUtils
				.copy(userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("")));
	}

	@Override
	@Transactional
	public AccountProfile updateAvatar(long id, String path) {
		User po = userRepository.findById(id).get();
		po.setAvatar(path);
		userRepository.save(po);
		return BeanMapUtils.copyPassport(po);
	}

	@Override
	@Transactional
	public void updatePassword(long id, String newPassword) {
		User po = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "Id", id));

		Assert.hasLength(newPassword, "password can not be blank!");

		po.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(po);
	}

	@Override
	@Transactional
	public void updatePassword(long id, String oldPassword, String newPassword) {
		User po = userRepository.findById(id).get();

		Assert.hasLength(newPassword, "password can not be blank!");

		Assert.isTrue(passwordEncoder.matches(oldPassword, po.getPassword()), "The current password is incorrect");
		po.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(po);
	}

	@Override
	@Transactional
	public void updateStatus(long id, int status) {
		User po = userRepository.findById(id).get();

		po.setStatus(status);
		userRepository.save(po);
	}

	@Override
	public long count() {
		return userRepository.count();
	}

}
