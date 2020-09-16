package com.genhub.domain.dto;

import lombok.Data;

@Data
public class SystemStatus {
	private long channelCount;
	private long postCount;
	private long commentCount;
	private long userCount;

	private float freeMemory;
	private float totalMemory;
	private float usedMemory;
	private float memPercent;
	private String os;
	private String javaVersion;

}
