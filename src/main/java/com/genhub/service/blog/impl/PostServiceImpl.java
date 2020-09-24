package com.genhub.service.blog.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.criteria.Predicate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.genhub.aspect.blog.PostStatusFilter;
import com.genhub.aspect.blog.PostUpdateEvent;
import com.genhub.domain.blog.Channel;
import com.genhub.domain.blog.Post;
import com.genhub.domain.blog.PostAttribute;
import com.genhub.domain.blog.PostResource;
import com.genhub.domain.blog.Resource;
import com.genhub.domain.blog.dto.PostVO;
import com.genhub.domain.blog.dto.UserVO;
import com.genhub.exceptions.ResourceNotFoundException;
import com.genhub.repository.blog.PostAttributeRepository;
import com.genhub.repository.blog.PostRepository;
import com.genhub.repository.blog.PostResourceRepository;
import com.genhub.repository.blog.ResourceRepository;
import com.genhub.service.blog.ChannelService;
import com.genhub.service.blog.FavoriteService;
import com.genhub.service.blog.PostService;
import com.genhub.service.blog.TagService;
import com.genhub.service.blog.UserService;
import com.genhub.utils.BeanMapUtils;
import com.genhub.utils.BlogConsts;
import com.genhub.utils.MarkdownUtils;
import com.genhub.utils.PreviewTextUtils;
import com.genhub.utils.ResourceLock;

/**
 * @author ssatwa
 *
 */
@Service
@Transactional
public class PostServiceImpl implements PostService {
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private PostAttributeRepository postAttributeRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private FavoriteService favoriteService;
	@Autowired
	private ChannelService channelService;
	@Autowired
	private TagService tagService;
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private PostResourceRepository postResourceRepository;
	@Autowired
	private ResourceRepository resourceRepository;

	private static Pattern pattern = Pattern.compile("(?<=/_signature/)(.+?)(?=\\.)");

	@Override
	@PostStatusFilter
	public Page<PostVO> paging(Pageable pageable, int channelId, Set<Integer> excludeChannelIds) {
		Page<Post> page = postRepository.findAll((root, query, builder) -> {
			Predicate predicate = builder.conjunction();

			if (channelId > BlogConsts.ZERO) {
				predicate.getExpressions().add(builder.equal(root.get("channelId").as(Integer.class), channelId));
			}

			if (null != excludeChannelIds && !excludeChannelIds.isEmpty()) {
				predicate.getExpressions().add(builder.not(root.get("channelId").in(excludeChannelIds)));
			}

//			predicate.getExpressions().add(
//					builder.equal(root.get("featured").as(Integer.class), BlogConsts.FEATURED_DEFAULT));

			return predicate;
		}, pageable);

		return new PageImpl<>(toPosts(page.getContent()), pageable, page.getTotalElements());
	}

	@Override
	public Page<PostVO> paging4Admin(Pageable pageable, int channelId, String title) {
		Page<Post> page = postRepository.findAll((root, query, builder) -> {
			Predicate predicate = builder.conjunction();
			if (channelId > BlogConsts.ZERO) {
				predicate.getExpressions().add(builder.equal(root.get("channelId").as(Integer.class), channelId));
			}
			if (StringUtils.isNotBlank(title)) {
				predicate.getExpressions().add(builder.like(root.get("title").as(String.class), "%" + title + "%"));
			}
			return predicate;
		}, pageable);

		return new PageImpl<>(toPosts(page.getContent()), pageable, page.getTotalElements());
	}

	@Override
	@PostStatusFilter
	public Page<PostVO> pagingByAuthorId(Pageable pageable, long userId) {
		Page<Post> page = postRepository.findAllByAuthorId(pageable, userId);
		return new PageImpl<>(toPosts(page.getContent()), pageable, page.getTotalElements());
	}

	@Override
	@PostStatusFilter
	public List<PostVO> findLatestPosts(int maxResults) {
		return find("created", maxResults).stream().map(BeanMapUtils::copy).collect(Collectors.toList());
	}

	@Override
	@PostStatusFilter
	public List<PostVO> findHottestPosts(int maxResults) {
		return find("views", maxResults).stream().map(BeanMapUtils::copy).collect(Collectors.toList());
	}

	@Override
	@PostStatusFilter
	public Map<Long, PostVO> findMapByIds(Set<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyMap();
		}

		List<Post> list = postRepository.findAllById(ids);
		Map<Long, PostVO> rets = new HashMap<>();

		HashSet<Long> uids = new HashSet<>();

		list.forEach(po -> {
			rets.put(po.getId(), BeanMapUtils.copy(po));
			uids.add(po.getAuthorId());
		});

		// 加载用户信息
		buildUsers(rets.values(), uids);
		return rets;
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public long post(PostVO post) {
		Post po = new Post();

		BeanUtils.copyProperties(post, po);

		po.setCreated(new Date());
		po.setStatus(post.getStatus());

		if (StringUtils.isBlank(post.getSummary())) {
			po.setSummary(trimSummary(post.getEditor(), post.getContent()));
		} else {
			po.setSummary(post.getSummary());
		}

		postRepository.save(po);
		tagService.batchUpdate(po.getTags(), po.getId());

		String key = ResourceLock.getPostKey(po.getId());
		AtomicInteger lock = ResourceLock.getAtomicInteger(key);
		try {
			synchronized (lock) {
				PostAttribute attr = new PostAttribute();
				attr.setContent(post.getContent());
				attr.setEditor(post.getEditor());
				attr.setId(po.getId());
				postAttributeRepository.save(attr);

				countResource(po.getId(), null, attr.getContent());
				onPushEvent(po, PostUpdateEvent.ACTION_PUBLISH);
				return po.getId();
			}
		} finally {
			ResourceLock.giveUpAtomicInteger(key);
		}
	}

	@Override
	public PostVO get(long id) {
		Post po = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "Id", id));
		PostVO d = BeanMapUtils.copy(po);

		d.setAuthor(userService.get(d.getAuthorId()));
		d.setChannel(channelService.getById(d.getChannelId()));

		PostAttribute attr = postAttributeRepository.findById(d.getId()).get();
		d.setContent(attr.getContent());
		d.setEditor(attr.getEditor());
		return d;
	}

	/**
	 * 
	 * @param p
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void update(PostVO p) {
		Optional<Post> optional = postRepository.findById(p.getId());

		if (optional.isPresent()) {
			String key = ResourceLock.getPostKey(p.getId());
			AtomicInteger lock = ResourceLock.getAtomicInteger(key);
			try {
				synchronized (lock) {
					Post po = optional.get();
					po.setTitle(p.getTitle());
					po.setChannelId(p.getChannelId());
					po.setThumbnail(p.getThumbnail());
					po.setStatus(p.getStatus());

					if (StringUtils.isBlank(p.getSummary())) {
						po.setSummary(trimSummary(p.getEditor(), p.getContent()));
					} else {
						po.setSummary(p.getSummary());
					}

					po.setTags(p.getTags());

					Optional<PostAttribute> attributeOptional = postAttributeRepository.findById(po.getId());
					String originContent = "";
					if (attributeOptional.isPresent()) {
						originContent = attributeOptional.get().getContent();
					}
					PostAttribute attr = new PostAttribute();
					attr.setContent(p.getContent());
					attr.setEditor(p.getEditor());
					attr.setId(po.getId());
					postAttributeRepository.save(attr);

					tagService.batchUpdate(po.getTags(), po.getId());

					countResource(po.getId(), originContent, p.getContent());
				}
			} finally {
				ResourceLock.giveUpAtomicInteger(key);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void updateFeatured(long id, int featured) {
		Post po = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "Id", id));
		int status = BlogConsts.FEATURED_ACTIVE == featured ? BlogConsts.FEATURED_ACTIVE : BlogConsts.FEATURED_DEFAULT;
		po.setFeatured(status);
		postRepository.save(po);
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void updateWeight(long id, int weighted) {
		Post po = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "Id", id));

		int max = BlogConsts.ZERO;
		if (BlogConsts.FEATURED_ACTIVE == weighted) {
			max = postRepository.maxWeight() + 1;
		}
		po.setWeight(max);
		postRepository.save(po);
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void delete(long id, long authorId) {
		Post po = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "Id", id));
		Assert.isTrue(po.getAuthorId() == authorId, "Authentication failed");

		String key = ResourceLock.getPostKey(po.getId());
		AtomicInteger lock = ResourceLock.getAtomicInteger(key);
		try {
			synchronized (lock) {
				postRepository.deleteById(id);
				postAttributeRepository.deleteById(id);
				cleanResource(po.getId());
				onPushEvent(po, PostUpdateEvent.ACTION_DELETE);
			}
		} finally {
			ResourceLock.giveUpAtomicInteger(key);
		}
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void delete(Collection<Long> ids) {
		try {
			if (CollectionUtils.isNotEmpty(ids)) {
				List<Post> list = postRepository.findAllById(ids);
				list.forEach(po -> {
					String key = ResourceLock.getPostKey(po.getId());
					AtomicInteger lock = ResourceLock.getAtomicInteger(key);
					try {
						synchronized (lock) {
							postRepository.delete(po);
							postAttributeRepository.deleteById(po.getId());
							cleanResource(po.getId());
							onPushEvent(po, PostUpdateEvent.ACTION_DELETE);
						}
					} finally {
						ResourceLock.giveUpAtomicInteger(key);
					}
				});
			}
		} catch (Exception ex) {
			throw new ResourceNotFoundException("Post", "Id", ids);
		}
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void identityViews(long id) {
		postRepository.updateViews(id, BlogConsts.IDENTITY_STEP);
	}

	@Override
	@Transactional
	public void identityComments(long id) {
		postRepository.updateComments(id, BlogConsts.IDENTITY_STEP);
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void favor(long userId, long postId) {
		postRepository.updateFavors(postId, BlogConsts.IDENTITY_STEP);
		favoriteService.add(userId, postId);
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void unfavor(long userId, long postId) {
		postRepository.updateFavors(postId, BlogConsts.DECREASE_STEP);
		favoriteService.delete(userId, postId);
	}

	@Override
	@PostStatusFilter
	public long count() {
		return postRepository.count();
	}

	@PostStatusFilter
	private List<Post> find(String orderBy, int size) {
		Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, orderBy));

		Set<Integer> excludeChannelIds = new HashSet<>();

		List<Channel> channels = channelService.findAll(BlogConsts.STATUS_CLOSED);
		if (channels != null) {
			channels.forEach((c) -> excludeChannelIds.add(c.getId()));
		}

		Page<Post> page = postRepository.findAll((root, query, builder) -> {
			Predicate predicate = builder.conjunction();
			if (excludeChannelIds.size() > 0) {
				predicate.getExpressions().add(builder.not(root.get("channelId").in(excludeChannelIds)));
			}
			return predicate;
		}, pageable);
		return page.getContent();
	}

	/**
	 * 截取文章内容
	 * 
	 * @param text
	 * @return
	 */
	private String trimSummary(String editor, final String text) {
		if (BlogConsts.EDITOR_MARKDOWN.endsWith(editor)) {
			return PreviewTextUtils.getText(MarkdownUtils.renderMarkdown(text), 126);
		} else {
			return PreviewTextUtils.getText(text, 126);
		}
	}

	private List<PostVO> toPosts(List<Post> posts) {
		HashSet<Long> uids = new HashSet<>();
		HashSet<Integer> groupIds = new HashSet<>();

		List<PostVO> rets = posts.stream().map(po -> {
			uids.add(po.getAuthorId());
			groupIds.add(po.getChannelId());
			return BeanMapUtils.copy(po);
		}).collect(Collectors.toList());

		buildUsers(rets, uids);
		buildGroups(rets, groupIds);

		return rets;
	}

	private void buildUsers(Collection<PostVO> posts, Set<Long> uids) {
		Map<Long, UserVO> userMap = userService.findMapByIds(uids);
		posts.forEach(p -> p.setAuthor(userMap.get(p.getAuthorId())));
	}

	private void buildGroups(Collection<PostVO> posts, Set<Integer> groupIds) {
		Map<Integer, Channel> map = channelService.findMapByIds(groupIds);
		posts.forEach(p -> p.setChannel(map.get(p.getChannelId())));
	}

	private void onPushEvent(Post post, int action) {
		PostUpdateEvent event = new PostUpdateEvent(System.currentTimeMillis());
		event.setPostId(post.getId());
		event.setUserId(post.getAuthorId());
		event.setAction(action);
		applicationContext.publishEvent(event);
	}

	private void countResource(Long postId, String originContent, String newContent) {
		if (StringUtils.isEmpty(originContent)) {
			originContent = "";
		}
		if (StringUtils.isEmpty(newContent)) {
			newContent = "";
		}

		Set<String> exists = extractImageMd5(originContent);
		Set<String> news = extractImageMd5(newContent);

		List<String> adds = ListUtils.removeAll(news, exists);
		List<String> deleteds = ListUtils.removeAll(exists, news);

		if (adds.size() > 0) {
			List<Resource> resources = resourceRepository.findByMd5In(adds);

			List<PostResource> prs = resources.stream().map(n -> {
				PostResource pr = new PostResource();
				pr.setResourceId(n.getId());
				pr.setPostId(postId);
				pr.setPath(n.getPath());
				return pr;
			}).collect(Collectors.toList());
			postResourceRepository.saveAll(prs);

			resourceRepository.updateAmount(adds, 1);
		}

		if (deleteds.size() > 0) {
			List<Resource> resources = resourceRepository.findByMd5In(deleteds);
			List<Long> rids = resources.stream().map(Resource::getId).collect(Collectors.toList());
			postResourceRepository.deleteByPostIdAndResourceIdIn(postId, rids);
			resourceRepository.updateAmount(deleteds, -1);
		}
	}

	private void cleanResource(long postId) {
		List<PostResource> list = postResourceRepository.findByPostId(postId);
		if (null == list || list.isEmpty()) {
			return;
		}
		List<Long> rids = list.stream().map(PostResource::getResourceId).collect(Collectors.toList());
		resourceRepository.updateAmountByIds(rids, -1);
		postResourceRepository.deleteByPostId(postId);
	}

	private Set<String> extractImageMd5(String text) {
//		Pattern pattern = Pattern.compile("(?<=/_signature/)[^/]+?jpg");

		Set<String> md5s = new HashSet<>();

		Matcher originMatcher = pattern.matcher(text);
		while (originMatcher.find()) {
			String key = originMatcher.group();
//			md5s.add(key.substring(0, key.lastIndexOf(".")));
			md5s.add(key);
		}

		return md5s;
	}
}
