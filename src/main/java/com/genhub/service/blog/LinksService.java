package com.genhub.service.blog;

import java.util.List;

import com.genhub.domain.blog.Links;

public interface LinksService {
    List<Links> findAll();
    void update(Links links);
    void delete(long id);
}
