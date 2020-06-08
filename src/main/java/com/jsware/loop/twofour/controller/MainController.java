package com.jsware.loop.twofour.controller;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import org.apache.http.client.ClientProtocolException;
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
import com.jsware.loop.twofour.repo.SubmissionRepo;

@Controller
public class MainController {

	@Autowired
	private MemberRepo memRepo;

	@Autowired
	private SubmissionRepo subRepo;

	private ContestRepo contestRepo;

	private AppConstants constants;
	private VerifyMemberHelper verify;
	private ObjectMapper mapper;
	private final Lock lock = new ReentrantLock();
	private boolean winnerChoosen = false;

	@Autowired
	public MainController(ObjectMapper mapper, AppConstants constants, VerifyMemberHelper verify,
			ContestRepo contestRepo) {
		super();
		this.verify = verify;
		this.constants = constants;
		this.mapper = mapper;
		this.contestRepo = contestRepo;

		this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(5 * 1000);
					init(constants, contestRepo);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void init(AppConstants constants, ContestRepo contestRepo) {
		constants.previousContest = contestRepo.findTopByOrderByCalendar();
		constants.activeContest = new Contest();
		contestRepo.save(constants.activeContest);

		manageContest();
	}

	private void manageContest() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				boolean good = true;
				while (good) {
					Calendar now = Calendar.getInstance();
					now.setTime(new Date());

					try {
						Thread.sleep(constants.activeContest.calendar.getTimeInMillis() - now.getTimeInMillis());

						reloadContestChooseWinner();

						Iterable<Member> members = memRepo.findAll();
						members.forEach(new Consumer<Member>() {

							@Override
							public void accept(Member member) {
								try {
									member.setPost_count(1);
									if (member.isNotify()) {
										switch (member.getMessageMedium()) {
										case PHONE:
											annouceWinnerText(member);
											break;
										case EMAIL:
											annouceWinnerEmail(member);
											break;
										case BOTH:
											annouceWinnerText(member);
											annouceWinnerEmail(member);
											break;
										}
									}

								} catch (Exception e) {
									e.printStackTrace();
									Thread.currentThread().interrupt();
								}
							}

						});
						memRepo.saveAll(members);
						contestRepo.save(constants.activeContest);
						constants.refresh();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		}).start();

	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Member> login(@RequestBody Ticket ticket) {
		try {
			Member mem = memRepo.findByEmailorPhoneNumberorUsername(ticket.getId());
			String password = (String) ticket.getData();

			if (mem != null && mem.AccessGranted(password)) {
				return ResponseEntity.status(HttpStatus.CREATED).body(mem);
			}
			throw new Exception();

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@RequestMapping(value = "/generateCode", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> generateCode(@RequestBody Member mem) {
		try {
			if (!memRepo.existByEmailorPhoneorUsername(mem.getEmail(), mem.getPhone(), mem.getUsername())) {
				verify.addMember(mem);
				return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
			}

			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
		}

		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
		}

	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/authenticateCode", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Member> authenticateCode(@RequestBody Ticket ticket) {

		try {
			HashMap<String, Object> map = mapper.readValue(mapper.writeValueAsString(ticket.getData()), HashMap.class);

			Member mem = mapper.readValue(mapper.writeValueAsString(map.get("mem")), Member.class);

			int code = mapper.readValue(mapper.writeValueAsString(map.get("code")), Integer.class);

			if (verify.removeMember(code, mem)) {
				mem.setVerified(true);
				return createMember(mem);
			}
			throw new Exception();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
		}
	}

	public ResponseEntity<Member> createMember(Member mem) {
		try {
			mem = memRepo.save(mem);
			return ResponseEntity.status(HttpStatus.CREATED).body(mem);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
		}
	}

	@RequestMapping(value = "/getContest", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Contest> getContest() {
		try {
			tryLock();
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(constants.activeContest);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@RequestMapping(value = "/getBackups", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<Submission>> getBackups() {
		try {
			tryLock();
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(constants.activeContest.backups);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	private void tryLock() throws InterruptedException {
		lock.lock();

		lock.unlock();
	}

	@RequestMapping(value = "/getPreviousContest", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Contest> getPreviousContest() {
		try {
			tryLock();
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(constants.previousContest);
		} catch (Exception e) {
			e.printStackTrace();

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<SubmissionTicket> submit(@RequestBody Submission sub) {

		try {

			String[] ids = new String[] { sub.member.getUsername(), sub.member.getEmail(), sub.member.getPhone() };

			Member member = null;

			for (String id : ids) {

				if (id != null)
					member = memRepo.findByEmailorPhoneNumberorUsername(id);

				if (member != null)
					break;
			}

			if (member == null || member.getPost_count() <= Double.NEGATIVE_INFINITY)
				throw new Exception();

			sub.member = member;

			SubmissionTicket subTicket = constants.submit(sub);
			sub.member.setPost_count(sub.member.getPost_count() - 1);

			if (subTicket.backupSlot != null) {
				subRepo.save(sub);
			}

			memRepo.save(sub.member);

			contestRepo.save(constants.activeContest);

			return ResponseEntity.status(HttpStatus.ACCEPTED).body(subTicket);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}
	}

	@RequestMapping(value = "/chooseWinner", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Object> chooseWinner(@RequestBody long choice) {
		try {
			if (choice >= 0) {
				if (!constants.activeContest.backups.isEmpty())
					constants.activeContest.loadSubmission(subRepo.findById(choice).get());
				else
					constants.activeContest.nullify();
			}
			winnerChoosen = true;
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}
	}

	@RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Object> updatePassword(@RequestBody Ticket ticket) {
		try {

			Member member = memRepo.findByEmailorPhoneNumberorUsername(ticket.getId());

			if (member != null) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = mapper.readValue(mapper.writeValueAsBytes(ticket.getData()),
						HashMap.class);

				String old = map.get("old_password");

				if (member.AccessGranted(old)) {
					member.setPassword(map.get("password"));
					memRepo.save(member);
					return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
				}

			}
			throw new Exception();

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Object> update(@RequestBody Ticket ticket) {
		try {

			Member mem = memRepo.findByEmailorPhoneNumberorUsername(ticket.getId());

			mem.lodaMember(mapper.readValue(mapper.writeValueAsString(ticket.getData()), Member.class));

			memRepo.save(mem);

			return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}
	}

	private void reloadContestChooseWinner()
			throws InterruptedException, IllegalAccessException, ClientProtocolException, IOException {

		verify.alertAdmin();
		;
		boolean youLocked = false;
		while (!winnerChoosen) {
			if (!youLocked) {
				lock.lock();
				youLocked = true;
			}
			Thread.sleep(1000 * 10);

		}
		if (youLocked) {
			lock.unlock();

		}
		;

		constants.previousContest = constants.activeContest;
		constants.activeContest = new Contest();
		winnerChoosen = false;

	}

	private void annouceWinnerEmail(Member member) throws IllegalAccessException, ClientProtocolException, IOException {
		verify.announceEmail(member);
	}

	private void annouceWinnerText(Member member) throws IllegalAccessException, ClientProtocolException, IOException {
		verify.announceText(member);
	}

}
