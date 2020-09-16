package com.genhub.domain.blog;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
@Table(name = "mto_resource", uniqueConstraints = { @UniqueConstraint(name = "UK_MD5", columnNames = { "md5" }) })
public class Resource implements Serializable {
	private static final long serialVersionUID = -2263990565349962964L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "md5", columnDefinition = "varchar(100) NOT NULL DEFAULT ''")
	private String md5;

	@Column(name = "path", columnDefinition = "varchar(255) NOT NULL DEFAULT ''")
	private String path;

	@Column(name = "amount", columnDefinition = "bigint NOT NULL DEFAULT '0'")
	private long amount;

	@Column(name = "create_time", columnDefinition = "timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP")
	@Generated(GenerationTime.INSERT)
	private LocalDateTime createTime;

	@Column(name = "update_time", columnDefinition = "timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP")
	@Generated(GenerationTime.ALWAYS)
	private LocalDateTime updateTime;

}
