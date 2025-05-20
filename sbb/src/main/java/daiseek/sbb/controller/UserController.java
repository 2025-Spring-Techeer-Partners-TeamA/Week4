package daiseek.sbb.controller;

import daiseek.sbb.dto.UserCreateForm;
import daiseek.sbb.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // 회원가입 요청시, signup_form 템플릿을 반환
    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    // 회원가입시 유저 생성 로직
    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        // BindingResult: 왼쪽에 선언된 매개변수가 에러가 발생하면 처리해준다.
        // UserCreateForm 객체에서 에러 발생시 다시 signup_form 반환
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        // 비밀번호 두 개가 다를 경우 처리 로직
        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            // bindingResult.rejectValue(필드, 오류코드, 오류메시지)
            // 비밀번호가 일치하지 않을경우, 오류 코드와 메시지를 지정해준다.
            // 이후 signup_form을 반환한다.
            bindingResult.rejectValue("password", "passwordInCorrect", "비밀번호가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(), userCreateForm.getEmail(), userCreateForm.getPassword1());

        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        } catch (Exception e) {
            e.printStackTrace();;
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }


    // 로그인
    @GetMapping("/login")
    public String login() {
        return "login_form";
    }
    // @PostMapping 방식의 메서드는 스프링 시큐리티가 알아서 처리하므로 직접 구현할 필요 없음.

}
