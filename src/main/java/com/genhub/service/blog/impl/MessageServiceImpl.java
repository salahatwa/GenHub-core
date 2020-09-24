package com.genhub.service.blog.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genhub.domain.blog.Message;
import com.genhub.domain.blog.dto.MessageVO;
import com.genhub.domain.blog.dto.PostVO;
import com.genhub.domain.blog.dto.UserVO;
import com.genhub.repository.blog.MessageRepository;
import com.genhub.service.blog.MessageService;
import com.genhub.service.blog.PostService;
import com.genhub.service.blog.UserService;
import com.genhub.utils.BeanMapUtils;
import com.genhub.utils.BlogConsts;

/**
 * @author langhsu
 */
@Service
@Transactional(readOnly = true)
public class MessageServiceImpl implements MessageService {
	@Autowired
	private MessageRepository messageRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private PostService postService;

	@Override
	public Page<MessageVO> pagingByUserId(Pageable pageable, long userId) {
		Page<Message> page = messageRepository.findAllByUserId(pageable, userId);

		Set<Long> postIds = new HashSet<>();
		Set<Long> fromUserIds = new HashSet<>();

		// 筛选
		List<MessageVO> rets = page.stream().map(po -> {
			if (po.getPostId() > 0) {
				postIds.add(po.getPostId());
			}
			if (po.getFromId() > 0) {
				fromUserIds.add(po.getFromId());
			}

			return BeanMapUtils.copy(po);
		}).collect(Collectors.toList());

		// 加载
		Map<Long, PostVO> posts = postService.findMapByIds(postIds);
		Map<Long, UserVO> fromUsers = userService.findMapByIds(fromUserIds);

		rets.forEach(n -> {
			if (n.getPostId() > 0) {
				n.setPost(posts.get(n.getPostId()));
			}
			if (n.getFromId() > 0) {
				n.setFrom(fromUsers.get(n.getFromId()));
			}
		});

		return new PageImpl<>(rets, pageable, page.getTotalElements());
	}

	@Override
	@Transactional
	public void send(MessageVO message) {
		if (message == null || message.getUserId() <= 0 || message.getFromId() <= 0) {
			return;
		}

		Message po = new Message();
		BeanUtils.copyProperties(message, po);
		po.setCreated(new Date());

		messageRepository.save(po);
	}

	@Override
	public int unread4Me(long userId) {
		return messageRepository.countByUserIdAndStatus(userId, BlogConsts.UNREAD);
	}

	@Override
	@Transactional
	public void readed4Me(long userId) {
		messageRepository.updateReadedByUserId(userId);
	}

	@Override
	@Transactional
	public int deleteByPostId(long postId) {
		return messageRepository.deleteByPostId(postId);
	}
}
