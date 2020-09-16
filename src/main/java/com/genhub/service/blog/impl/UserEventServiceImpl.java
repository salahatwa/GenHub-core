package com.genhub.service.blog.impl;

import java.util.Collections;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genhub.repository.blog.UserRepository;
import com.genhub.service.blog.UserEventService;
import com.genhub.utils.BlogConsts;

@Service
@Transactional(rollbackFor = Throwable.class)
public class UserEventServiceImpl implements UserEventService {
	@Autowired
	private UserRepository userRepository;

	@Override
	public void identityPost(long userId, boolean plus) {
		userRepository.updatePosts(userId, (plus) ? BlogConsts.IDENTITY_STEP : BlogConsts.DECREASE_STEP);
	}

	@Override
	public void identityComment(long userId, boolean plus) {
		userRepository.updateComments(Collections.singleton(userId),
				(plus) ? BlogConsts.IDENTITY_STEP : BlogConsts.DECREASE_STEP);
	}

	@Override
	public void identityComment(Set<Long> userIds, boolean plus) {
		userRepository.updateComments(userIds, (plus) ? BlogConsts.IDENTITY_STEP : BlogConsts.DECREASE_STEP);
	}

}
