package ru.webforum.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class RoleManager 
{
	private Set<Role> roleUser;
	private Set<Role> roleAdmin;
	
	{
		roleUser = new HashSet<Role>();
		roleUser.add(new Role("ROLE_USER"));
		
		roleAdmin = new HashSet<Role>();
		roleAdmin.add(new Role("ROLE_ADMIN"));
	}
	
	public Set<Role> getRoleSet(boolean rights)
	{
		if(rights)
		{
			return roleAdmin;
		}
		else
		{
			return roleUser;
		}
	}
}
