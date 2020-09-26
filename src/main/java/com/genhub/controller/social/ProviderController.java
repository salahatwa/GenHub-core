package com.genhub.controller.social;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.social.connect.Connection;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.social.twitter.api.UserOperations;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.genhub.config.SocialConfig;
import com.genhub.config.secuirty.CurrentUser;
import com.genhub.config.secuirty.JwtTokenProvider;
import com.genhub.config.secuirty.UserPrincipal;
import com.genhub.domain.User;
import com.genhub.domain.blog.dto.UserVO;
import com.genhub.domain.social.Provider;
import com.genhub.domain.social.ProviderType;
import com.genhub.domain.social.dto.LinkedInRqToken;
import com.genhub.domain.social.dto.LinkedInRqTokenRs;
import com.genhub.domain.social.dto.SocialRq;
import com.genhub.domain.social.dto.SocialRs;
import com.genhub.exceptions.ResourceNotFoundException;
import com.genhub.repository.blog.RoleRepository;
import com.genhub.repository.blog.UserRepository;
import com.genhub.repository.social.ProviderRepository;
import com.genhub.service.social.ProviderService;
import com.genhub.utils.Constant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/provider")
public class ProviderController {

	@Autowired
	private ProviderService providerService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	JwtTokenProvider tokenProvider;

	@Autowired
	SocialConfig socialConfig;

	@Autowired
	private ProviderRepository profileRepository;

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> removeProviderById(@CurrentUser UserPrincipal currentUser, @PathVariable String id) {

		this.providerService.removeAccountById(id);

		return ResponseEntity.ok(true);
	}

	@GetMapping("/accounts")
	public ResponseEntity<Page<Provider>> getAllUserSocialAccounts(@CurrentUser UserPrincipal currentUser,
			@RequestParam(value = "page", defaultValue = Constant.DEFAULT_PAGE_NUMBER) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = Constant.DEFAULT_PAGE_SIZE) int pageSize,
			@RequestParam(defaultValue = "id") String sortBy) {

		return ResponseEntity.ok(providerService.getAllAccountsForUser(currentUser.getId(), pageNo, pageSize, sortBy));
	}

	@GetMapping("/twitter/init")
	public ResponseEntity<SocialRs> twitterIntialize() {

		TwitterConnectionFactory connectionFactory = new TwitterConnectionFactory(socialConfig.getTwitterKey(),
				socialConfig.getTwitterSecret());

		OAuth1Operations oauthOperations = connectionFactory.getOAuthOperations();
		OAuthToken requestToken = oauthOperations.fetchRequestToken(socialConfig.getTwitterCallBack1(), null);
		String authorizeUrl = oauthOperations.buildAuthorizeUrl(requestToken.getValue(), OAuth1Parameters.NONE);

		return ResponseEntity.ok(new SocialRs(authorizeUrl));
	}

	@GetMapping("/linkedIn/init")
	public ResponseEntity<SocialRs> linkedInIntialize() {

		String url = "https://www.linkedin.com/uas/oauth2/authorization?response_type=code&client_id="
				+ socialConfig.getLinkedInKey() + "&scope=r_emailaddress,r_liteprofile,w_member_social&redirect_uri="
				+ socialConfig.getLinkedInCallBack1();

		return ResponseEntity.ok(new SocialRs(url));
	}

	@PostMapping("/linkedIn/request-token")
	public ResponseEntity<UserVO> linkedInRqToken(@CurrentUser UserPrincipal currenUser, @RequestBody SocialRq socialRq)
			throws JSONException {

		LinkedInRqToken rq = new LinkedInRqToken();
		rq.setClient_id(socialConfig.getLinkedInKey());
		rq.setClient_secret(socialConfig.getLinkedInSecret());
		rq.setCode(socialRq.getRequestToken());
		rq.setGrant_type("authorization_code");
		rq.setRedirect_uri(socialConfig.getLinkedInCallBack1());

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("grant_type", "authorization_code");
		map.add("code", socialRq.getRequestToken());
		map.add("client_id", socialConfig.getLinkedInKey());
		map.add("client_secret", socialConfig.getLinkedInSecret());
		map.add("redirect_uri", socialConfig.getLinkedInCallBack1());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);

		RestTemplate restTemplate = new RestTemplateBuilder().build();
		LinkedInRqTokenRs rs = restTemplate.exchange("https://www.linkedin.com/oauth/v2/accessToken", HttpMethod.POST,
				requestEntity, LinkedInRqTokenRs.class).getBody();

		System.out.println(rs.getAccess_token());
		headers.add("Authorization", "Bearer " + rs.getAccess_token());

		HttpEntity entity = new HttpEntity<>(headers);
		String p = restTemplate.exchange(
				"https://api.linkedin.com/v2/me?projection=(id,firstName,lastName,emailAddress,profilePicture(displayImage~:playableStreams))",
				HttpMethod.GET, entity, String.class).getBody();

		JSONObject obj = new JSONObject(p);
		String userId = (String) obj.get("id");

		String name = obj.getJSONObject("firstName").getJSONObject("localized").get("en_US") + " "
				+ obj.getJSONObject("lastName").getJSONObject("localized").get("en_US");

		String imgUrl = (String) obj.getJSONObject("profilePicture").getJSONObject("displayImage~")
				.getJSONArray("elements").getJSONObject(0).getJSONArray("identifiers").getJSONObject(0)
				.get("identifier");
		Optional<Provider> profilet = profileRepository.findById(userId);

		User user = null;

		if (profilet.isPresent())
			user = userRepository.findById( profilet.get().getCreatedBy()).get();
		else {
			user = userRepository.findById(currenUser.getId())
					.orElseThrow(() -> new ResourceNotFoundException("ERROR_usernotfound"));

			Provider profile = new Provider();
			profile.setId(userId);
			profile.setName(name);
			profile.setImage(imgUrl);
			profile.setProviderType(ProviderType.LINKEDIN);
			profile.setToken(rs.getAccess_token());

			providerService.save(profile);

		}

		UserVO userVo = new UserVO(user.getName(), user.getPassword(), user.getName(), user.getEmail());
		BeanUtils.copyProperties(user, userVo);
		userVo.setAccessToken(tokenProvider.toToken(user.getId()));
		return ResponseEntity.ok(userVo);
	}

	@PostMapping("/twitter/request-token")
	public ResponseEntity<UserVO> twitterRqToken(@CurrentUser UserPrincipal currenUser,
			@Valid @RequestBody SocialRq socialRq) {

		TwitterConnectionFactory connectionFactory = new TwitterConnectionFactory(socialConfig.getTwitterKey(),
				socialConfig.getTwitterSecret());
		OAuth1Operations oauthOperations = connectionFactory.getOAuthOperations();

		OAuthToken accessToken = oauthOperations.exchangeForAccessToken(new AuthorizedRequestToken(
				new OAuthToken(socialRq.getRequestToken(), null), socialRq.getOauthVerifier()), null);

		Connection<Twitter> twitterConnection = connectionFactory.createConnection(accessToken);
		Twitter twitter = twitterConnection.getApi();

		UserOperations userOperations = twitter.userOperations();
		TwitterProfile twitterProfile = userOperations.getUserProfile();

		long twitterID = userOperations.getProfileId();

		Optional<Provider> profilet = profileRepository.findById(String.valueOf(twitterID));

		User user = null;

		if (profilet.isPresent()) {
			Provider prof = profilet.get();
			user = userRepository.findById( prof.getCreatedBy()).get();
			prof.setToken(accessToken.getValue());
			prof.setSecret(accessToken.getSecret());

			System.out.println("Token updated");
			profileRepository.save(prof);
		} else {
			user = userRepository.findById(currenUser.getId())
					.orElseThrow(() -> new ResourceNotFoundException("ERROR_usernotfound"));

			Provider profile = new Provider();
			profile.setDescription(twitterProfile.getDescription());
			profile.setId(Long.toString(twitterID));
			profile.setName(twitterProfile.getName());
			profile.setGeoEnabled(twitterProfile.isGeoEnabled());
			profile.setUrl(twitterProfile.getProfileUrl());
			profile.setImage(twitterProfile.getProfileImageUrl());
			profile.setSecure(twitterProfile.isProtected());
			profile.setProviderType(ProviderType.TWITTER);
			profile.setToken(accessToken.getValue());
			profile.setSecret(accessToken.getSecret());
			providerService.save(profile);
		}

		UserVO userVo = new UserVO(user.getName(), user.getPassword(), user.getName(), user.getEmail());
		BeanUtils.copyProperties(user, userVo);
		userVo.setAccessToken(tokenProvider.toToken(user.getId()));
		return ResponseEntity.ok(userVo);
	}

}
