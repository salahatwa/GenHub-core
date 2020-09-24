package com.genhub.domain.social.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LinkedInShareRq {
	private String author;
	private String lifecycleState;
	private SpecificContent specificContent = new SpecificContent();
	private Visibility visibility = new Visibility();

	@Data
	public class ShareCommentary {
		public String text;
	}

	public class Description {
		public String text;
	}

	public class Title {
		public String text;
	}

	public class Medium {
		public String status;
		public Description description;
		public String originalUrl;
		public Title title;
	}

	@Data
	public class ShareContent {
		public ShareCommentary shareCommentary = new ShareCommentary();
		public String shareMediaCategory;
		public List<Medium> media = new ArrayList<LinkedInShareRq.Medium>();
	}

	@Data
	public class SpecificContent {
		@JsonProperty("com.linkedin.ugc.ShareContent")
		public ShareContent shareContent = new ShareContent();
	}

	@Data
	public class Visibility {
		@JsonProperty("com.linkedin.ugc.MemberNetworkVisibility")
		private String memberVisibilty;
	}

}
