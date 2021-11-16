package katachi.spring.todo.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import katachi.spring.todo.model.User;
import katachi.spring.todo.model.UserIdAndName;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	User findOneById(int id);
	User findOneByMail(String mail);

	@Query(value="SELECT new katachi.spring.todo.model.UserIdAndName(u.id, u.lastName, u.firstName) FROM User u")
	ArrayList<UserIdAndName> findAllUserName();
}
