package daiseek.sbb.service;

import daiseek.sbb.entity.Question;
import daiseek.sbb.entity.SiteUser;
import daiseek.sbb.exception.DataNotFoundException;
import daiseek.sbb.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.config.ConfigDataNotFoundException;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionService {

    // 생성자 주입
    private final QuestionRepository questionRepository;

    // Page 기능으로 대체
//    public List<Question> getList() {
//        return this.questionRepository.findAll();
//    }

    public Page<Question> getList(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
//        createDate를 역순으로 나열
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.questionRepository.findAll(pageable);
    }

    public Question getQuestion(Integer id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public void create(String subject, String content, SiteUser siteUser) {
        Question question = new Question();
        question.setSubject(subject);
        question.setContent(content);
        question.setCreateDate(LocalDateTime.now());
        question.setAuthor(siteUser); // 질문 작성시 작성자도 게시
        this.questionRepository.save(question);
    }

    public void modify(Question question, String subject, String content) {
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }


    public void delete(Question question) {
        this.questionRepository.delete(question);
    }
}
