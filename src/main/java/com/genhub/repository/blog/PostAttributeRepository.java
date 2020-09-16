package com.genhub.repository.blog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.genhub.domain.blog.PostAttribute;


public interface PostAttributeRepository
		extends JpaRepository<PostAttribute, Long>, JpaSpecificationExecutor<PostAttribute> {
}
