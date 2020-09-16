package com.genhub.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "role")
public class Role {

	public static int STATUS_NORMAL = 0;
	public static int STATUS_CLOSED = 1;
	public static String ROLE_ADMIN = "admin";
	public static String ROLE_USER = "user";
	public static long ADMIN_ID = 1;
	public static long USER_ID = 2;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false, updatable = false, length = 32)
	private String name;

	@Column(length = 140)
	private String description;

	private int status;

	public Role() {

	}

	public Role(String name) {
		this.name = name;
	}

}
