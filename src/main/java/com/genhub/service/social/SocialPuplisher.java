package com.genhub.service.social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genhub.config.SocialConfig;
import com.genhub.domain.social.Provider;
import com.genhub.domain.social.Task;
import com.genhub.domain.social.dto.LinkedInShareRq;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@Component
public class SocialPuplisher {

	@Autowired
	SocialConfig socialConfig;

	public void publishToTwitter(Task task, Provider provider) {

		System.out.println(socialConfig.getTwitterSecret());
		System.out.println("------------------------");
		System.out.println();
		System.out.println();

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(socialConfig.getTwitterKey())
				.setOAuthConsumerSecret(socialConfig.getTwitterSecret()).setOAuthAccessToken(provider.getToken())
				.setOAuthAccessTokenSecret(provider.getSecret());
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter4j.Twitter twitter = tf.getInstance();

		Status status;
		try {
			status = twitter.updateStatus(task.getContent());

//			Map<String, RateLimitStatus> rates = twitter.getRateLimitStatus();
//			System.out.println(rates);
//			StatusUpdate s=new StatusUpdate("");
//			s.
//			twitter.updateStatus(latestStatus)
			System.out.println("Successfully updated the status to [" + status.getText() + "].");
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// tweeting
//		Twitter twitterw = new TwitterTemplate(socialConfig.getTwitterKey(), socialConfig.getTwitterSecret(),
//				provider.getToken(), provider.getSecret());
//		;
//		System.out.println(twitter.isAuthorized());
//
////		twitter.restOperations().
//		System.out.println(twitter.searchOperations().search("trump"));
//
////		twitter.restOperations().
//
////		.getInterceptors().add (0, new ContentTypeInterceptor());
//
//		// TweetData tweetData = new TweetData(toTweet.content);
//		TweetData tweetData = new TweetData(task.getContent().trim());
//
//		// add image
//		if (task.getImg() != null) {
//			try {
//				Resource img = new UrlResource(task.getImg());
//				tweetData = tweetData.withMedia(img);
//			} catch (MalformedURLException e) {
////				tweetData = new TweetData(tweet + " " + toTweet.imageUrl);
//				System.err.println(e.getMessage());
//			}
//		}
//
////		// add flashcard
////		if (toTweet.adjustedLength() > Tweet.MAX_TWEET_LENGTH) {
////			String flashcard = appDomain + "/flashcard/" + toTweet.tweetID;
////
////			try {
////				tweetData = new TweetData(toTweet.trimmedContent()).withMedia(new UrlResource(flashcard));
////			} catch (MalformedURLException e) {
////				tweetData = new TweetData(toTweet.trimmedContent() + " " + flashcard);
////			}
////		}
//
//		// add Geo-Locations
//		if (task.getLongitude() != 0 || task.getLatitude() != 0) {
////			tweetData = tweetData.atLocation(task.getLongitude(), task.getLatitude()).displayCoordinates(true);
//		}
//
//		// update Tweet-Status in DB
////		DBConnector.flagAsTweeted(tweetID, userID);
//
////		
//
//		// tweeting
//
//		
//		MultiValueMap<String, Object> postParameters = tweetData.toRequestParameters();
//		System.out.println(postParameters);
//		// update Status
//		org.springframework.social.twitter.api.Tweet statusUpdate = twitter.timelineOperations()
//				.updateStatus(tweetData);
//		if (replyID > 0) {
//			try {
//				statusUpdate = twitter.timelineOperations().updateStatus(tweetData.inReplyToStatus(replyID));
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		} else {
//			try {
//				statusUpdate = twitter.timelineOperations().updateStatus(tweetData);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			DBConnector.addStatusID(tweetID, statusUpdate.getId());
//		}

	}

	public void publishToLinkedIn(Task task, Provider provider) {

		ObjectMapper m = new ObjectMapper();
		LinkedInShareRq rq = new LinkedInShareRq();
		rq.setAuthor("urn:li:person:" + provider.getId());
		rq.setLifecycleState("PUBLISHED");
		rq.getVisibility().setMemberVisibilty("PUBLIC");
		rq.getSpecificContent().getShareContent().setShareMediaCategory("NONE");
		rq.getSpecificContent().getShareContent().getShareCommentary().setText(task.getContent());

//		try {
//			System.out.println(m.writeValueAsString(rq));
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}

		HttpHeaders headers = new HttpHeaders();
//		headers.add("user-agent",
//				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("x-li-format", "json");
		headers.add("X-Restli-Protocol-Version", "2.0.0");

		headers.add("Authorization", "Bearer " + provider.getToken().trim());

		HttpEntity<LinkedInShareRq> entity = new HttpEntity<>(rq, headers);
		RestTemplate restTemplate = new RestTemplateBuilder().build();
		String rs = restTemplate.exchange("https://api.linkedin.com/v2/ugcPosts", HttpMethod.POST, entity, String.class)
				.getBody();

		System.out.println(rs);
	}

}
