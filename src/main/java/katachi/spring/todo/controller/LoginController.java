package katachi.spring.todo.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {
	@GetMapping({"", "/login"})
	public String getHome() {
		return "login";
	}

	@PostMapping("/login")
	public String postLogin(HttpServletRequest request) {
		return "login";
	}
}
