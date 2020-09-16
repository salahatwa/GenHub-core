package com.genhub.aspect.blog;

import org.springframework.context.ApplicationEvent;

import lombok.Data;

@Data
public class PostUpdateEvent extends ApplicationEvent {
	public final static int ACTION_PUBLISH = 1;
	public final static int ACTION_DELETE = 2;

	private long postId;
	private long userId;
	private int action = ACTION_PUBLISH;

	public PostUpdateEvent(Object source) {
		super(source);
	}

}
