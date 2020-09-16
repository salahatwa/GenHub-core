package com.genhub.domain.blog;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Data
@Entity
@Table(name = "mto_security_code")
public class SecurityCode {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "key_", unique = true, nullable = false, length = 64)
	private String key;

	@Column(length = 16, nullable = false)
	private String code;

	@Column(length = 64)
	private String target;

	@Column
	private int type;

	@Column(name = "expired", nullable = false)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date expired;

	@Column(name = "created", nullable = false)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date created;

	@Column
	private int status;

}
