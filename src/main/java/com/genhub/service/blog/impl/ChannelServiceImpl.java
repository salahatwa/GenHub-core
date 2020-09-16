package com.genhub.service.blog.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genhub.domain.blog.Channel;
import com.genhub.exceptions.ResourceNotFoundException;
import com.genhub.repository.blog.ChannelRepository;
import com.genhub.service.blog.ChannelService;
import com.genhub.utils.BlogConsts;

@Service
@Transactional(readOnly = true)
public class ChannelServiceImpl implements ChannelService {
	@Autowired
	private ChannelRepository channelRepository;

	@Override
	public Page<Channel> findAllPaging(Pageable pageable, int status) {

		Page<Channel> list;
		if (status > BlogConsts.IGNORE) {
			list = channelRepository.findAllByStatus(status, pageable);
		} else {
			list = channelRepository.findAll(pageable);
		}
		return list;
	}

	@Override
	public List<Channel> findAll(int status) {
		Sort sort = Sort.by(Sort.Direction.DESC, "weight", "id");
		List<Channel> list;
		if (status > BlogConsts.IGNORE) {
			list = channelRepository.findAllByStatus(status, sort);
		} else {
			list = channelRepository.findAll(sort);
		}
		return list;
	}

	@Override
	public Map<Integer, Channel> findMapByIds(Collection<Integer> ids) {
		List<Channel> list = channelRepository.findAllById(ids);
		if (null == list) {
			return Collections.emptyMap();
		}
		return list.stream().collect(Collectors.toMap(Channel::getId, n -> n));
	}

	@Override
	public Channel getById(int id) {
		return channelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Channel", "Id", id));
	}

	@Override
	@Transactional
	public Channel update(Channel channel) {
		
		Optional<Channel> optional = channelRepository.findById(channel.getId());
		Channel po = optional.orElse(new Channel());
		BeanUtils.copyProperties(channel, po);
		
		System.out.println(po);
		channelRepository.save(po);
		return po;
	}

	@Override
	@Transactional
	public Channel updateWeight(int id, int weighted) {
		Channel po = channelRepository.findById(id).get();

		int max = BlogConsts.ZERO;
		if (BlogConsts.FEATURED_ACTIVE == weighted) {
			max = channelRepository.maxWeight() + 1;
		}
		po.setWeight(max);
		po=channelRepository.save(po);
		return po;
	}

	@Override
	@Transactional
	public void delete(int id) {
		try {
			channelRepository.deleteById(id);
		} catch (Exception ex) {
			throw new ResourceNotFoundException("Channel", "Id", id);
		}
	}

	@Override
	public long count() {
		return channelRepository.count();
	}

}
