package com.genhub.domain.blog;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Data;

/**
 * 
 * @author ssatwa
 *
 */
@Data
@Entity
@Table(name = "mto_post_attribute")
public class PostAttribute implements Serializable {
	private static final long serialVersionUID = 7829351358884064647L;

	@Id
	private long id;

	@Column(length = 16, columnDefinition = "varchar(16) default 'tinymce'")
	private String editor;

	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Type(type = "text")
	private String content;

}
