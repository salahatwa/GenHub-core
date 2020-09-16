package com.genhub.repository.blog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.genhub.domain.blog.SecurityCode;

public interface SecurityCodeRepository
		extends JpaRepository<SecurityCode, Long>, JpaSpecificationExecutor<SecurityCode> {
	SecurityCode findByKeyAndType(String key, int type);

	SecurityCode findByKey(String key);
}
