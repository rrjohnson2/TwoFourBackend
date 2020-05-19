package com.jsware.loop.twofour.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jsware.loop.twofour.model.Member;

public interface MemberRepo extends CrudRepository<Member,Long> {
	
	@Query("from Member m where m.email = :email OR m.phone = :phone OR m.username = :username ")
	public Member findByEmailorPhoneNumberorUsername(
			@Param("email") String email,
			@Param("phone") String phone,
			@Param("username")String username);
	
	@Query("select case when count(m)> 0 then true else false end from Member m where  m.email = :email OR m.phone = :phone OR  m.username = :username")
	public boolean existByEmailorPhoneorUsername(
			@Param("email") String email,
			@Param("phone") String phone,
			@Param("username") String username);

}
