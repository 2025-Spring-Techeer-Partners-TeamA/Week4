package daiseek.sbb.controller;

import daiseek.sbb.dto.AnswerForm;
import daiseek.sbb.dto.QuestionForm;
import daiseek.sbb.entity.Question;
import daiseek.sbb.entity.SiteUser;
import daiseek.sbb.repository.QuestionRepository;
import daiseek.sbb.service.QuestionService;
import daiseek.sbb.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

// 프리픽스가 모두 /question로 시작하므로, 컨트롤러 클래스 위에 @RequestMapping을 붙일 수 있다.
@RequestMapping(value = "/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

    private final QuestionRepository questionRepository;
    private final QuestionService questionService;
    private final UserService userService;


    @GetMapping("/list")
//    @ResponseBody
//    public String list(Model model) {

        // 1. 서비스 없이 레포지토리에서 데이터를 주고 받을때
//        List<Question> questions = this.questionService.getQuestions();

        // 2. 서비스에 레포지토리를 주입받아서 사용

        // 기존의 메서드 대신 page 기능을 구현한 메서드 사용
//        List<Question> questionList = this.questionService.getList();
//        model.addAttribute("questionList", questionList);
//        return "question_list";
//    }

    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        Page<Question> paging = this.questionService.getList(page);
        model.addAttribute("paging", paging);
        return "question_list";
    }


    @GetMapping(value = "/detail/{id}") // URL 매핑만 하면, value 생략 가능
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
        Question question = this.questionService.getQuestion(id);
        model.addAttribute("question", question);
        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(Model model) {
        model.addAttribute("questionForm", new QuestionForm());
        // 빈 객체를 모델에 추가
        return "question_form"; // 'http://'을 제거하고 절대 경로로 수정

    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser);
        // TODO : 질문 저장
        return "redirect:/question/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModifyForm(@PathVariable("id") Integer id, Model model, Principal principal) {
        Question question = this.questionService.getQuestion(id);

        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }

        QuestionForm questionForm = new QuestionForm();
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());

        model.addAttribute("questionForm", questionForm);
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult,
                                 @PathVariable("id") Integer id, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }

        Question question = this.questionService.getQuestion(id);

        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

//        questionForm.setSubject(question.getSubject());
//        questionForm.setContent(question.getContent());
//        return "question_form"; // 기존의 질문 등록 템플릿을 재활용

        // 비즈니스 로직을 서비스가 담당하도록 위임
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/question/detail/%s", id); // 수정완료시 질문상세화면으로 리다이렉트

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/";
    }

}

// Model : 자바 클래스와 템플릿 간의 연결고리 역할
// Model 객체는 따로 생성할 필요 없이 메서드에 매개변수로 넣어주면,
// 스프링 부트가 자동으로 모델 객체를 생성해준다.