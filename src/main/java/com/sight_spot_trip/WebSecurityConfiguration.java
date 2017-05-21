package com.sight_spot_trip;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	// http://stackoverflow.com/questions/37671125/how-to-configure-spring-security-to-allow-swagger-url-to-be-accessed-without-aut
	// @Override
	// public void configure(WebSecurity web) throws Exception {
	// web.ignoring().antMatchers("/**", "/v2/api-docs", "/configuration/ui",
	// "/swagger-resources", "/configuration/security", "/swagger-ui.html",
	// "/swagger-ui-standalone.html", "/webjars/**");
	// }

	@Autowired
	public void configure(AuthenticationManagerBuilder auth) {
		try {
			auth.inMemoryAuthentication()
			.withUser("user").password("user").roles("USER")
			.and()
			.withUser("admin").password("admin").roles("ADMIN", "USER");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/api/**", "/swagger-ui.html", "/swagger-ui-standalone.html", "/v2/api-docs"); // #3
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/signup", "/about").permitAll()
				.antMatchers("/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated()
				.and()
				.formLogin()
				.successHandler(authenticationSuccessHandler())
				.permitAll();
	}
			


	private SavedRequestAwareAuthenticationSuccessHandler authenticationSuccessHandler() {
		SavedRequestAwareAuthenticationSuccessHandler auth = new RoleBasedAuthenticationSuccessHandler();
		return auth;
	}

	static class RoleBasedAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler
			implements AuthenticationSuccessHandler {
		@Override
		public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
				Authentication authentication) throws ServletException, IOException {
			String authorities = authentication.getAuthorities().stream()
					.map(i -> i.toString())
					.collect(Collectors.joining("|"));
			response.addCookie(new Cookie("user_role", authorities));
			super.onAuthenticationSuccess(request, response, authentication);
		}
	}

}