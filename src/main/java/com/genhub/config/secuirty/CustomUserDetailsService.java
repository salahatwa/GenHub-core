package com.genhub.config.secuirty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genhub.service.blog.UserService;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserService userService;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
		return UserPrincipal.create(userService.findByUsernameOrEmail(usernameOrEmail));
	}

	@Transactional
	public UserDetails loadUserById(long id) {

		return UserPrincipal.create(userService.get(id));
	}
}