package com.genhub.service.blog;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.genhub.domain.blog.Channel;

public interface ChannelService {
	Page<Channel> findAllPaging(Pageable pageable, int status);

	List<Channel> findAll(int status);

	Map<Integer, Channel> findMapByIds(Collection<Integer> ids);

	Channel getById(int id);

	Channel update(Channel channel);

	Channel updateWeight(int id, int weighted);

	void delete(int id);

	long count();
}
