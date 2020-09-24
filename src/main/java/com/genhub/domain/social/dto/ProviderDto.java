package com.genhub.domain.social.dto;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.genhub.domain.audit.DateAudit;
import com.genhub.domain.social.ProviderType;

import lombok.Data;

@Data
public class ProviderDto extends DateAudit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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

	@Enumerated(EnumType.ORDINAL)
	private ProviderType providerType;

}
