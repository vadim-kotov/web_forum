package ru.webforum.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ru.webforum.service.ApplicationUserDetailsService;

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
			.csrf().and().cors().disable()
			.authorizeRequests()
				.antMatchers("/users/registration.do").anonymous()
				.antMatchers("/users/login.do").anonymous()
				.antMatchers("/forum/*/new_section.do").hasRole("ADMIN")
				.antMatchers("/forum/*/new_topic.do").authenticated()
				.antMatchers("/forum/*/topic_*/new_message.do").authenticated()
				.antMatchers("/users/account.do").authenticated()
			.anyRequest().permitAll()
			.and()
			.formLogin()
				.loginPage("/users/login.do")
				.defaultSuccessUrl("/forum.do")
			.and()
				.logout()
				.logoutUrl("/users/logout.do")
				.logoutSuccessUrl("/forum.do")
				.permitAll();
	}
	
	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception
	{
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
	}
}
