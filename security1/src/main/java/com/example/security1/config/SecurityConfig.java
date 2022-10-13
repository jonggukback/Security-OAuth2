package com.example.security1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.security1.config.oauth.PrincipalOauth2UserService;


@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 된다.
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;
	
	@Bean
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests()
			.antMatchers("/user/**").authenticated()
			.antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
			.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
			.anyRequest().permitAll()
			.and()
			.formLogin()
			.loginPage("/loginForm")
			.loginProcessingUrl("/login")// login 주소가 호출이되면 시큐리티가 낚아채서 대신 로그인 진행
			.defaultSuccessUrl("/") // 로그인이 성공하면 이동할 디폴트 페이지 설정
			.and()
			.oauth2Login()
			.loginPage("/loginForm") // 구글 로그인 이후 후처리 필요
			.userInfoEndpoint()
			.userService(principalOauth2UserService);
		
		/*
		 * oauth2 로그인 이후처리 내용
		 * 1. 코드받기(인증) 2. 엑세스토큰(권한) 3. 사용자프로필 정보를 가져오고 그 정보를 토대로 회원가입을 자동으로 진행
		 * Tip. 코드X, (엑세스토큰+사용자프로필정보 O)
		 */
	}
}
