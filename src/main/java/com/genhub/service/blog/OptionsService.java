package com.genhub.service.blog;

import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;

import com.genhub.domain.blog.Options;

public interface OptionsService {

	List<Options> findAll();

	void update(Map<String, String> options);

	void initSettings(Resource resource);
}
