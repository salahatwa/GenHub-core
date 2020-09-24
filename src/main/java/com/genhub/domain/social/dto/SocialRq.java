package com.genhub.domain.social.dto;

import lombok.Data;

@Data
public class SocialRq {
	private String requestToken;
	private String oauthVerifier;
}
