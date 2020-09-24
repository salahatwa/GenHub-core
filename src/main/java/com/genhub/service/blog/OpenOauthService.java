package com.genhub.service.blog;

import com.genhub.domain.blog.dto.OpenOauthVO;
import com.genhub.domain.blog.dto.UserVO;


public interface OpenOauthService {
    UserVO getUserByOauthToken(String oauth_token);

    OpenOauthVO getOauthByToken(String oauth_token);
    
    OpenOauthVO getOauthByOauthUserId(String oauthUserId);

    OpenOauthVO getOauthByUid(long userId);

    boolean checkIsOriginalPassword(long userId);

    void saveOauthToken(OpenOauthVO oauth);

}
