package com.genhub.domain.blog;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Data;

/**
 * 
 * @author ssatwa
 *
 */
@Data
@Entity
@Table(name = "mto_comment", indexes = { @Index(name = "IK_POST_ID", columnList = "post_id") })
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private long pid;

	@Column(name = "post_id")
	private long postId;

	@Column(name = "content")
	private String content;

	@Column(name = "created")
	private Date created;

	@Column(name = "author_id")
	private long authorId;

	private int status;

}
