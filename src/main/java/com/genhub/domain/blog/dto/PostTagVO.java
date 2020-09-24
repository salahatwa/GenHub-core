package com.genhub.domain.blog.dto;

import java.io.Serializable;

import com.genhub.domain.blog.PostTag;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PostTagVO extends PostTag implements Serializable {
	private static final long serialVersionUID = 73354108587481371L;

	private PostVO post;
}
