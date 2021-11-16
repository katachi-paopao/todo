package katachi.spring.todo.model;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsImpl implements UserDetails {
	private User user;
	private Collection<GrantedAuthority> authorities;

	public UserDetailsImpl(User user) {
		this.user = user;
		this.authorities = new ArrayList<>();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getMail();
	}

	public int getId() {
		return user.getId();
	}

	public String getLastName() {
		return user.getLastName();
	}

	public String getFirstName() {
		return user.getFirstName();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public void setMail(String mail) {
		this.user.setMail(mail);
	}

	public void setLastName(String name) {
		this.user.setLastName(name);
	}

	public void setFirstName(String name) {
		this.user.setFirstName(name);
	}

}
