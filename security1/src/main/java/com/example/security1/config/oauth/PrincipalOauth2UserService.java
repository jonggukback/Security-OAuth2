package com.example.security1.config.oauth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.security1.config.auth.PrincipalDetails;
import com.example.security1.config.oauth.provider.FacebookUserInfo;
import com.example.security1.config.oauth.provider.GoogleUserInfo;
import com.example.security1.config.oauth.provider.NaverUserInfo;
import com.example.security1.config.oauth.provider.OAuth2Userinfo;
import com.example.security1.model.User;
import com.example.security1.repository.UserRepository;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService{
	
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	private UserRepository userRepository;
	
	@Autowired
	public PrincipalOauth2UserService(UserRepository userRepository, @Lazy BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.userRepository = userRepository;
	}
	
	
	// 구글로부터 받은 userRequest 데이터에 대한 후처리되는 함수
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		// 웹서버의 정보 어떤 OAuth로 로그인했는지 알 수 있다 구글인지, 카카오인지
		// 구글 로그인 버튼 클릭-> 구글로그인 창 -> 로그인을 완료 -> code를 리턴 (OAuth-Client 라이브러리) -> AccessToken 요청
		System.out.println("getClientRegistration: "+userRequest.getClientRegistration()); // google
		System.out.println("getAccessToken: "+userRequest.getAccessToken().getTokenValue()); // token 값
		
		// UserReqeust 정보 -> loadUser함수 호출 -> 회원 프로필
		OAuth2User oauth2User = super.loadUser(userRequest);
		System.out.println("getAttributes: "+oauth2User.getAttributes());
		
		OAuth2Userinfo oauth2Userinfo = null;
		if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
			System.out.println("구글 로그인 요청");
			oauth2Userinfo = new GoogleUserInfo(oauth2User.getAttributes());
		}else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
			System.out.println("페이스북 로그인 요청");
			oauth2Userinfo = new FacebookUserInfo(oauth2User.getAttributes());
		}else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
			System.out.println("네이버 로그인 요청");
			oauth2Userinfo = new NaverUserInfo((Map)oauth2User.getAttributes().get("response"));
		}else {
			System.out.println("구글과 페이스북 로그인만 가능합니다.");
		}
		
		// 회원가입
		String provider = oauth2Userinfo.getProvider();
		String providerid = oauth2Userinfo.getProviderId();
		String username = provider+"_"+providerid;
		String password = bCryptPasswordEncoder.encode(providerid);
		String email = oauth2Userinfo.getEmail();
		String role = "ROLE_USER";
		
		User userEntity = userRepository.findByUsername(username);
		
		if (userEntity == null) {
			System.out.println("OAuth 로그인이 처음입니다. 자동 가입됩니다.");
			userEntity = User.builder()
					.username(username)
					.password(password)
					.email(email)
					.role(role)
					.build();
			userRepository.save(userEntity);
		}else {
			System.out.println("이미 가입된 대상입니다. 로그인을 시도합니다.");
		}
		
		return new PrincipalDetails(userEntity, oauth2User.getAttributes());
	}
}
