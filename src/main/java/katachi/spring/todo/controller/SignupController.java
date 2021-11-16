package katachi.spring.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import katachi.spring.todo.model.SignupForm;
import katachi.spring.todo.model.User;
import katachi.spring.todo.model.ValidationOrder;
import katachi.spring.todo.service.ToDoService;
import katachi.spring.todo.service.UserService;

@Controller
public class SignupController {
	@Autowired
	UserService userService;

	@Autowired
	ToDoService todoService;

	@Autowired
	PasswordEncoder passwordEncoder;

	@GetMapping("/signup")
	public String getSignup(@ModelAttribute SignupForm signupForm, Model model) {
		return "signup";
	}

	@PostMapping("/signup")
	public String postSignupVue(@ModelAttribute @Validated(ValidationOrder.class) SignupForm signupForm, BindingResult br,
			Model model) {
		if (br.hasErrors()) {
			return getSignup(signupForm, model);
		}

		if ( userService.duplicateCheckForMail(signupForm.getMail()) ) {
			model.addAttribute("mailDuplicated", "入力されたメールアドレスは既に登録されています");
			return getSignup(signupForm, model);
		}

		if( signupForm.getConfirmPassword().equals( signupForm.getPassword() ) ) {
			model.addAttribute("signupForm", signupForm);
			return "signupConfirm";
		} else {
			model.addAttribute("passwordMismatch", "入力パスワードと確認パスワードが異なっています");
			return getSignup(signupForm, model);
		}

	}

	@GetMapping("signupConfirm")
	public String getSignupConfirm(Model model) {
		if (model.getAttribute("signupForm") != null) {
			return "signupConfirm";
		} else {
			return "redirect:/signup";
		}
	}

	@PostMapping(value="/register", params="register")
	public String postRegister(RedirectAttributes redirectAttributes, @ModelAttribute @Validated(ValidationOrder.class) SignupForm signupForm, BindingResult br, Model model) throws Exception {
			if (br.hasErrors() || userService.duplicateCheckForMail(signupForm.getMail()) ) { // 再チェック
				return getSignup(signupForm, model);
			}

			User user = new User();
			user.setMail(signupForm.getMail());
			user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
			user.setLastName(signupForm.getLastName());
			user.setFirstName(signupForm.getFirstName());
			userService.register(user);

			redirectAttributes.addFlashAttribute("signupForm", signupForm);
			return "redirect:/signupDone";
	}

	@PostMapping(value="/register", params="fix")
	public String postRegisterFix (RedirectAttributes redirectAttributes, @ModelAttribute SignupForm signupForm, Model model) {
		redirectAttributes.addFlashAttribute("signupForm", signupForm);
		return "redirect:/signup";
	}

	@GetMapping("signupDone")
	public String getSignupDone(Model model) {
		if (model.getAttribute("signupForm") != null) {
			return "signupDone";
		} else {
			return "login";
		}
	}
}