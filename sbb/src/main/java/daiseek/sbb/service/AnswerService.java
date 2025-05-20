package daiseek.sbb.service;

import daiseek.sbb.entity.Answer;
import daiseek.sbb.entity.Question;
import daiseek.sbb.entity.SiteUser;
import daiseek.sbb.exception.DataNotFoundException;
import daiseek.sbb.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    public Answer create(Question question, String content, SiteUser author) {
        Answer answer = new Answer();

        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setQuestion(question);
        answer.setAuthor(author); // 작성자도 같이 저장
        this.answerRepository.save(answer);

        return answer;
    }

    public Answer getAnswer(Integer id) {
        Optional<Answer> answer = this.answerRepository.findById(id);

        if (answer.isPresent()) {
            return answer.get();
        } else {
            throw new DataNotFoundException("답변을 찾을 수 없습니다.");
        }
    }

    public void modify(Answer answer, String content) {
        answer.setContent(content);
        answer.setModifyDate(LocalDateTime.now());
        this.answerRepository.save(answer);
    }

    public void delete(Answer answer) {
        this.answerRepository.delete(answer);
    }
}
