package com.genhub.domain.social;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.genhub.domain.audit.UserDateAudit;

import lombok.Data;

@Data
@Entity
public class Provider extends UserDateAudit {

	@Id
	private String id;
	private String name;
	private boolean isGeoEnabled;
	private String handle;
	private String email;
	private String description;
	private String expireAt;

	private String url;
	private String image;
	private boolean secure;

	@Column(columnDefinition = "text")
	private String token;
	private String secret;

	@Enumerated(EnumType.ORDINAL)
	private ProviderType providerType;

//	@ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.DETACH)
//	@JoinColumn(name = "user_id")
//	private User user;
//	@Column(name = "user_id")
//	private String userId;

	@ManyToMany(mappedBy = "providers", fetch = FetchType.LAZY)
	@JsonIgnoreProperties("providers")
	private List<Task> tasks = new ArrayList<>();
}
