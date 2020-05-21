package com.jsware.loop.twofour.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jsware.loop.twofour.model.Contest;

public interface ContestRepo extends CrudRepository<Contest, Long> {

	@Query("FROM Contest c ORDER BY c.id DESC ")
	Contest findLastest();

}
