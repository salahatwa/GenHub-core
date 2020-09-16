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

/**
 * 
 * @author ssatwa
 *
 */
@Data
@Entity
@Table(name = "mto_tag")
public class Tag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(unique = true, nullable = false, updatable = false, length = 32)
	private String name;

	@Column(length = 128)
	private String thumbnail;

	private String description;

	private long latestPostId;

	@Temporal(value = TemporalType.TIMESTAMP)
	private Date created;

	@Temporal(value = TemporalType.TIMESTAMP)
	private Date updated;

	private int posts;

}
