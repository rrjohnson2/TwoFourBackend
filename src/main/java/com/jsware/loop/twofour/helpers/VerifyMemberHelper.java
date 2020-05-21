package com.jsware.loop.twofour.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.jsware.loop.twofour.model.Member;

@Component
public class VerifyMemberHelper {
	
	public List<Member> unverified = new ArrayList<Member>();
	
	public HashMap<Integer,Integer> code_to_index = new HashMap<Integer, Integer>();
	
	
	public void addMember(Member mem)
	{
		Random rand = new Random();
		
		int code = rand.nextInt(999999);
		
		unverified.add(mem);
		
		code_to_index.put(code, unverified.size()-1);
		
		countDownToRemoval(code,mem);
	}
	
	private void countDownToRemoval(int code, Member mem) {

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep( 60*1000);
					removeMember(code, mem);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}).start();
		
	}

	public boolean removeMember(int code, Member mem)
	{
		if(code_to_index.get(code) !=null)
		{
			int index = code_to_index.get(code);
			if(mem.equals(unverified.get(index)))
			{
				unverified.remove(index);
				code_to_index.remove(code);
				return true;
			}
		}
		
		
		
		return false;
	}

}
