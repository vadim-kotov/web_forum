package ru.webforum.security;

import org.springframework.security.core.GrantedAuthority;

public class Role implements GrantedAuthority 
{
	private String authority;
	
	Role(String authority) { this.authority = authority; }

	@Override
	public String getAuthority()
	{
		return authority;
	}

}
