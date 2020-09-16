package com.genhub.domain.blog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * 
 * @author ssatwa
 *
 */
@Data
@Entity
@Table(name = "mto_options")
public class Options {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(length = 5)
	private int type;

	@Column(name = "key_", unique = true, length = 32)
	private String key;

	@Column(length = 300)
	private String value;

}
