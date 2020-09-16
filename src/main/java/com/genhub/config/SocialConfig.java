package com.genhub.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("social")
public class SocialConfig {

	private String twitterKey;
	private String twitterSecret;
	private String twitterCallBack;
	private String twitterCallBack1;
	private String linkedInKey;
	private String linkedInSecret;
	private String linkedInCallBack;
	private String linkedInCallBack1;

}
