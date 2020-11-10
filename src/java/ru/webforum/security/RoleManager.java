package ru.webforum.security;

import java.util.HashSet;
import java.util.Set;

//import org.springframework.stereotype.Component;

//@Component
public class RoleManager 
{
	static private Set<Role> roleUser;
	static private Set<Role> roleAdmin;
	
	{
		roleUser = new HashSet<Role>();
		roleUser.add(new Role("ROLE_USER"));
		
		roleAdmin = new HashSet<Role>();
		roleAdmin.add(new Role("ROLE_ADMIN"));
		
		for(Role r : roleUser)
		{
			System.out.println(r.getAuthority());
		}
	}
	
	static public Set<Role> getRoleSet(boolean rights)
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
