package daiseek.sbb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;


    //--연관관계---//
    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    // 게시글이 삭제되면, 그에 따른 답변들도 삭제됨
    // 즉, 부모 객체가 삭제되면, 자식도 따라서 삭제되는 원리
    private List<Answer> answerList;

    @ManyToOne
    private SiteUser author;

}
