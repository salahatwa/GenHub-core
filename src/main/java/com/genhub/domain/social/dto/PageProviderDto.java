package com.genhub.domain.social.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageProviderDto {
	private List<ProviderDto> providers=new ArrayList<ProviderDto>();
	private long totalElements;

}
