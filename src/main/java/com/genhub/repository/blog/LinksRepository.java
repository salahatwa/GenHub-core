package com.genhub.repository.blog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.genhub.domain.blog.Links;

public interface LinksRepository extends JpaRepository<Links, Long>, JpaSpecificationExecutor<Links> {
}
