package katachi.spring.todo.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class SignupForm {
	@NotBlank(groups=ValidGroup1.class)
	@Email(groups=ValidGroup2.class)
	@Length(max=100, groups=ValidGroup3.class)
	private String mail;

	@NotBlank(groups=ValidGroup1.class)
	@Pattern(regexp="^[a-zA-Z0-9]+$", groups=ValidGroup2.class)
	@Length(min=8, max=64, groups=ValidGroup3.class)
	private String password;

	private String confirmPassword;

	@NotBlank(groups=ValidGroup1.class)
	@Length(max=30, groups=ValidGroup2.class)
	private String lastName;

	@NotBlank(groups=ValidGroup1.class)
	@Length(max=30, groups=ValidGroup2.class)
	private String firstName;
}
