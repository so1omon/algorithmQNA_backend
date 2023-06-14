package algorithm_QnA_community.algorithm_QnA_community.api.controller.post;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.MemberBriefDto;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentDetailRes;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentRes;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentsRes;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.TopCommentRes;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
 * 2023/05/30        janguni       keyWords 추가
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDetailRes {

    private Long postId;
    private MemberBriefDto member;

    private String postTitle;

    private String postContent;
    private LocalDateTime createdAt;

    private List<String> postKeyWords;
    private int postLikeCnt;
    private int postDislikeCnt;
    /* TODO isLiked 구현해야함 */
    private Boolean isLiked;
    /* TODO isLiked 구현해야함 */
    private int totalCommentCnt;
    private int totalPageSize;
    private int page;
    private boolean next;
    private boolean prev;
    private int size; // 현재 페이지의 comments의 최상위 댓글 수

    private List<TopCommentRes> commentList;

    public PostDetailRes(Post post, Member member, Boolean isLiked, CommentsRes commentsRes, int totalCommentCnt){
        this.postId = post.getId();
        this.postTitle = post.getTitle();
        this.postContent = post.getContent();
        this.createdAt = post.getCreatedDate();
        this.postKeyWords = convertToListKeyWords(post.getKeyWords());
        this.postLikeCnt = post.getLikeCnt();
        this.postDislikeCnt = post.getDislikeCnt();
        this.member = new MemberBriefDto(member);
        this.isLiked = isLiked;
        this.commentList = commentsRes.getComments();
        /* TODO 추후 페이징 구현 시작 - 생성자 파라미터 정보도 변경하기 */
        this.page = commentsRes.getPage();
        this.totalPageSize = commentsRes.getTotalPageSize();
        this.totalCommentCnt = totalCommentCnt;
        this.next = commentsRes.isNext();
        this.prev = commentsRes.isPrev();
        this.size = commentsRes.getSize();
        /* TODO 추후 페이징 구현 끝*/
    }

    private List<String> convertToListKeyWords(String keyWords){
        List<String> keyWordsList = new ArrayList<>();
        String[] keyWordsArray = keyWords.split("#");
        keyWordsList = Arrays.asList(keyWordsArray);
        if (keyWordsList.get(0).equals("Unknown")) return null;
        return keyWordsList;
    }
}
