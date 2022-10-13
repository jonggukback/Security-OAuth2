package com.example.security1.config.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.example.security1.model.User;

import lombok.Data;

/* 
 * 시큐리티가 /login 주소 요청이 오면 낚아채서 (인터셉터) 로그인을 진행 시킨다. 
 * 로그인 진행 완료가 되면 시큐리티 session을 만들어줍니다. (Security ContextHolder)
 * 오브젝트 타입 => Authentication 타입 객체
 * Authentication 안에 User 정보가 있어야 됩니다.
 * User 오브젝트 타입 => UserDetails 타입 객체
 * 
 * Security Session => Authentication => UserDetails(PrincipalDetails)
 */

@Data
public class PrincipalDetails implements UserDetails, OAuth2User{

	private User user; // 콤포지션
	private Map<String,Object> attributes;
	
	// 일반 로그인
	public PrincipalDetails(User user) {
		this.user = user;
	}
	
	// OAuth 로그인
	public PrincipalDetails(User user, Map<String,Object> attributes) {
		this.user = user;
		this.attributes = attributes;
	}
	
	// 해당 User의 권한을 리턴하는곳
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collect = new ArrayList<>();
		collect.add(()->{
			return user.getRole();
		});
		return collect;
	}

	// 해당 User의 Password를 리턴하는곳
	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return user.getPassword();
	}

	// 해당 User의 Username을 리턴하는곳
	@Override
	public String getUsername() {
		return user.getUsername();
	}

	// 계정의 만료 여부
	@Override
	public boolean isAccountNonExpired() {
		return true; // 아니요
	}

	// 계정이 잠겨있는지 여부
	@Override
	public boolean isAccountNonLocked() {
		return true; // 아니요
	}

	// 비밀번호가 오래됐는지 여부
	@Override
	public boolean isCredentialsNonExpired() {
		return true; // 아니요
	}

	// 계정이 비활성화 되어있나
	@Override
	public boolean isEnabled() {
		// 비활성화를 걸려면?
		// 현재시간 - 로긴시간 => 1년초과시 return false;
		return true; // 아니요
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public String getName() {
		return (String) attributes.get("sub");
	}

}
