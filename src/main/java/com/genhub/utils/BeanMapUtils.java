package com.genhub.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.genhub.domain.Role;
import com.genhub.domain.User;
import com.genhub.domain.blog.Comment;
import com.genhub.domain.blog.Favorite;
import com.genhub.domain.blog.Message;
import com.genhub.domain.blog.Post;
import com.genhub.domain.blog.PostTag;
import com.genhub.domain.blog.Tag;
import com.genhub.domain.dto.blog.AccountProfile;
import com.genhub.domain.dto.blog.CommentVO;
import com.genhub.domain.dto.blog.FavoriteVO;
import com.genhub.domain.dto.blog.MessageVO;
import com.genhub.domain.dto.blog.PostTagVO;
import com.genhub.domain.dto.blog.PostVO;
import com.genhub.domain.dto.blog.TagVO;
import com.genhub.domain.dto.blog.UserVO;

public class BeanMapUtils {
	private static String[] USER_IGNORE = new String[] { "extend", "roles" };

	public static UserVO copy(User po) {
		if (po == null) {
			return null;
		}
		UserVO ret = new UserVO();
		BeanUtils.copyProperties(po, ret, USER_IGNORE);
		return ret;
	}

	public static List<GrantedAuthority> roleToAuthiority(List<Role> roles) {
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}

	public static AccountProfile copyPassport(User po) {
		AccountProfile passport = new AccountProfile(po.getId(), po.getUsername());
		passport.setName(po.getName());
		passport.setEmail(po.getEmail());
		passport.setAvatar(po.getAvatar());
		passport.setLastLogin(po.getLastLogin());
		passport.setStatus(po.getStatus());
		return passport;
	}

	public static CommentVO copy(Comment po) {
		CommentVO ret = new CommentVO();
		BeanUtils.copyProperties(po, ret);
		return ret;
	}

	public static PostVO copy(Post po) {
		PostVO d = new PostVO();
		BeanUtils.copyProperties(po, d);
		return d;
	}

	public static MessageVO copy(Message po) {
		MessageVO ret = new MessageVO();
		BeanUtils.copyProperties(po, ret);
		return ret;
	}

	public static FavoriteVO copy(Favorite po) {
		FavoriteVO ret = new FavoriteVO();
		BeanUtils.copyProperties(po, ret);
		return ret;
	}

	public static PostTagVO copy(PostTag po) {
		PostTagVO ret = new PostTagVO();
		BeanUtils.copyProperties(po, ret);
		return ret;
	}

	public static TagVO copy(Tag po) {
		TagVO ret = new TagVO();
		BeanUtils.copyProperties(po, ret);
		return ret;
	}

	public static String[] postOrder(String order) {
		String[] orders;
		switch (order) {
		case BlogConsts.order.HOTTEST:
			orders = new String[] { "comments", "views", "created" };
			break;
		case BlogConsts.order.FAVOR:
			orders = new String[] { "favors", "created" };
			break;
		default:
			orders = new String[] { "weight", "created" };
			break;
		}
		return orders;
	}
}
