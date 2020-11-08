package ru.webforum.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class ApplicationWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter 
{
	@Autowired
	ApplicationUserDetailsService userDetailsService;
	
	@Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	/*
	@Autowired
	public ApplicationWebSecurityConfigurerAdapter(ApplicationUserDetailsService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder)
	{
		this.userDetailsService = userDetailsService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	*/
	
	@Override
	protected void configure(final HttpSecurity httpSecurity) throws Exception
	{
		httpSecurity
			.csrf().disable()
			.authorizeRequests()
				.antMatchers("/web_forum/users/registration.do").anonymous()
			.anyRequest().authenticated()
			.and()
			.formLogin()
				.loginPage("/web_forum/users/login.do")
				.defaultSuccessUrl("/forum.do");
	}
	
	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception
	{
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
	}
}
