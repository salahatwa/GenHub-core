package com.genhub.domain.blog.dto;

import com.genhub.domain.blog.Favorite;

import lombok.Data;

@Data
public class FavoriteVO extends Favorite {
	// extend
	private PostVO post;

}
