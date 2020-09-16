package com.genhub.domain.blog;

import java.io.Serializable;
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

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.NumericField;
import org.hibernate.search.annotations.SortableField;

import lombok.Data;

/**
 * 
 * @author ssatwa
 *
 */

@Data
@Entity
@Table(name = "mto_post", indexes = { @Index(name = "IK_CHANNEL_ID", columnList = "channel_id") })
@FilterDefs({ @FilterDef(name = "POST_STATUS_FILTER", defaultCondition = "status = 0") })
@Filters({ @Filter(name = "POST_STATUS_FILTER") })
@Indexed(index = "post")
public class Post implements Serializable {
	private static final long serialVersionUID = 7144425803920583495L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@SortableField
	@NumericField
	private long id;

	@Field
	@NumericField
	@Column(name = "channel_id", length = 5)
	private int channelId;

	@Field
	@Column(name = "title", length = 64)
	private String title;

	@Field
	@Column(length = 140)
	private String summary;

	@Column(length = 128)
	private String thumbnail;

	@Field
	@Column(length = 64)
	private String tags;

	@Field
	@NumericField
	@Column(name = "author_id")
	private long authorId;

	@Temporal(value = TemporalType.TIMESTAMP)
	private Date created;

	private int favors;

	private int comments;

	private int views;

	private int status;

	private int featured;

	private int weight;

}