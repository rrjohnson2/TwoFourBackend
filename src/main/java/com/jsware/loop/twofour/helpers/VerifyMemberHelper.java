package com.jsware.loop.twofour.helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jsware.loop.twofour.model.Member;
import com.jsware.loop.twofour.sending.Email;
import com.jsware.loop.twofour.sending.EmailandPhoneMessage;
import com.jsware.loop.twofour.sending.Text;

@Component
public class VerifyMemberHelper {
	
	public List<Member> unverified = new ArrayList<Member>();
	
	public HashMap<Integer,Integer> code_to_index = new HashMap<Integer, Integer>();
	
	public HashMap<Integer,Integer> index_to_code = new HashMap<Integer, Integer>();
	
	private final String from_email = "looooop.inc@gmail.com";
	private final String from_password ="johnsr41";
	private final String url ="http://localhost:4200/";
	private final String annouce="Ccheckout the new winner " + url;
	
	@Autowired
	EmailandPhoneMessage messageHandler;
	
	
	public void addMember(Member mem)
	{
		
			Random rand = new Random();
			
			int code = rand.nextInt(999999);
			
			
			try {
				
				sendCode(mem, code);
				
				if(unverified.contains(mem))
				{
					int old_index = unverified.indexOf(mem);
					int old_code = index_to_code.get(old_index);
					
					
					code_to_index.remove(old_code);
					index_to_code.remove(old_index);
					unverified.remove(old_index);
					
				}
				
				
				
				unverified.add(mem);
				
				int index =  unverified.size()-1;
				code_to_index.put(code,index);
				index_to_code.put(index, code);
				
				countDownToRemoval(code,mem);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
	}
	
	private void sendCode(Member mem, int code) throws IllegalAccessException, ClientProtocolException, IOException {
		
		String content = "this is your Two Four Viral Code "+code;
		switch (mem.getMessageMedium()) {
		case PHONE:
					messageHandler.sendText(new Text(content, mem.getPhone()));
			break;
		case EMAIL:
			messageHandler.sendEmail(new Email(from_email, mem.getEmail(), "TWO FOUR CODE", content, from_password));
			break;
		case BOTH:
			messageHandler.sendEmail(new Email(from_email, mem.getEmail(), "TWO FOUR CODE", content, from_password));
			messageHandler.sendText(new Text("this is your Two Four Viral Code "+code, mem.getPhone()));
			break;

		default:
			break;
		}
	}

	private void countDownToRemoval(int code, Member mem) {

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep( 3*60*1000);
					removeMember(code, mem);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}).start();
		
	}

	public boolean removeMember(int code, Member mem)
	{
		try {
			
			if(code_to_index.get(code) !=null)
			{
				int index = code_to_index.get(code);
				if(mem.equals(unverified.get(index)))
				{
					unverified.remove(index);
					code_to_index.remove(code);
					index_to_code.remove(index);
					return true;
				}
			}
			
			
			
			return false;
			
		} catch (Exception e) {
			return false;
		}
	}

	public void announceText(Member member) throws IllegalAccessException, ClientProtocolException, IOException {
		
		messageHandler.sendText(new Text(annouce,member.getPhone()));
		
	}

	public void announceEmail(Member member) throws IllegalAccessException, ClientProtocolException, IOException {
		messageHandler.sendEmail(new Email(from_email, member.getEmail(), "TWO FOUR WINNER",annouce, from_password));
		
	}

}
