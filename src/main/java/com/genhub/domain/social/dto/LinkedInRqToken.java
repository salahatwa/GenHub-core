package com.genhub.domain.social.dto;

import lombok.Data;

@Data
public class LinkedInRqToken {
	private String grant_type;
	private String code;
	private String redirect_uri;
	private String client_id;
	private String client_secret;
}
