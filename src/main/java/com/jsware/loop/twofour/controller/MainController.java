package com.jsware.loop.twofour.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsware.loop.twofour.constants.AppConstants;
import com.jsware.loop.twofour.helpers.VerifyMemberHelper;
import com.jsware.loop.twofour.model.Contest;
import com.jsware.loop.twofour.model.Member;
import com.jsware.loop.twofour.model.Submission;
import com.jsware.loop.twofour.model.SubmissionTicket;
import com.jsware.loop.twofour.model.Ticket;
import com.jsware.loop.twofour.repo.ContestRepo;
import com.jsware.loop.twofour.repo.MemberRepo;



@Controller
public class MainController {
	
	@Autowired
	private MemberRepo memRepo;
	
	
	private ContestRepo contestRepo;
	private AppConstants constants;
	private VerifyMemberHelper verify;
	private ObjectMapper mapper;
	
	
	@Autowired
	public MainController(ObjectMapper mapper, AppConstants constants, VerifyMemberHelper verify, ContestRepo contestRepo) {
		super();
		this.verify = verify;
		this.constants = constants;
		this.mapper=mapper;
		this.contestRepo = contestRepo;
		
		
		this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		constants.previousContest= contestRepo.findLastest();
		constants.activeContest = new Contest() ;
		contestRepo.save(constants.activeContest);
		
		manageContest();
	}
	
	private void manageContest() {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				boolean good = true;
				while(good)
				{
					Calendar now = Calendar.getInstance();
					now.setTime(new Date());
					
					try {
						Thread.sleep(constants.activeContest.calendar.getTimeInMillis() - now.getTimeInMillis());
						if(constants.activeContest.winner !=null) /** choose winner**/;
						
						constants.previousContest = constants.activeContest;
						
						constants.activeContest= new Contest();
						
						Iterable<Member> members = memRepo.findAll();
						members.forEach(new Consumer<Member>() {

							@Override
							public void accept(Member member) {
								member.setPost_count(1);
								
							}
							
						});
						memRepo.saveAll(members);
						
						contestRepo.save(constants.activeContest);
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
			}
		}).start();
		
		
	}

	@RequestMapping(value="/login",method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Member> login(@RequestBody Ticket ticket)
	{
		try
		{
			 Member mem = memRepo.findByEmailorPhoneNumberorUsername(ticket.getId());
			 String password = (String) ticket.getData();
			
			if(mem !=null && mem.AccessGranted(password)){
				return ResponseEntity
				        .status(HttpStatus.CREATED)                 
				            .body(mem);
			}
			throw new Exception();
			
		}
		catch(Exception e)
		{
			return ResponseEntity
		            .status(HttpStatus.NOT_FOUND)                 
		            .body(null);
		}
	}
	
	
	
	
	@RequestMapping(value="/generateCode",method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> generateCode(@RequestBody Member mem)
	{
		try {
			if(!memRepo.existByEmailorPhoneorUsername(mem.getEmail(), mem.getPhone(),mem.getUsername()))
			{
				verify.addMember(mem);
				return ResponseEntity
			            .status(HttpStatus.ACCEPTED)                 
			            .body(null);
			}
			
			return ResponseEntity
		            .status(HttpStatus.NOT_ACCEPTABLE)                 
		            .body(null);
		}
		
		

		catch(Exception e)
		{
			return ResponseEntity
		            .status(HttpStatus.NOT_ACCEPTABLE)                 
		            .body(null);
		}
		
		
		
		
		
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/authenticateCode",method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Member> authenticateCode(@RequestBody Ticket ticket) 
	{
		
		try {
			HashMap<String, Object> map = mapper.readValue(
					mapper.writeValueAsString(ticket.getData()),
					HashMap.class);
			
			Member mem =  mapper.readValue(mapper.writeValueAsString(map.get("mem")),Member.class);
			
			int code =  mapper.readValue(mapper.writeValueAsString(map.get("code")),Integer.class);
			
			if(verify.removeMember(code, mem))
			{
				mem.setVerified(true);
				return createMember(mem);
			}
			throw new Exception();
		} catch (Exception e) {
			return ResponseEntity
		            .status(HttpStatus.NOT_ACCEPTABLE)                 
		            .body(null);
		}
	}
	
	
	public ResponseEntity<Member> createMember(Member mem)
	{
		try
		{
				mem = memRepo.save(mem);	
				return ResponseEntity
			            .status(HttpStatus.CREATED)                 
			            .body(mem);
			
		}
		catch(Exception e)
		{
			return ResponseEntity
		            .status(HttpStatus.NOT_ACCEPTABLE)                 
		            .body(null);
		}
	}
	
	@RequestMapping(value="/getContest",method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Contest> getContest()
	{
		return ResponseEntity
	            .status(HttpStatus.ACCEPTED)                 
	            .body(constants.activeContest);
	}
	
	@RequestMapping(value="/getPreviousContest",method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Contest> getPreviousContest()
	{
		return ResponseEntity
	            .status(HttpStatus.ACCEPTED)                 
	            .body(constants.previousContest);
	}
	
	@RequestMapping(value="/submit",method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<SubmissionTicket> submit(@RequestBody Submission sub)
	{
		SubmissionTicket subTicket = constants.submit(sub);
		sub.member.setPost_count(sub.member.getPost_count()-1);
		
		memRepo.save(sub.member);
		
		contestRepo.save(constants.activeContest);
		return ResponseEntity
	            .status(HttpStatus.ACCEPTED)                 
	            .body(subTicket);
	}
	
	
	
	
	
	

}
