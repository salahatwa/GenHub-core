package com.genhub.domain.dto.blog;

import com.genhub.domain.blog.Message;

import lombok.Data;

@Data
public class MessageVO extends Message {
	// extend
	private UserVO from;
	private PostVO post;

}
