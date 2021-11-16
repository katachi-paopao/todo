package katachi.spring.todo.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import katachi.spring.todo.model.UserIdAndName;
import katachi.spring.todo.model.ValidationOrder;
import katachi.spring.todo.model.Work;
import katachi.spring.todo.model.WorkEdit;
import katachi.spring.todo.service.ToDoService;
import katachi.spring.todo.service.UserService;

@RestController
public class TodoAppRestController {
	@Autowired
	ToDoService todoService;

	@Autowired
	UserService userService;

	@GetMapping("todo/rest/getWorkList")
	public ArrayList<Work> getWorkListAPI() {
		ArrayList<Work> workList = new ArrayList<Work>();
		workList = todoService.getWorkList();

		return workList;
	}

	@GetMapping("todo/rest/getUserList")
	public ArrayList<UserIdAndName> getUserListAPI() {
		ArrayList<UserIdAndName> userNameList = userService.findAllUserName();

		return userNameList;
	}

	@GetMapping("todo/rest/getStatusCodeMap")
	public Map<String, Integer> getStatusCodeMapAPI() {
		HashMap<String, Integer> statusCodeMap = todoService.getStatusCodeMap();
		return statusCodeMap;
	}

	@GetMapping("/todo/rest/detail")
	public Work getDetail(@RequestParam("workId") int workId) {
		Work work = todoService.findWork(workId);
		return work;
	}

	@PostMapping("todo/rest/updateStatus")
	public boolean postStatusUpdate(@RequestBody Map<String, Integer> m) {
		return todoService.statusUpdater(m.get("workId"), m.get("status"));
	}

	@PostMapping("todo/rest/deleteWork")
	public boolean postDeleteWork(@RequestBody Map<String, Integer> m) {
		return todoService.deleteById(m.get("workId"));
	}

	@PostMapping("todo/rest/updateWork")
	public ResponseEntity<String> postUpdateWork(@RequestBody Map<String, Object> m) throws ParseException, JsonProcessingException {
		/* 入力の受け取り */
		final int workId = (int)m.get("workId");
		final String workDetail = (String) m.get("workDetail");
		final int workStatus = (int) m.get("workStatus");
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		final Date deadlineDate = m.get("deadlineDate").equals("") ? null : df.parse((String)m.get("deadlineDate"));
		final Date completionDate = m.get("completionDate").equals("") ? null : df.parse((String)m.get("completionDate"));
		final List<Map> workerList = (ArrayList<Map>)m.get("workerList");
		final int[] workerIDList = new int [workerList.size()];
		for (int i=0, s=workerList.size(); i<s; i++) {
			workerIDList[i] = (int) workerList.get(i).get("id");
		}

		/* バリデーションチェック */
		WorkEdit we = new WorkEdit();
		we.setWorkId(workId);
		we.setStatus(workStatus);
		we.setDetail(workDetail);
		we.setDeadlineDate(deadlineDate);
		we.setCompletionDate(completionDate);
		we.setWorker(workerIDList);

		final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        final Set<ConstraintViolation<WorkEdit>> res = validator.validate(we, ValidationOrder.class);

		if (res.size() > 0) {
			final ObjectMapper mapper = new ObjectMapper();
			final Map<String, String> err = new HashMap<String, String>();
			for (ConstraintViolation<WorkEdit> cv : res) {
				err.put(cv.getPropertyPath().toString(), cv.getMessage());
			}

			String json = mapper.writeValueAsString(err);
			return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
		}

		todoService.updateWork(we);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
