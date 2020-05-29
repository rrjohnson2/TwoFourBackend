package com.jsware.loop.twofour.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.jsware.loop.twofour.model.Contest;

public interface ContestRepo extends CrudRepository<Contest, Long> {

	
	Contest  findTopByOrderByCalendar();

}
