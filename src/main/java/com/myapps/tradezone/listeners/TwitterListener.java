package com.myapps.tradezone.listeners;

import java.io.IOException;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import twitter4j.*;
import twitter4j.conf.*;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapps.tradezone.models.*;
import com.myapps.tradezone.services.*;

/**
 * This is a listener class which listens to and notifies about new tweets from twitter. It used the twitter4j
 * library to connect and listen to Twitter tweets.
 *
 * @author Bhavesh Patel
 */
@Component
@Configurable
public class TwitterListener {

	private static final String SIMPLE_QUEUE = "simple.queue";
	
	@Autowired
	private TweetRepository repository;
    
	@Autowired
	private JmsTemplate jmsTemplate;
    
   public void addTweet(String tweetJson) {
        jmsTemplate.convertAndSend(SIMPLE_QUEUE, tweetJson);
    }
   
   @Autowired
   private MongoTemplate mongoTemplate;

   public static final String COLLECTION_NAME = "Equity_Options_Volume_0";

   public void equityOptionsData(String symbol) {
       List<EquityOptions> results = null;
       Query query = new Query();
       query.addCriteria(Criteria.where("symbol").is(symbol));
       results = this.mongoTemplate.find(query, EquityOptions.class, COLLECTION_NAME);
       EquityOptions eq1 = results.get(0);
       System.out.println("Equity Options Data: " + eq1.getSymbol() + " " + eq1.getEquityName() + " " + eq1.getTotalAvgDaily());
   }
   
	public void initConfiguration(){
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true);
        cb.setOAuthConsumerKey("ohPo9nOiWk8BUE1uLcIXwrEA8");
        cb.setOAuthConsumerSecret("G45B1oRsygTjba0yzEu2s3lG7couog7pYisxUWrH7MH57q3Pfk");
        cb.setOAuthAccessToken("304728296-qv1itXbrS6bcsilicy0uBQAXFAERemhGv09eQwFr");
        cb.setOAuthAccessTokenSecret("dLKXPEWmOWjaj8g39OK42jZl2ovuEhRR4HU1ou25yOTe3");
        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        twitterStream.addListener(listener);
        twitterStream.user();

    }

	UserStreamListener listener = new UserStreamListener() {
		
		ObjectMapper mapper = new ObjectMapper();

	    @Override
		public void onStatus(Status status) {
	    	if (status.getUser().getId() == 4170993693L && status.getText().contains("Activity expiring on")) {
	    	try {
			String date = DateFormat.getDateTimeInstance().format(new Date());
			Tweet tweet = new Tweet(
					status.getText(), status.getUser().getScreenName(), date, status.getUser().getId());
			String tweeAsJson = mapper.writeValueAsString(tweet);
			System.out.println(tweet.toString());
			repository.save(tweet);
			addTweet(tweeAsJson);
			equityOptionsData("ORCL");
			//addTweet(tweet.toString());
	    	} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	}
		}

		@Override
		public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
			System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
		}

		@Override
		public void onDeletionNotice(long directMessageId, long userId) {
			System.out.println("Got a direct message deletion notice id:" + directMessageId);
		}

		@Override
		public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
			System.out.println("Got a track limitation notice:" + numberOfLimitedStatuses);
		}

		@Override
		public void onScrubGeo(long userId, long upToStatusId) {
			System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
		}

		@Override
		public void onStallWarning(StallWarning warning) {
			System.out.println("Got stall warning:" + warning);
		}

		@Override
		public void onFriendList(long[] friendIds) {
			System.out.print("onFriendList");
			for (long friendId : friendIds) {
				System.out.print(" " + friendId);
			}
			System.out.println();
		}

		@Override
		public void onFavorite(User source, User target, Status favoritedStatus) {
			System.out.println("onFavorite source:@" + source.getScreenName() + " target:@" + target.getScreenName()
					+ " @" + favoritedStatus.getUser().getScreenName() + " - " + favoritedStatus.getText());
		}

		@Override
		public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
			System.out.println("onUnFavorite source:@" + source.getScreenName() + " target:@" + target.getScreenName()
					+ " @" + unfavoritedStatus.getUser().getScreenName() + " - " + unfavoritedStatus.getText());
		}

		@Override
		public void onFollow(User source, User followedUser) {
			System.out
					.println("onFollow source:@" + source.getScreenName() + " target:@" + followedUser.getScreenName());
		}

		@Override
		public void onUnfollow(User source, User followedUser) {
			System.out
					.println("onFollow source:@" + source.getScreenName() + " target:@" + followedUser.getScreenName());
		}

		@Override
		public void onDirectMessage(DirectMessage directMessage) {
			System.out.println("onDirectMessage text:" + directMessage.getText());
		}

		@Override
		public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {
			System.out.println("onUserListMemberAddition added member:@" + addedMember.getScreenName() + " listOwner:@"
					+ listOwner.getScreenName() + " list:" + list.getName());
		}

		@Override
		public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {
			System.out.println("onUserListMemberDeleted deleted member:@" + deletedMember.getScreenName()
					+ " listOwner:@" + listOwner.getScreenName() + " list:" + list.getName());
		}

		@Override
		public void onUserListSubscription(User subscriber, User listOwner, UserList list) {
			System.out.println("onUserListSubscribed subscriber:@" + subscriber.getScreenName() + " listOwner:@"
					+ listOwner.getScreenName() + " list:" + list.getName());
		}

		@Override
		public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {
			System.out.println("onUserListUnsubscribed subscriber:@" + subscriber.getScreenName() + " listOwner:@"
					+ listOwner.getScreenName() + " list:" + list.getName());
		}

		@Override
		public void onUserListCreation(User listOwner, UserList list) {
			System.out
					.println("onUserListCreated  listOwner:@" + listOwner.getScreenName() + " list:" + list.getName());
		}

		@Override
		public void onUserListUpdate(User listOwner, UserList list) {
			System.out
					.println("onUserListUpdated  listOwner:@" + listOwner.getScreenName() + " list:" + list.getName());
		}

		@Override
		public void onUserListDeletion(User listOwner, UserList list) {
			System.out.println(
					"onUserListDestroyed  listOwner:@" + listOwner.getScreenName() + " list:" + list.getName());
		}

		@Override
		public void onUserProfileUpdate(User updatedUser) {
			System.out.println("onUserProfileUpdated user:@" + updatedUser.getScreenName());
		}

		@Override
		public void onUserDeletion(long deletedUser) {
			System.out.println("onUserDeletion user:@" + deletedUser);
		}

		@Override
		public void onUserSuspension(long suspendedUser) {
			System.out.println("onUserSuspension user:@" + suspendedUser);
		}

		@Override
		public void onBlock(User source, User blockedUser) {
			System.out.println("onBlock source:@" + source.getScreenName() + " target:@" + blockedUser.getScreenName());
		}

		@Override
		public void onUnblock(User source, User unblockedUser) {
			System.out.println(
					"onUnblock source:@" + source.getScreenName() + " target:@" + unblockedUser.getScreenName());
		}

		@Override
		public void onRetweetedRetweet(User source, User target, Status retweetedStatus) {
			System.out.println(
					"onRetweetedRetweet source:@" + source.getScreenName() + " target:@" + target.getScreenName()
							+ retweetedStatus.getUser().getScreenName() + " - " + retweetedStatus.getText());
		}

		@Override
		public void onFavoritedRetweet(User source, User target, Status favoritedRetweet) {
			System.out.println(
					"onFavroitedRetweet source:@" + source.getScreenName() + " target:@" + target.getScreenName()
							+ favoritedRetweet.getUser().getScreenName() + " - " + favoritedRetweet.getText());
		}

		@Override
		public void onQuotedTweet(User source, User target, Status quotingTweet) {
			System.out.println("onQuotedTweet" + source.getScreenName() + " target:@" + target.getScreenName()
					+ quotingTweet.getUser().getScreenName() + " - " + quotingTweet.getText());
		}

		@Override
		public void onException(Exception ex) {
			ex.printStackTrace();
			System.out.println("onException:" + ex.getMessage());
		}
	};
}
