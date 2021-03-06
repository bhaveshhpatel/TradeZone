package com.myapps.tradezone.listeners;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
import com.myapps.tradezone.workers.TradeWorker;

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
	
	private static final String ALERT_QUEUE = "alert.queue";
	
	private static final String EQUITY_QUEUE = "equity.queue";
	
	ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	private TweetRepository repository;
    
	@Autowired
	private TradeRepository tradeRepository;
    
	@Autowired
	private AlertRepository alertRepository;
    
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
		int avgStockVol = 0;
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
		String volume = tweetSplitString[10];
		trade.setVolume(volume);
		int adv = avgDailyVol / count;
		double multiple = (double) Integer.parseInt(tweetSplitString[10].replace(",", "")) / adv;
		multiple = Math.round(multiple * 100d) / 100d;
		trade.setAvgDailyOptionsVol(adv);
		trade.setMultipleOfDailyOptionsVol(multiple);
		TradeWorker tWorker = new TradeWorker();
		YEquityData yed = tWorker.getYEquityData(symbol);
		YEquityQuote yeq = yed.getQuery().getResults().getQuote();
		avgStockVol = Integer.parseInt(yeq.getAverageDailyVolume().replace(",", ""));
		trade.setAvgDailyStockVol(avgStockVol);
		double percent = (double) Integer.parseInt(volume) * 10000 / avgStockVol;
		percent = Math.round(percent * 100d) / 100d;
		trade.setPercentOfStockVol(percent);
		System.out.println("Trade info: " + trade.toString());
		System.out.println("Options Data: ");
		try {
		String tradeAsJson = mapper.writeValueAsString(trade);
		System.out.println(trade.toString());
		jmsTemplate.convertAndSend(TRADE_QUEUE, tradeAsJson);
		if (tweet.contains("@ IS")) {
			Alert alert = new Alert();
			alert.setDate(date);
			alert.setSymbol(symbol);
			alert.setEquityName(name);
			StringBuilder alertexp = new StringBuilder(tweetSplitString[7]).append(" ")
					.append(tweetSplitString[8].replace(",", ""));
			alert.setExpiration(alertexp.toString());
			alert.setStrike(tweetSplitString[1]);
			alert.setOption(tweetSplitString[2]);
			alert.setAction(tweetSplitString[3]);
			alert.setVolume(volume);
			String alertAsJson = mapper.writeValueAsString(alert);
			System.out.println(alert.toString());
			jmsTemplate.convertAndSend(ALERT_QUEUE, alertAsJson);
			alertRepository.save(alert);
		}
		Map<String,String> equityMap = new HashMap<String,String>();
		equityMap.put("symbol", symbol);
		equityMap.put("name", name);
		equityMap.put("AverageDailyVolume", yeq.getAverageDailyVolume());
		equityMap.put("YearLow", yeq.getYearLow());
		equityMap.put("YearHigh", yeq.getYearHigh());
		equityMap.put("MarketCapitalization", yeq.getMarketCapitalization());
		equityMap.put("Open", yeq.getOpen());
		equityMap.put("PreviousClose", yeq.getPreviousClose());
		String equityAsJson = mapper.writeValueAsString(equityMap);
		System.out.println(equityAsJson.toString());
		jmsTemplate.convertAndSend(EQUITY_QUEUE, equityAsJson);
		tradeRepository.save(trade);
    	} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
