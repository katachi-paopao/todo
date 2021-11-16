package katachi.spring.todo.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import lombok.Data;

@Entity
@Table(name="work")
@Data
public class Work {
	@Id
	@Column(length = 11)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false)
	private int status;

	@Column(nullable = false)
	private String name;

	private String detail;
	private Date deadlineDate;
	private Date completionDate;

	@Version
	private int version;

	@Transient
	private List<UserIdAndName> workerList;
}
