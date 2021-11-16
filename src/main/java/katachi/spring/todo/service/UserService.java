package katachi.spring.todo.service;


import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import katachi.spring.todo.model.User;
import katachi.spring.todo.model.UserIdAndName;
import katachi.spring.todo.repository.UserRepository;

@Transactional
@Service
public class UserService {
	@Autowired
	UserRepository userRepository;

	public ArrayList<UserIdAndName> findAllUserName(){
		return userRepository.findAllUserName();
	}

	public User findById(int id) {
		return userRepository.findOneById(id);
	};

	public boolean duplicateCheckForMail(String mail) {
		User user = userRepository.findOneByMail(mail);
		if (user != null) return true;
		else return false;
	}

	public boolean register(User user) {
		if (userRepository.saveAndFlush(user) != null) return true;
		return false;
	}
}
