package com.jsware.loop.twofour.model;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@SequenceGenerator(name="mem_seq", initialValue=1)
public class Member {
	
	@Id
	@JsonIgnore
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="mem_seq")
	private long id;
	
	@Column( unique = true ,length=15)
	private String username;
	
	@Column( unique = true ,length=30)
	private String email;
	
	@Column( unique = true ,length=15)
	private String phone;
	
	@JsonIgnore
	private String salt;
	
	@JsonIgnore
	private String saltyPassword;
	
	
	private boolean verified;
	
	
	private  int post_count = 1;
	
	private String facebook;
	
	private String instgram;
	 
	private String twitter;
	
	public boolean isVerified() {
		return verified;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public int getPost_count() {
		return post_count;
	}

	public void setPost_count(int post_count) {
		this.post_count = post_count;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getSaltyPassword() {
		return saltyPassword;
	}

	public void setSaltyPassword(String saltyPassword) {
		this.saltyPassword = saltyPassword;
	}

	public String getFacebook() {
		return facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	public String getInstgram() {
		return instgram;
	}

	public void setInstgram(String instgram) {
		this.instgram = instgram;
	}

	public String getTwitter() {
		return twitter;
	}

	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}

	public void setPassword(String password)
	{
		Random rand = new  SecureRandom();
		
		byte[] saltyBytes = new byte[32];
		
		rand.nextBytes(saltyBytes);
		
		this.salt = Base64.getEncoder().encodeToString(saltyBytes);
		
		this.saltyPassword= salt+encrypt(this.salt,password)+salt;
	}
	
	private String encrypt(String salt, String password) {
		/*
		 * This encryption method takes the salt and password mixes it up by alternating 
		 * characters into a result variable salt starts first;
		 * but if one or the other runs out the the crypt uses the remaining characters of the other list
		 */
		char[] res = new char[salt.length()+password.length()];
		
		char[] salt_char = salt.toCharArray();
		char[] password_char = password.toCharArray();
		
		int salt_count=0;
		int salt_max = salt.length();
		int password_count =0;
		int password_max = password.length();
		
		for(int i = 0; i<res.length;i++)
		{
			if(i%2==0)
			{
				if(salt_count<salt_max)
				{
					res[i]=salt_char[salt_count];
					salt_count++;
				}
				else {
					populateRemaining(i,password_char,password_count,res);
					break;
				}
			}
			
			else
			{
				if(password_count<password_max)
					{
						
						res[i]= password_char[password_count];
						password_count++;
					}
				else {
					populateRemaining(i,salt_char,salt_count,res);
					break;
					}
				
			}
		}
			
		return new String(res);
	}
	
	private void populateRemaining(int current, char[] _char, int _count, char[] res) {
		for(int i= current;i<res.length;i++)
		{
			res[i]=_char[_count];
			_count++;
		}
	}


	public boolean AccessGranted(String password)
	{
		return saltyPassword.equals(salt+encrypt(salt,password)+salt);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Member other = (Member) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	
	

}
