package com.genhub.domain.social.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SocialRs {
	private String authorizeUrl;

	private String secret;

	private String value;

	public SocialRs(String authorizeUrl) {
		super();
		this.authorizeUrl = authorizeUrl;
	}

	public SocialRs(String secret, String value) {
		super();
		this.secret = secret;
		this.value = value;
	}

}
