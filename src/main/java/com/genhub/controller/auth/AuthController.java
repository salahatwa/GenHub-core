package com.genhub.controller.auth;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.genhub.config.blog.SiteOptions;
import com.genhub.config.secuirty.CurrentUser;
import com.genhub.config.secuirty.JwtTokenProvider;
import com.genhub.config.secuirty.UserPrincipal;
import com.genhub.domain.blog.dto.UserVO;
import com.genhub.domain.dto.LoginRq;
import com.genhub.domain.dto.SignUpRq;
import com.genhub.exceptions.BlogException;
import com.genhub.exceptions.InvalidUsernamePasswordException;
import com.genhub.service.blog.SecurityCodeService;
import com.genhub.service.blog.UserService;
import com.genhub.utils.BlogConsts;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author ssatwa
 *
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@ConditionalOnProperty(name = "site.controls.register", havingValue = "true", matchIfMissing = true)
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;
	@Autowired
	private SecurityCodeService securityCodeService;

	@Autowired
	private SiteOptions siteOptions;

	@Autowired
	private JwtTokenProvider tokenProvider;

	private String getJwtFromRequest(String bearerToken) {
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}

	@GetMapping("/me")
	public ResponseEntity<UserVO> getCurrentUser(@CurrentUser UserPrincipal currentUser,
			@RequestHeader(value = "Authorization") String authorization) {
		UserVO userSummary = new UserVO(getJwtFromRequest(authorization), currentUser);
		return ResponseEntity.ok(userSummary);
	}
	


	@PostMapping("/login")
	public ResponseEntity<UserVO> authenticateUser(@Valid @RequestBody LoginRq loginRequest) {
		String jwt = null;
		UserPrincipal userPrincipal = null;
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

			SecurityContextHolder.getContext().setAuthentication(authentication);

			userPrincipal = (UserPrincipal) authentication.getPrincipal();
			jwt = tokenProvider.generateToken(userPrincipal.getId());
		} catch (Exception ex) {
			throw new InvalidUsernamePasswordException("user " + ex.getMessage(), "Email or Password",
					loginRequest.getUsername());
		}
		return ResponseEntity.ok(new UserVO(jwt, userPrincipal));
	}

	@PostMapping("/signup")
	public ResponseEntity<UserVO> register(@RequestBody SignUpRq rq, @RequestParam(required = false) String code) {
		UserVO user = null;
		try {
			if (siteOptions.getControls().isRegister_email_validate()) {
				Assert.state(StringUtils.hasText(rq.getEmail()), "Please input the email address");
				Assert.state(StringUtils.hasText(code), "Please enter the email verification code");
				securityCodeService.verify(rq.getEmail(), BlogConsts.CODE_REGISTER, code);
			}
			user = new UserVO(rq.getName(), rq.getPassword(), rq.getName(), rq.getEmail());
			user.setAvatar(BlogConsts.AVATAR);
			user = userService.register(user);

			user.setAccessToken(tokenProvider.toToken(user.getId()));

		} catch (Exception e) {
			throw new BlogException(e.getMessage());
		}
		return ResponseEntity.ok(user);
	}

}