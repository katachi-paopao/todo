package katachi.spring.todo.model;

import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class WorkEdit{
	@NotNull(groups=ValidGroup1.class)
	private Integer workId;

	@Length(max=1200, groups=ValidGroup2.class)
	private String detail;

	@NotNull(groups=ValidGroup1.class)
	private int status;

	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date deadlineDate;

	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date completionDate;

	@NotEmpty(groups=ValidGroup1.class)
	private int[] worker;
}
