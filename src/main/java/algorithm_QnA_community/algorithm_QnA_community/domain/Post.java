package algorithm_QnA_community.algorithm_QnA_community.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

//@Entity
@Getter
@AllArgsConstructor
public class Post {

    @Id @GeneratedValue
    private Long postId;

    private String title;

    private String content;

    private Date createdAt;

    private int likeCount;

    private int dislikeCount;
}
