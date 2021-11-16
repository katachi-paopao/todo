package katachi.spring.todo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import katachi.spring.todo.model.SignupForm;
import katachi.spring.todo.model.User;
import katachi.spring.todo.model.UserDetailsImpl;
import katachi.spring.todo.model.UserIdAndName;
import katachi.spring.todo.model.ValidationOrder;
import katachi.spring.todo.model.Work;
import katachi.spring.todo.model.WorkRegisterForm;
import katachi.spring.todo.service.ToDoService;
import katachi.spring.todo.service.UserService;

@Controller
public class TodoAppController {
	@Autowired
	ToDoService todoService;

	@Autowired
	UserService userService;

	@Autowired
	PasswordEncoder passwordEncoder;

	@PostMapping("/todo/userEdit/{id}")
	public String postUserEdit(@PathVariable("id") int id,
			@AuthenticationPrincipal UserDetailsImpl loggedUser,
			@ModelAttribute WorkRegisterForm workRegisterForm,
			@ModelAttribute @Validated(ValidationOrder.class)  SignupForm signupForm,
			BindingResult br,
			Model model) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof UserDetailsImpl && ((UserDetailsImpl) principal).getId() == id) {
			if (br.hasErrors() ) {
				return getUserEdit(id, workRegisterForm, signupForm, model);
			}

			if ( signupForm.getMail().equals( ((UserDetailsImpl) principal).getUsername() ) == false
					&& userService.duplicateCheckForMail(signupForm.getMail()) ) {
				model.addAttribute("mailDuplicated", "入力されたメールアドレスは既に登録されています");
				return getUserEdit(id, workRegisterForm, signupForm, model);
			}

			if( signupForm.getConfirmPassword().equals( signupForm.getPassword() ) == false ) {
				model.addAttribute("passwordMismatch", "入力パスワードと確認パスワードが異なっています");
				return getUserEdit(id, workRegisterForm, signupForm, model);
			}

			User user = new User();
			user.setId( ((UserDetailsImpl) principal).getId() );
			user.setMail(signupForm.getMail());
			user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
			user.setLastName(signupForm.getLastName());
			user.setFirstName(signupForm.getFirstName());
			userService.register(user);

			loggedUser.setMail(user.getMail());
			loggedUser.setLastName(user.getLastName());
			loggedUser.setFirstName(user.getFirstName());
		}
		return "redirect:/todo";
	}

	@GetMapping("/todo")
	public String getHome(@ModelAttribute WorkRegisterForm workRegisterForm, Model model) {
		model.addAttribute("contents", "todoApp/contents_todoList :: todoList");
		return "/todoApp/homeLayout";
	}

	@GetMapping("/todo/userEdit/{id}")
	public String getUserEdit(@PathVariable("id") int id,
			@ModelAttribute WorkRegisterForm workRegisterForm,
			@ModelAttribute SignupForm signupForm,
			Model model) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof UserDetailsImpl && ((UserDetailsImpl) principal).getId() == id) {
			User user = userService.findById(id);
			model.addAttribute("user", user);

			ArrayList<UserIdAndName> userNameList = userService.findAllUserName();
			model.addAttribute("userNameList", userNameList);

			model.addAttribute("contents", "todoApp/contents_userEdit :: userEdit");
			return "/todoApp/homeLayout";
		}
		return "redirect:/todo";
	}

	@PostMapping("/todo/workRegister")
	public String postWorkRegister(
			@ModelAttribute @Validated(ValidationOrder.class) WorkRegisterForm workRegisterForm,
			BindingResult br,
			Model model) {

		if (br.hasErrors()) {
			model.addAttribute("bindingError", "bindingError");
			return getHome(workRegisterForm, model);
		}

		Work work = new Work();
		work.setName(workRegisterForm.getName());
		work.setStatus(0);
		work.setDetail(workRegisterForm.getDetail());
		work.setDeadlineDate(workRegisterForm.getDeadlineDate());

		List<UserIdAndName> workerList = new ArrayList<UserIdAndName>();
		for (int userId : workRegisterForm.getWorker()) {
			UserIdAndName worker = new UserIdAndName();
			worker.setId(userId);
			workerList.add(worker);
		}
		work.setWorkerList(workerList);

		todoService.register(work);

		return "redirect:/todo";
	}


	@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Work Not Found")
	public class WorkNotFoundException extends RuntimeException {
	}

	@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Optimistic Locking Failure")
	class OptLockConFlictException extends RuntimeException {}
}