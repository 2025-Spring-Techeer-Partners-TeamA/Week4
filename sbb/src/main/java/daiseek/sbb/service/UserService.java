package daiseek.sbb.service;

import daiseek.sbb.entity.SiteUser;
import daiseek.sbb.exception.DataNotFoundException;
import daiseek.sbb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    // SecurityConfig에서 생성한 Bcrypt 객체 사용
    private final PasswordEncoder passwordEncoder;

    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        // 비크림트 : 비밀번호 암호화 클래스
        // 해시 함수로 비밀번호를 해시화하여 안전하게 저장하고 검증시 사용
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        user.setPassword(passwordEncoder.encode(password));
        // 싱글톤 객체로 대체
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
        return user;
    }

    // 로그인한 사용자명으로 조회
    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByusername(username);

        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("사용자를 찾을 수 없습니다.");
        }
    }

}
