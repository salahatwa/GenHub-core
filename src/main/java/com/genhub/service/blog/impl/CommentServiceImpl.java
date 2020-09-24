package com.genhub.service.blog.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.genhub.domain.blog.Comment;
import com.genhub.domain.blog.dto.CommentComplementor;
import com.genhub.domain.blog.dto.CommentVO;
import com.genhub.exceptions.ResourceNotFoundException;
import com.genhub.repository.blog.CommentRepository;
import com.genhub.service.blog.CommentService;
import com.genhub.service.blog.UserEventService;

@Service
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private UserEventService userEventService;

	@Override
	public Page<CommentVO> paging4Admin(Pageable pageable) {
		Page<Comment> page = commentRepository.findAll(pageable);
		List<CommentVO> rets = CommentComplementor.of(page.getContent()).flutBuildUser().getComments();
		return new PageImpl<>(rets, pageable, page.getTotalElements());
	}

	@Override
	public Page<CommentVO> pagingByAuthorId(Pageable pageable, long authorId) {
		Page<Comment> page = commentRepository.findAllByAuthorId(pageable, authorId);

		List<CommentVO> rets = CommentComplementor.of(page.getContent()).flutBuildUser().flutBuildParent()
				.flutBuildPost().getComments();
		return new PageImpl<>(rets, pageable, page.getTotalElements());
	}

	@Override
	public Page<CommentVO> pagingByPostId(Pageable pageable, long postId) {
		Page<Comment> page = commentRepository.findAllByPostId(pageable, postId);

		List<CommentVO> rets = CommentComplementor.of(page.getContent()).flutBuildUser().flutBuildParent()
				.getComments();
		return new PageImpl<>(rets, pageable, page.getTotalElements());
	}

	@Override
	public List<CommentVO> findLatestComments(int maxResults) {
		Pageable pageable = PageRequest.of(0, maxResults, Sort.by(Sort.Direction.DESC, "id"));
		Page<Comment> page = commentRepository.findAll(pageable);
		return CommentComplementor.of(page.getContent()).flutBuildUser().getComments();
	}

	@Override
	public Map<Long, CommentVO> findByIds(Set<Long> ids) {
		List<Comment> list = commentRepository.findAllById(ids);
		return CommentComplementor.of(list).flutBuildUser().toMap();
	}

	@Override
	public Comment findById(long id) {
		return commentRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public long post(CommentVO comment) {
		Comment po = new Comment();

		po.setAuthorId(comment.getAuthorId());
		po.setPostId(comment.getPostId());
		po.setContent(comment.getContent());
		po.setCreated(new Date());
		po.setPid(comment.getPid());
		commentRepository.save(po);

		userEventService.identityComment(comment.getAuthorId(), true);
		return po.getId();
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void delete(List<Long> ids) {
		try {
			List<Comment> list = commentRepository.removeByIdIn(ids);
			if (!CollectionUtils.isEmpty(list)) {
				list.forEach(po -> {
					userEventService.identityComment(po.getAuthorId(), false);
				});
			}
		} catch (Exception ex) {
			throw new ResourceNotFoundException("Comment", "Id", ids);
		}
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void delete(long id, long authorId) {
		Comment comment = commentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Comment", "Id", id));
		Assert.isTrue(comment.getAuthorId() == authorId, "Authentication failed");
		commentRepository.deleteById(id);

		userEventService.identityComment(authorId, false);
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void deleteByPostId(long postId) {
		List<Comment> list = commentRepository.removeByPostId(postId);
		if (!CollectionUtils.isEmpty(list)) {
			Set<Long> userIds = new HashSet<>();
			list.forEach(n -> userIds.add(n.getAuthorId()));
			userEventService.identityComment(userIds, false);
		}
	}

	@Override
	public long count() {
		return commentRepository.count();
	}

	@Override
	public long countByAuthorIdAndPostId(long authorId, long toId) {
		return commentRepository.countByAuthorIdAndPostId(authorId, toId);
	}

}
