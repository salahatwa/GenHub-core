package com.genhub.domain.blog.dto;

import com.genhub.domain.blog.Message;

import lombok.Data;

@Data
public class MessageVO extends Message {
	// extend
	private UserVO from;
	private PostVO post;

}
