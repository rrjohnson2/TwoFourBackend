package com.jsware.loop.twofour.model;

public class Submission {
	
	public Submission(Submission sub) {
		this.content_extension = sub.content_extension;
		this.content_url = sub.content_url;
		this.description = sub.description;
		this.member = sub.member; 
		this.rolls =sub.rolls;
	}
	public Submission() {}
	public String  description;
	public Member member;
	public String content_url;
	public String content_extension;
	public int rolls;

}
