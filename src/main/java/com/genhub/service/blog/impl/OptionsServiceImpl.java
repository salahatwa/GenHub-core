package com.genhub.service.blog.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genhub.domain.blog.Options;
import com.genhub.repository.blog.OptionsRepository;
import com.genhub.service.blog.OptionsService;

@Service
public class OptionsServiceImpl implements OptionsService {
	@Autowired
	private OptionsRepository optionsRepository;
	@Autowired
	private EntityManager entityManager;

	@Override
	@Transactional(readOnly = true)
	public List<Options> findAll() {
		List<Options> list = optionsRepository.findAll();
		List<Options> rets = new ArrayList<>();

		for (Options po : list) {
			Options r = new Options();
			BeanUtils.copyProperties(po, r);
			rets.add(r);
		}
		return rets;
	}

	@Override
	@Transactional
	public void update(Map<String, String> options) {
		if (options == null) {
			return;
		}

		options.forEach((key, value) -> {
			Options entity = optionsRepository.findByKey(key);
			String val = StringUtils.trim(value);
			if (entity != null) {
				entity.setValue(val);
			} else {
				entity = new Options();
				entity.setKey(key);
				entity.setValue(val);
			}
			optionsRepository.save(entity);
		});
	}

	@Override
	@Transactional
	public void initSettings(Resource resource) {
		Session session = entityManager.unwrap(Session.class);
		session.doWork(connection -> ScriptUtils.executeSqlScript(connection, resource));
	}

}
