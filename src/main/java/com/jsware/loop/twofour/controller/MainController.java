package com.jsware.loop.twofour.controller;

import java.util.Date;
import java.util.HashMap;

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
import com.jsware.loop.twofour.model.Member;
import com.jsware.loop.twofour.model.Ticket;
import com.jsware.loop.twofour.repo.MemberRepo;



@Controller
public class MainController {
	
	@Autowired
	private MemberRepo memRepo;
	
	private AppConstants constants= new AppConstants();
	
	private VerifyMemberHelper verify = new VerifyMemberHelper();
	
	private ObjectMapper mapper;
	
	
	@Autowired
	public MainController(ObjectMapper mapper) {
		super();
		this.mapper=mapper;
		this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
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
				//sendEmailorTextMessage
				verify.addMember(mem);
				return ResponseEntity
			            .status(HttpStatus.CREATED)                 
			            .body("CODE GENERATED");
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
	
	@RequestMapping(value="/getContestTime",method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Date> getContestTime()
	{
		return ResponseEntity
	            .status(HttpStatus.ACCEPTED)                 
	            .body(constants.contestTime);
	}
	
	@RequestMapping(value="/getSubCount",method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Integer> getSubCount()
	{
		return ResponseEntity
	            .status(HttpStatus.ACCEPTED)                 
	            .body(constants.sub_count);
	}
	
	
	
	

}
