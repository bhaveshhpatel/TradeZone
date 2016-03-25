package com.myapps.tradezone.listeners;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

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

	private static final String TWEET_QUEUE = "tweet.queue";
	
	private static final String TRADE_QUEUE = "trade.queue";
	
	ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	private TweetRepository repository;
    
	@Autowired
	private TradeRepository tradeRepository;
    
	@Autowired
	private JmsTemplate jmsTemplate;
    
   public void addTweet(String tweetJson) {
        jmsTemplate.convertAndSend(TWEET_QUEUE, tweetJson);
    }
   
   @Autowired
   private MongoTemplate mongoTemplate;

   public static final String COLLECTION_NAME_0 = "Equity_Options_Volume_0";

   public static final String COLLECTION_NAME_1 = "Equity_Options_Volume_1";

   public static final String COLLECTION_NAME_2 = "Equity_Options_Volume_2";

	public void equityOptionsData(String tweet, String date) {
		String[] tweetSplitString = tweet.split(" ");
		int avgDailyVol = 0;
		int count = 0;
		String name = "";
		List<EquityOptions> results = null;
		String symbol = tweetSplitString[0].replace("$", "").trim();
		Query query = new Query();
		query.addCriteria(Criteria.where("symbol").is(symbol));
		results = this.mongoTemplate.find(query, EquityOptions.class, COLLECTION_NAME_0);
		if (!results.isEmpty()) {
			name = ((EquityOptions) results.get(0)).getEquityName();
			avgDailyVol += Integer.parseInt(((EquityOptions) results.get(0)).getTotalAvgDaily().replace(",", ""));
			count++;
		}
		results = this.mongoTemplate.find(query, EquityOptions.class, COLLECTION_NAME_1);
		if (!results.isEmpty()) {
			avgDailyVol += Integer.parseInt(((EquityOptions) results.get(0)).getTotalAvgDaily().replace(",", ""));
			count++;
		}
		results = this.mongoTemplate.find(query, EquityOptions.class, COLLECTION_NAME_2);
		if (!results.isEmpty()) {
			avgDailyVol += Integer.parseInt(((EquityOptions) results.get(0)).getTotalAvgDaily().replace(",", ""));
			count++;
		}
		Trade trade = new Trade();
		trade.setDate(date);
		trade.setSymbol(symbol);
		trade.setEquityName(name);
		StringBuilder exp = new StringBuilder(tweetSplitString[7]).append(" ")
				.append(tweetSplitString[8].replace(",", ""));
		trade.setExpiration(exp.toString());
		trade.setStrike(tweetSplitString[1]);
		trade.setOption(tweetSplitString[2]);
		trade.setAction(tweetSplitString[3]);
		trade.setVolume(tweetSplitString[10]);
		int adv = avgDailyVol / count;
		double multiple = (double) Integer.parseInt(tweetSplitString[10].replace(",", "")) / adv;
		multiple = Math.round(multiple * 100d) / 100d;
		trade.setAvgDailyOptionsVol(adv);
		trade.setMultipleOfDailyOptionsVol(multiple);
		System.out.println("Trade info: " + trade.toString());
		try {
		String tradeAsJson = mapper.writeValueAsString(trade);
		System.out.println(trade.toString());
		jmsTemplate.convertAndSend(TRADE_QUEUE, tradeAsJson);
		//getEquityData(symbol);
		retrieve(symbol);
		tradeRepository.save(trade);
    	} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void retrieve(String requestedSymbol) {
		RestTemplate rTemplate = new RestTemplate();
	    rTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
	    String env = "http://datatables.org/alltables.env";
	  String symbolString = "%28%22" + requestedSymbol + "%22%29";
	  String fmt="json";
	  String queryStr = "select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20IN%20";
	  String restJsonUrl = "http://query.yahooapis.com/v1/public/yql?q="
		  + queryStr + symbolString + "&format=" + fmt + "&env=" + env;
	  restJsonUrl = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20IN%20(%22YHOO%22)&format=json&env=http://datatables.org/alltables.env";
	  System.out.println("URL: " + restJsonUrl);
	  Wrapper response =rTemplate.getForObject(restJsonUrl, Wrapper.class);
	}
   
	public void getEquityData(String symbol) {
		String url = "http://query.yahooapis.com/v1/public/yql?q=select%20"
				+ "symbol,AverageDailyVolume%20from%20yahoo.finance.quotes%20where%20symbol%20IN%20(\""
				+ symbol + "\")&format=json&env=http://datatables.org/alltables.env";
				//String url = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20IN%20(%22YHOO%22)&format=json&env=http://datatables.org/alltables.env";
		System.out.println("URL: " + url);
		RestTemplate restTemplate = new RestTemplate();
	    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
	    Query query = restTemplate.getForObject(url, Query.class);
		//System.out.println("Equities for " + equities.getSymbol() + " Avg Daily Vol: " + equities.getAverageDailyVolume());
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

	    @Override
		public void onStatus(Status status) {
	    	//if (status.getUser().getId() == 4170993693L && status.getText().contains("Activity expiring on")) {
	    	if (status.getText().contains("Activity expiring on")) {
	    	try {
	    		SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
	    		format.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
			String date = format.format(new Date());
			Tweet tweet = new Tweet(
					status.getText(), status.getUser().getScreenName(), date, status.getUser().getId());
			String tweetAsJson = mapper.writeValueAsString(tweet);
			System.out.println(tweet.toString());
			repository.save(tweet);
			addTweet(tweetAsJson);
			equityOptionsData(status.getText(), date);
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
			//System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
		}

		@Override
		public void onDeletionNotice(long directMessageId, long userId) {
			//System.out.println("Got a direct message deletion notice id:" + directMessageId);
		}

		@Override
		public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
			//System.out.println("Got a track limitation notice:" + numberOfLimitedStatuses);
		}

		@Override
		public void onScrubGeo(long userId, long upToStatusId) {
			//System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
		}

		@Override
		public void onStallWarning(StallWarning warning) {
			//System.out.println("Got stall warning:" + warning);
		}

		@Override
		public void onFriendList(long[] friendIds) {
			/*
			System.out.print("onFriendList");
			for (long friendId : friendIds) {
				System.out.print(" " + friendId);
			}
			System.out.println();
			*/
		}

		@Override
		public void onFavorite(User source, User target, Status favoritedStatus) {
			//System.out.println("onFavorite source:@" + source.getScreenName() + " target:@" + target.getScreenName()
			//		+ " @" + favoritedStatus.getUser().getScreenName() + " - " + favoritedStatus.getText());
		}

		@Override
		public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
			//System.out.println("onUnFavorite source:@" + source.getScreenName() + " target:@" + target.getScreenName()
			//		+ " @" + unfavoritedStatus.getUser().getScreenName() + " - " + unfavoritedStatus.getText());
		}

		@Override
		public void onFollow(User source, User followedUser) {
			//System.out
			//		.println("onFollow source:@" + source.getScreenName() + " target:@" + followedUser.getScreenName());
		}

		@Override
		public void onUnfollow(User source, User followedUser) {
			//System.out
			//		.println("onFollow source:@" + source.getScreenName() + " target:@" + followedUser.getScreenName());
		}

		@Override
		public void onDirectMessage(DirectMessage directMessage) {
			//System.out.println("onDirectMessage text:" + directMessage.getText());
		}

		@Override
		public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {
			//System.out.println("onUserListMemberAddition added member:@" + addedMember.getScreenName() + " listOwner:@"
			//		+ listOwner.getScreenName() + " list:" + list.getName());
		}

		@Override
		public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {
			//System.out.println("onUserListMemberDeleted deleted member:@" + deletedMember.getScreenName()
			//		+ " listOwner:@" + listOwner.getScreenName() + " list:" + list.getName());
		}

		@Override
		public void onUserListSubscription(User subscriber, User listOwner, UserList list) {
			//System.out.println("onUserListSubscribed subscriber:@" + subscriber.getScreenName() + " listOwner:@"
			//		+ listOwner.getScreenName() + " list:" + list.getName());
		}

		@Override
		public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {
			//System.out.println("onUserListUnsubscribed subscriber:@" + subscriber.getScreenName() + " listOwner:@"
			//		+ listOwner.getScreenName() + " list:" + list.getName());
		}

		@Override
		public void onUserListCreation(User listOwner, UserList list) {
			//System.out
			//		.println("onUserListCreated  listOwner:@" + listOwner.getScreenName() + " list:" + list.getName());
		}

		@Override
		public void onUserListUpdate(User listOwner, UserList list) {
			//System.out
			//		.println("onUserListUpdated  listOwner:@" + listOwner.getScreenName() + " list:" + list.getName());
		}

		@Override
		public void onUserListDeletion(User listOwner, UserList list) {
			//System.out.println(
			//		"onUserListDestroyed  listOwner:@" + listOwner.getScreenName() + " list:" + list.getName());
		}

		@Override
		public void onUserProfileUpdate(User updatedUser) {
			//System.out.println("onUserProfileUpdated user:@" + updatedUser.getScreenName());
		}

		@Override
		public void onUserDeletion(long deletedUser) {
			//System.out.println("onUserDeletion user:@" + deletedUser);
		}

		@Override
		public void onUserSuspension(long suspendedUser) {
			//System.out.println("onUserSuspension user:@" + suspendedUser);
		}

		@Override
		public void onBlock(User source, User blockedUser) {
			//System.out.println("onBlock source:@" + source.getScreenName() + " target:@" + blockedUser.getScreenName());
		}

		@Override
		public void onUnblock(User source, User unblockedUser) {
			//System.out.println(
			//		"onUnblock source:@" + source.getScreenName() + " target:@" + unblockedUser.getScreenName());
		}

		@Override
		public void onRetweetedRetweet(User source, User target, Status retweetedStatus) {
			//System.out.println(
			//		"onRetweetedRetweet source:@" + source.getScreenName() + " target:@" + target.getScreenName()
			//				+ retweetedStatus.getUser().getScreenName() + " - " + retweetedStatus.getText());
		}

		@Override
		public void onFavoritedRetweet(User source, User target, Status favoritedRetweet) {
			//System.out.println(
			//		"onFavroitedRetweet source:@" + source.getScreenName() + " target:@" + target.getScreenName()
			//				+ favoritedRetweet.getUser().getScreenName() + " - " + favoritedRetweet.getText());
		}

		@Override
		public void onQuotedTweet(User source, User target, Status quotingTweet) {
			//System.out.println("onQuotedTweet" + source.getScreenName() + " target:@" + target.getScreenName()
			//		+ quotingTweet.getUser().getScreenName() + " - " + quotingTweet.getText());
		}

		@Override
		public void onException(Exception ex) {
			ex.printStackTrace();
			System.out.println("onException:" + ex.getMessage());
		}
	};
}
