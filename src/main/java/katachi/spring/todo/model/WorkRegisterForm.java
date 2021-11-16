package katachi.spring.todo.model;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class WorkRegisterForm {
	@NotBlank(groups=ValidGroup1.class)
	@Length(max=50, groups=ValidGroup2.class)
	private String name;

	@Length(max=1200, groups=ValidGroup1.class)
	private String detail;

	@Pattern(regexp="[yet|done|suspended]")
	private String status;

	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date deadlineDate;

	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date completionDate;

	@NotEmpty(groups=ValidGroup1.class)
	@Size(min=1, groups=ValidGroup2.class)
	private int[] worker;
}
