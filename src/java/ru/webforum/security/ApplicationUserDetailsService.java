package ru.webforum.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ru.webforum.model.User;
import ru.webforum.model.UserManager;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {
	private UserManager userManager;
	
	@Autowired
	public ApplicationUserDetailsService(UserManager userManager) 
	{
		this.userManager = userManager;
	}

	@Override
	public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException 
	{
		User user = userManager.getUser(login);
		if(user == null)
		{
			throw new UsernameNotFoundException("User not found");
		}
		return user;
	}

}
