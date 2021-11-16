package katachi.spring.todo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="user_master")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
	@Id
	@Column(length = 11)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false)
	private String mail;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String lastName;

	@Column(nullable = false)
	private String firstName;

	/*
	@ManyToMany(
			mappedBy="workerSet"
		)
	private Set<Work> workSet;
	*/
}
