package com.genhub.domain.dto.blog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.genhub.config.secuirty.UserPrincipal;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserVO implements Serializable {

	private static final long serialVersionUID = 107193816173103116L;

	private String accessToken;

	private long id;
	private String username;

	@JsonIgnore
	private String password;
	private String avatar;
	private String name;

	private String bio;

	@JsonIgnore
	private String email;

	private int posts;
	private int comments;

	private Date created;
	private Date lastLogin;
	private String signature;

	private int status;

	private List<GrantedAuthority> authorities  = new ArrayList<>();

	public UserVO(String username, String password, String name, String email) {
		super();
		this.username = username;
		this.password = password;
		this.name = name;
		this.email = email;
	}

	public UserVO(String accessToken, UserPrincipal userPrincipal) {
		this.accessToken = accessToken;
		BeanUtils.copyProperties(userPrincipal, this);
		this.authorities=new ArrayList<GrantedAuthority>(userPrincipal.getAuthorities());
	}

}
