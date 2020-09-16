package com.genhub.domain.dto.blog;

import com.genhub.domain.blog.Favorite;

import lombok.Data;

@Data
public class FavoriteVO extends Favorite {
	// extend
	private PostVO post;

}
