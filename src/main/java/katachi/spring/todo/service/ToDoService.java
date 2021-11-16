package katachi.spring.todo.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import katachi.spring.todo.model.UserIdAndName;
import katachi.spring.todo.model.Work;
import katachi.spring.todo.model.WorkEdit;
import katachi.spring.todo.repository.WorkRepository;
import katachi.spring.todo.repository.WorkerRepository;

@Service
public class ToDoService {
	final static int STATUS_YET = 0;
	final static int STATUS_DONE = 1;
	final static int STATUS_SUSPENDED = 2;
	final static int STATUS_UNDEFINED = -1;

	final HashMap<String, Integer> STATUS_CODE_MAP = new HashMap<String, Integer>(){
		{
			put("yet", STATUS_YET);
			put("done", STATUS_DONE);
			put("suspended", STATUS_SUSPENDED);
		}
	};

	@Autowired
	WorkRepository workRepository;

	@Autowired
	WorkerRepository workerRepository;

	public int getStatusCode(String status) {
		switch (status) {
			case "yet":
				return STATUS_YET;
			case "done":
				return STATUS_DONE;
			case "suspended":
				return STATUS_SUSPENDED;
			default:
				return STATUS_UNDEFINED;
		}
	}

	public HashMap<String, Integer> getStatusCodeMap(){
		return STATUS_CODE_MAP;
	}

	public Work findWork(int workId) {
		Work work = workRepository.findById(workId);
		if (work != null) {
			work.setWorkerList(workerRepository.findWorker(work.getId()));
			return work;
		} else {
			return null;
		}
	}

	// すべてのワークを取得
	public ArrayList<Work> getWorkList(){
		ArrayList<Work> workList = new ArrayList<Work>(workRepository.findAll());
		for(Work work : workList) {
			work.setWorkerList(workerRepository.findWorker(work.getId()));
		}
		return workList;
	}

	public boolean register(Work work) {
		Work result = workRepository.saveAndFlush(work);

		if (result != null) {
			int key = result.getId();
			for (UserIdAndName user : work.getWorkerList()) {
				workerRepository.insertOne(key, user.getId());
			}
			return true;
		} else {
			return false;
		}
	}

	@Transactional
	public boolean updateWork(WorkEdit we) {
		int workId = we.getWorkId();
		Work target = workRepository.getById(workId);
		target.setDetail(we.getDetail());
		target.setDeadlineDate(we.getDeadlineDate());
		target.setCompletionDate(we.getCompletionDate());

		Work result = workRepository.saveAndFlush(target);

		if (result != null) {
			workerRepository.deleteByWorkId(workId);
			for (int workerId : we.getWorker()) workerRepository.insertOne(workId, workerId);
			statusUpdater(workId, we.getStatus());
			return true;
		} else {
			return false;
		}

	}

	public boolean statusUpdater(int workId, int status) {
		if (status == STATUS_YET) {
			return workStatusUpdate(workId, STATUS_YET);
		} else if (status == STATUS_DONE) {
			return workStatusUpdate(workId, STATUS_DONE);
		} else if (status == STATUS_SUSPENDED) {
			return workStatusUpdate(workId, STATUS_SUSPENDED);
		}
		return false;
	}

	@Transactional
	public boolean workStatusUpdate(int workId, int status) {
		Work target = workRepository.findById(workId);
		if (target != null) {
			target.setStatus(status);

			if (status == STATUS_YET && target.getCompletionDate() != null) { // 完了日をリセット
				target.setCompletionDate(null);
			} else if (status == STATUS_DONE && target.getCompletionDate() == null) { // 完了日に現在の日付をセット
				target.setCompletionDate(new Date());
			}

			workRepository.saveAndFlush(target);
			return true;
		}
		return false;
	}

	public boolean deleteById(int workId) {
		if (workRepository.deleteById(workId) != null) {
			return true;
		} else {
			return false;
		}
	}

}
