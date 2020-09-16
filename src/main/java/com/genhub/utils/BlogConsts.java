
package com.genhub.utils;

public interface BlogConsts {
	String thumbnailPath = "/storage/thumbnails";

	String avatarPath = "/storage/avatars";

	String AVATAR = "https://s.gravatar.com/avatar/30a3742efd884b026c73eea0e1afe7f6?s=80";

	String SEPARATOR = ",";

	String SEPARATOR_X = "x";

	String ROLE_ADMIN = "admin";

	int PAGE_DEFAULT_SIZE = 10;

	int IDENTITY_STEP = 1;

	int DECREASE_STEP = -1;

	int TIME_MIN = 1000;

	int IGNORE = -1;

	int ZERO = 0;

	int STATUS_CLOSED = 1;

	int STATUS_NORMAL = 0;

	int STATUS_FEATURED = 1;

	int STATUS_LOCKED = 1;

	int STATUS_HIDDEN = 1;

	interface order {
		String FEATURED = "featured";
		String NEWEST = "newest";
		String HOTTEST = "hottest";
		String FAVOR = "favors";
	}

	int CODE_BIND = 1; // bind email
	int CODE_FORGOT = 2; // forgot password
	int CODE_REGISTER = 3;

	int CODE_STATUS_INIT = 0;
	int CODE_STATUS_CERTIFIED = 1;

	int FEATURED_DEFAULT = 0;
	int FEATURED_ACTIVE = 1;

	int UNREAD = 0;

	int READED = 1;

	int MESSAGE_EVENT_FAVOR_POST = 1;

	int MESSAGE_EVENT_COMMENT = 3;

	int MESSAGE_EVENT_COMMENT_REPLY = 4;

	String CACHE_USER = "userCaches";
	String CACHE_POST = "postCaches";

	String QQ_CALLBACK = "qq_callback";
	String QQ_APP_ID = "qq_app_id";
	String QQ_APP_KEY = "qq_app_key";

	String WEIBO_CALLBACK = "weibo_callback";
	String WEIBO_CLIENT_ID = "weibo_client_id";
	String WEIBO_CLIENT_SERCRET = "weibo_client_sercret";
	String DOUBAN_CALLBACK = "douban_callback";
	String DOUBAN_API_KEY = "douban_api_key";
	String DOUBAN_SECRET_KEY = "douban_secret_key";

	String GITHUB_CALLBACK = "github_callback";
	String GITHUB_CLIENT_ID = "github_client_id";
	String GITHUB_SECRET_KEY = "github_secret_key";

	String EMAIL_TEMPLATE_CODE = "email_code.ftl";

	String EDITOR_MARKDOWN = "markdown";

	String STORAGE_LIMIT_SIZE = "storage_limit_size";
	String STORAGE_MAX_WIDTH = "storage_max_width";

	String THUMBNAIL_POST_SIZE = "thumbnail_post_size";
}
