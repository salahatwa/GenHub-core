package com.genhub.domain.dto.blog;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class AccountProfile implements Serializable {
	private static final long serialVersionUID = 1748764917028425871L;
	private long id;
	private String username;
	private String avatar;
	private String name;
	private String email;

	private Date lastLogin;
	private int status;

	private BadgesCount badgesCount;

	public AccountProfile(long id, String username) {
		this.id = id;
		this.username = username;
	}

}
