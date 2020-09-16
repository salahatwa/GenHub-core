package com.genhub.domain.blog;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

/**
 * 
 * @author ssatwa
 *
 */
@Data
@Entity
@Table(name = "mto_channel")
@ToString
public class Channel implements Serializable {
	private static final long serialVersionUID = 2436696690653745208L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(length = 32)
	private String name;

	@Column(name = "key_", unique = true, length = 32)
	private String key;

	@Column(length = 128)
	private String thumbnail;

	@Column(length = 5)
	private int status;

	private int weight;

}
