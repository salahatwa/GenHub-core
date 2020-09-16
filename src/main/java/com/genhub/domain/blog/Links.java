package com.genhub.domain.blog;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import lombok.Data;

/**
 * 
 * @author ssatwa
 *
 */
@Data
@Entity
@Table(name = "mto_links")
public class Links {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;
	private String url;

	@Column(name = "created", columnDefinition = "timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP")
	@Generated(GenerationTime.INSERT)
	private LocalDateTime created;

	@Column(name = "updated", columnDefinition = "timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP")
	@Generated(GenerationTime.ALWAYS)
	private LocalDateTime updateTime;

}
