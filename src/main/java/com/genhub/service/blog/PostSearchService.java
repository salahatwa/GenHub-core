package com.genhub.service.blog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.genhub.domain.blog.dto.PostVO;

public interface PostSearchService {

	Page<PostVO> search(Pageable pageable, String term) throws Exception;

	void resetIndexes();
}
