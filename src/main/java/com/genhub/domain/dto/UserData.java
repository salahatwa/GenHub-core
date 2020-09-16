package com.genhub.domain.dto;

import com.genhub.config.secuirty.UserPrincipal;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserData {
	private String accessToken;
	private UserPrincipal profile;

	public UserData(String accessToken) {
		this.accessToken = accessToken;
	}

	public UserData(String accessToken, UserPrincipal userPrincipal) {
		this.accessToken = accessToken;
		this.profile = userPrincipal;
	}

}
