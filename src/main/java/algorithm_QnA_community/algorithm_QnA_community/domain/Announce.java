package algorithm_QnA_community.algorithm_QnA_community.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
@Table
public class Announce extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 관리자(회원) 매핑 (다대일)

    @Column
    private String title;

    @Column
    private String contents;


    @Builder
    public Announce(String title,String contents){
        this.title = title;
        this.contents = contents;
    }

}
