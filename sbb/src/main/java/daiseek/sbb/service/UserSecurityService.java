package daiseek.sbb.service;

import daiseek.sbb.entity.SiteUser;
import daiseek.sbb.entity.UserRole;
import daiseek.sbb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserSecurityService implements UserDetailsService {

    private final UserRepository userRepository;

    // 사용자명으로 SiteUser 객체 조회 메서드
    // 만일 사용자명에 해당하는 데이터가 없을때 UsernameNotFoundException 발생
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<SiteUser> _siteUser = this.userRepository.findByusername(username);

        if (_siteUser.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        SiteUser siteUser = _siteUser.get();

        List<GrantedAuthority> authorities = new ArrayList<>();

        if ("admin".equals(username)) {
            // 사용자명이 admin인 경우 ADMIN 권한 부여
            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
        } else { // 아니면, USER 권한 부여
            authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
        }
        // 최종적으로 사용자의 이름과 비밀번호, 권한을 반환
        return new User(siteUser.getUsername(), siteUser.getPassword(), authorities);

        // Note. 스프링 시큐리티에는 loadUserByUsername 메서드에 의해 반환된 User 객체의 비밀번호가
        // 입력된 비밀번호와 같은지 검사하는 기능이 존재한다.
    }

}
