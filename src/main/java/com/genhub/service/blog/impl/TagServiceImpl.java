package com.genhub.service.blog.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.genhub.domain.blog.PostTag;
import com.genhub.domain.blog.Tag;
import com.genhub.domain.dto.blog.PostTagVO;
import com.genhub.domain.dto.blog.PostVO;
import com.genhub.domain.dto.blog.TagVO;
import com.genhub.repository.blog.PostTagRepository;
import com.genhub.repository.blog.TagRepository;
import com.genhub.service.blog.PostService;
import com.genhub.service.blog.TagService;
import com.genhub.utils.BeanMapUtils;
import com.genhub.utils.BlogConsts;

@Service
@Transactional(readOnly = true)
public class TagServiceImpl implements TagService {
	@Autowired
	private TagRepository tagRepository;
	@Autowired
	private PostTagRepository postTagRepository;
	@Autowired
	private PostService postService;

	@Override
	public Page<TagVO> pagingQueryTags(Pageable pageable) {
		Page<Tag> page = tagRepository.findAll(pageable);

		Set<Long> postIds = new HashSet<>();
		List<TagVO> rets = page.getContent().stream().map(po -> {
			postIds.add(po.getLatestPostId());
			return BeanMapUtils.copy(po);
		}).collect(Collectors.toList());

		Map<Long, PostVO> posts = postService.findMapByIds(postIds);
		rets.forEach(n -> n.setPost(posts.get(n.getLatestPostId())));
		return new PageImpl<>(rets, pageable, page.getTotalElements());
	}

	@Override
	public Page<PostTagVO> pagingQueryPosts(Pageable pageable, String tagName) {
		Tag tag = tagRepository.findByName(tagName);
		Assert.notNull(tag, "Tag does not exist");
		Page<PostTag> page = postTagRepository.findAllByTagId(pageable, tag.getId());

		Set<Long> postIds = new HashSet<>();
		List<PostTagVO> rets = page.getContent().stream().map(po -> {
			postIds.add(po.getPostId());
			return BeanMapUtils.copy(po);
		}).collect(Collectors.toList());

		Map<Long, PostVO> posts = postService.findMapByIds(postIds);
		rets.forEach(n -> n.setPost(posts.get(n.getPostId())));
		return new PageImpl<>(rets, pageable, page.getTotalElements());
	}

	@Override
	@Transactional
	public void batchUpdate(String names, long latestPostId) {
		if (StringUtils.isBlank(names.trim())) {
			return;
		}

		String[] ns = names.split(BlogConsts.SEPARATOR);
		Date current = new Date();
		for (String n : ns) {
			String name = n.trim();
			if (StringUtils.isBlank(name)) {
				continue;
			}

			Tag po = tagRepository.findByName(name);
			if (po != null) {
				PostTag pt = postTagRepository.findByPostIdAndTagId(latestPostId, po.getId());
				if (null != pt) {
					pt.setWeight(System.currentTimeMillis());
					postTagRepository.save(pt);
					continue;
				}
				po.setPosts(po.getPosts() + 1);
				po.setUpdated(current);
			} else {
				po = new Tag();
				po.setName(name);
				po.setCreated(current);
				po.setUpdated(current);
				po.setPosts(1);
			}

			po.setLatestPostId(latestPostId);
			tagRepository.save(po);

			PostTag pt = new PostTag();
			pt.setPostId(latestPostId);
			pt.setTagId(po.getId());
			pt.setWeight(System.currentTimeMillis());
			postTagRepository.save(pt);
		}
	}

	@Override
	@Transactional
	public void deteleMappingByPostId(long postId) {
		Set<Long> tagIds = postTagRepository.findTagIdByPostId(postId);
		if (CollectionUtils.isNotEmpty(tagIds)) {
			tagRepository.decrementPosts(tagIds);
		}
		postTagRepository.deleteByPostId(postId);
	}
}
