package com.genhub.domain.dto.blog;

import lombok.Data;

@Data
public class OpenOauthVO {
	private long id;

	private long userId;

	private int oauthType;

	private String oauthUserId;

	private String oauthCode;

	private String accessToken;

	private String expireIn;

	private String refreshToken;

	// extends
	private String username;
	private String nickname;
	private String email;
	private String avatar;

}
