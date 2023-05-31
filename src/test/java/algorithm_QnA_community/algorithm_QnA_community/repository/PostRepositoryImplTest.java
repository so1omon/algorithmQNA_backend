package algorithm_QnA_community.algorithm_QnA_community.repository;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.PostSearchDto;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.PostSimpleDto;
import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Role;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostCategory;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.validation.constraints.Size;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@SpringBootTest
@Transactional
@Slf4j
class PostRepositoryImplTest {

    @Autowired
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    EntityManager em;

    @Autowired
    PostRepositoryImpl postImplRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CommentRepository commentRepository;

    @BeforeEach
    public void memberSave() {
        Member member = Member.createMember()
                .name("uni2")
                .email("uni12345@gmail.com")
                .role(Role.ROLE_USER)
                .profileImgUrl("profile")
                .build();
        memberRepository.save(member);

        for (int i = 0; i < 10; i++) {
            Post post = Post.createPost()
                    .member(member)
                    .title("DP,QNA게시물 제목" + i)
                    .content("게시물 내용"+i)
                    .postCategory(PostCategory.DP)
                    .type(PostType.QNA)
                    .keyWords(new ArrayList<>(Collections.singleton(String.valueOf(i))))
                    .build();
            postRepository.save(post);
            if (i==5){
                for (int j = 0; j < 3; j++) {
                    Comment comment = Comment.createComment()
                            .member(member)
                            .post(post)
                            .content("댓글 내용")
                            .build();
                    commentRepository.save(comment);
                }
            }
        }
    }

    @Test
    void queryBasicTest(){
        long startTime = System.currentTimeMillis();
        List<String> keyWordsCond = new ArrayList<>();
        PostSearchDto postSearchDto = new PostSearchDto("DP", "QNA", "LATESTASC", 0, true, keyWordsCond, "게시", null, true);
        Page<PostSimpleDto> results = postImplRepository.findPostsOrderByCreatedDateDesc(postSearchDto, PageRequest.of(0, 20));
        long stopTime = System.currentTimeMillis();
        List<PostSimpleDto> result = results.getContent();
        for (PostSimpleDto p: result) {
            String title = p.getTitle();
            log.info("result title = {}", title);
        }
    }




}