package com.genhub.service.blog;

import com.genhub.domain.dto.blog.OpenOauthVO;
import com.genhub.domain.dto.blog.UserVO;


public interface OpenOauthService {
    UserVO getUserByOauthToken(String oauth_token);

    OpenOauthVO getOauthByToken(String oauth_token);
    
    OpenOauthVO getOauthByOauthUserId(String oauthUserId);

    OpenOauthVO getOauthByUid(long userId);

    boolean checkIsOriginalPassword(long userId);

    void saveOauthToken(OpenOauthVO oauth);

}
