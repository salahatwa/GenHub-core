package com.genhub.domain.dto.blog;

import java.io.Serializable;

import com.genhub.domain.blog.Tag;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TagVO extends Tag implements Serializable {
	private static final long serialVersionUID = -7787865229252467418L;

	private PostVO post;
}
