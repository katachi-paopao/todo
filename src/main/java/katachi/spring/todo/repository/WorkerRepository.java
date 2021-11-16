package katachi.spring.todo.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import katachi.spring.todo.model.UserIdAndName;

@Repository
public class WorkerRepository {
	@Autowired
	JdbcTemplate jdbc;

	final String FIND_WORKER_SQL = "SELECT u.id, u.last_name, u.first_name FROM work w "
			+ "INNER JOIN worker e ON w.id = e.work_id "
			+ "LEFT JOIN user_master u ON e.user_id = u.id "
			+ "WHERE w.id = ? "
			+ "ORDER BY u.id";

	public boolean insertOne(int workId, int userId) {
		int result = jdbc.update("INSERT INTO worker(work_id, user_id) VALUES (?, ?)", workId, userId);
		if (result > 0) return true;
		else return false;
	}

	public boolean deleteByWorkId(int workId) {
		int result = jdbc.update("DELETE FROM worker WHERE work_id = ?", workId);
		if (result > 0) return true;
		else return false;
	}

	public List<UserIdAndName> findWorker(int workId){
		List<UserIdAndName> workerList = new ArrayList<UserIdAndName>();
		List<Map<String, Object>> mapList = jdbc.queryForList(FIND_WORKER_SQL, workId);

		for(Map<String, Object> map : mapList) {
			UserIdAndName user = new UserIdAndName();
			user.setId((int)map.get("id"));
			user.setLastName((String)map.get("last_name"));
			user.setFirstName((String)map.get("first_name"));

			workerList.add(user);
		}
		return workerList;
	}
}
