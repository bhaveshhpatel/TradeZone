package com.myapps.tradezone.models;

import org.springframework.data.annotation.Id;

public class Tweet {
	@Id
	private String id;
	private String tweetMsg;
	private String userName;
	private String date;
	private Long userId;

	public Tweet(String tweetMsg, String userName, String date, Long userId) {
        this.tweetMsg = tweetMsg;
        this.userName = userName;
        this.date = date;
        this.userId = userId;
    }

	public void setTweetMsg(String msg) {
		this.tweetMsg = msg;
	}

	public void setUserName(String name) {
		this.userName = name;
	}

	public void setDate(String d) {
		this.date = d;
	}

	public void setUserId(Long id) {
		this.userId = id;
	}

	public String getTweetMsg() {
		return tweetMsg;
	}

	public String getUserName() {
		return userName;
	}

	public String getDate() {
		return date;
	}

	public Long getUserId() {
		return userId;
	}
	
	public String toString() {
		return userName + " : " + tweetMsg + "\n" + " - " +date.toString();
	}
}
