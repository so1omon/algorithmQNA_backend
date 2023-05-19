package algorithm_QnA_community.algorithm_QnA_community.api.controller.post;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.MemberBriefDto;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentDetailRes;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.post
 * fileName       : PostDetailRes
 * author         : janguni
 * date           : 2023/05/14
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/14        janguni       최초 생성
 * 2023/05/19        solmin        필드명 및 생성자 구조 변경
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDetailRes {

    private Long postId;
    private MemberBriefDto member;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private int likeCnt;
    private int dislikeCnt;
    /* TODO isLiked 구현해야함 */
    private Boolean isLiked;
    /* TODO isLiked 구현해야함 */
    private int totalCommentCnt;
    private int totalPageSize;
    private int page;
    private boolean next;
    private boolean prev;
    private int size;
    private List<CommentDetailRes> comments;

    public PostDetailRes(Post post, Member member,
                         int totalCommentSize, int page, int totalPageSize, boolean next, boolean prev,
                         List<CommentDetailRes> comments){
        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedDate();
        this.likeCnt = post.getLikeCnt();
        this.dislikeCnt = post.getDislikeCnt();
        this.member = new MemberBriefDto(member);
        /* TODO 추후 페이징 구현 시작 - 생성자 파라미터 정보도 변경하기 */
        this.page = page;
        this.totalPageSize = totalPageSize;
        this.totalCommentCnt = totalCommentSize;
        this.next = next;
        this.prev = prev;
        this.size = comments.size();
        /* TODO 추후 페이징 구현 끝*/

    }
}
