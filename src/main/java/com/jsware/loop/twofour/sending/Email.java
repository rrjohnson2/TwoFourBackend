package com.jsware.loop.twofour.sending;

import java.io.File;

public class Email {
	public String from;
	public String to;
	public String subject;
	public String content;
	public String password;
	public File[] attachments;
	
	public Email(String from, String to, String subject, String content, String password) {
		super();
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.content = content;
		this.password = password;
	} 
	
	
}
