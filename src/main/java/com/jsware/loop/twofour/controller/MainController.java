package com.jsware.loop.twofour.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jsware.loop.twofour.constants.AppConstants;
import com.jsware.loop.twofour.model.Member;
import com.jsware.loop.twofour.repo.MemberRepo;



@Controller
public class MainController {
	
	@Autowired
	private MemberRepo memRepo;
	
	private AppConstants constants= new AppConstants();
	
	
	
	@RequestMapping(value="/createMember",method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> createMember(@RequestBody Member mem)
	{
		try
		{
			if(!memRepo.existByEmailorPhoneorUsername(mem.getEmail(), mem.getPhone(),mem.getUsername()))
			{
				mem = memRepo.save(mem);
			
				return ResponseEntity
			            .status(HttpStatus.CREATED)                 
			            .body("USER ADDED");
			}
			
			throw new Exception();
			
		}
		catch(Exception e)
		{
			return ResponseEntity
		            .status(HttpStatus.NOT_ACCEPTABLE)                 
		            .body("USER CANNOT BE ADDED");
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
	
	

}
