package com.genhub.domain.blog;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
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
@Table(name = "mto_favorite", indexes = { @Index(name = "IK_USER_ID", columnList = "user_id") })
public class Favorite {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "user_id")
	private long userId;

	@Column(name = "post_id")
	private long postId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

}
