package com.genhub.domain.blog;

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
@Table(name = "mto_post_tag", indexes = { @Index(name = "IK_TAG_ID", columnList = "tag_id") })
public class PostTag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "post_id")
	private long postId;

	@Column(name = "tag_id")
	private long tagId;

	private long weight;

}
