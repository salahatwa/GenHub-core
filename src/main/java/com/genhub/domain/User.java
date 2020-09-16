package com.genhub.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.NaturalId;

import com.genhub.domain.audit.DateAudit;

import lombok.Data;

@Data
@Entity
@Table(name = "mto_user", uniqueConstraints = { @UniqueConstraint(columnNames = { "username" }),
		@UniqueConstraint(columnNames = { "email" }) })
public class User extends DateAudit {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "name", length = 18)
	private String name;

	@NotBlank
	@Column(name = "username", unique = true, nullable = false, length = 64)
	private String username;

	@NaturalId(mutable = true)
	@NotBlank
	@Email(message = "Not valid email")
	@Column(name = "email", unique = true, length = 64)
	private String email;

	@Column(name = "password", length = 64)
	private String password;

	private String bio;

	private int gender;

	private String signature;

	private int status;

	@Column(name = "last_login")
	private Date lastLogin;

	private int posts;

	private int comments;

	@Column(length = 511)
	private String avatar;


	public User() {

	}

	public User(String name, String username, String email, String password) {
		this.name = name;
		this.username = username;
		this.email = email;
		this.password = password;
	}

	public void update(String email, String username, String bio, String avatar) {
		if (!"".equals(email)) {
			this.email = email;
		}

		if (!"".equals(username)) {
			this.username = username;
		}

		if (!"".equals(bio)) {
			this.bio = bio;
		}

		if (!"".equals(avatar)) {
			this.avatar = avatar;
		}
	}

}