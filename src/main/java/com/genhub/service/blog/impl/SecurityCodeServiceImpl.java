package com.genhub.service.blog.impl;

import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.genhub.domain.blog.SecurityCode;
import com.genhub.exceptions.BlogException;
import com.genhub.repository.blog.SecurityCodeRepository;
import com.genhub.service.blog.SecurityCodeService;
import com.genhub.utils.BlogConsts;
import com.genhub.utils.EntityStatus;

@Service
public class SecurityCodeServiceImpl implements SecurityCodeService {
	@Autowired
	private SecurityCodeRepository securityCodeRepository;

	private int survivalTime = 30;

	@Override
	@Transactional
	public String generateCode(String key, int type, String target) {
		SecurityCode po = securityCodeRepository.findByKey(key);

		String code = RandomStringUtils.randomNumeric(6);
		Date now = new Date();

		if (po == null) {
			po = new SecurityCode();
			po.setKey(key);
			po.setCreated(now);
			po.setExpired(DateUtils.addMinutes(now, survivalTime));
			po.setCode(code);
			po.setType(type);
			po.setTarget(target);
		} else {

			long interval = (now.getTime() - po.getCreated().getTime()) / 1000;

			if (interval <= 60) {
				throw new BlogException("The interval between sending cannot be less than 1 minute");
			}

			po.setStatus(EntityStatus.ENABLED);
			po.setCreated(now);
			po.setExpired(DateUtils.addMinutes(now, survivalTime));
			po.setCode(code);
			po.setType(type);
			po.setTarget(target);
		}

		securityCodeRepository.save(po);

		return code;
	}

	@Override
	@Transactional
	public boolean verify(String key, int type, String code) {
		Assert.hasLength(code, "Verification code cannot be empty");
		SecurityCode po = securityCodeRepository.findByKeyAndType(key, type);
		Assert.notNull(po, "You have not performed type verification");

		Date now = new Date();

		Assert.state(now.getTime() <= po.getExpired().getTime(), "Verification code has expired");
		Assert.isTrue(po.getType() == type, "wrong verification code type");
		Assert.isTrue(po.getStatus() == BlogConsts.CODE_STATUS_INIT, "Verification code has been used");
		Assert.state(code.equals(po.getCode()), "Verification code is not correct");
		po.setStatus(BlogConsts.CODE_STATUS_CERTIFIED);
		securityCodeRepository.save(po);
		return true;
	}

}
