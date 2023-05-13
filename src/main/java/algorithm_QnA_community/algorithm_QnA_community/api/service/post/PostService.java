package algorithm_QnA_community.algorithm_QnA_community.api.service.post;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.PostCreateReq;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.PostLikeReq;
import algorithm_QnA_community.algorithm_QnA_community.config.exception.CustomException;
import algorithm_QnA_community.algorithm_QnA_community.config.exception.ErrorCode;
import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.like.LikePost;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostCategory;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostType;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.DefStatus;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.Res;
import algorithm_QnA_community.algorithm_QnA_community.repository.LikePostRepository;
import algorithm_QnA_community.algorithm_QnA_community.repository.MemberRepository;
import algorithm_QnA_community.algorithm_QnA_community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.function.Consumer;

import static algorithm_QnA_community.algorithm_QnA_community.domain.member.Role.ROLE_USER;
import static java.lang.Enum.valueOf;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.service.post
 * fileName       : PostService
 * author         : janguni
 * date           : 2023/05/11
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/11        janguni            최초 생성
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    private final LikePostRepository likePostRepository;

    /**
     * 게시물 등록
     */
    @Transactional
    public void writePost(PostCreateReq postCreateReq, Long memberId){

        Member findMember = memberRepository.findById(memberId).get();

        // 일반 사용자가 공지사항 타입을 선택한 경우
        if (findMember.getRole().equals(ROLE_USER) & postCreateReq.getContentType().equals(PostType.NOTICE) ) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "공지사항을 작성할 수 있는 권한이 없습니다.");
        }

        Post post = Post.createPost()
                .member(findMember)
                .title(postCreateReq.getTitle())
                .content(postCreateReq.getContent())
                .category(PostCategory.valueOf(postCreateReq.getCategoryName()))
                .type(PostType.valueOf(postCreateReq.getContentType()))
                .build();

        postRepository.save(post);
    }

    /**
     * 게시물 수정
     */
    @Transactional
    public void updatePost(Long postId,PostCreateReq postCreateReq, Long memberId) {
        Member findMember = memberRepository.findById(memberId).get();

        // 일반 사용자가 공지사항 타입을 선택한 경우
        if (findMember.getRole().equals(ROLE_USER) & postCreateReq.getContentType().equals(PostType.NOTICE) ) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "공지사항을 작성할 수 있는 권한이 없습니다.");
        }

        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시물이 존재하지 않습니다."));

        setIfNotNull(postCreateReq.getTitle(), findPost::updateTitle);
        setIfNotNull(postCreateReq.getContent(), findPost::updateContent);
        setIfNotNull(PostCategory.valueOf(postCreateReq.getCategoryName()), findPost::updateCategory);
        setIfNotNull(PostType.valueOf(postCreateReq.getContentType()),findPost::updateType);
    }

    /**
     * 게시물 삭제
     */
    @Transactional
    public void deletePost(Long postId, Long memberId) {
        Member findMember = memberRepository.findById(memberId).get();

        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시물이 존재하지 않습니다."));

        // 게시물 작성자와 삭제하려는 사용자가 같지 않은 경우
        if (findMember!=findPost.getMember()){
            throw new CustomException(ErrorCode.UNAUTHORIZED, "게시물을 삭제할 권한이 없습니다.");
        }

        postRepository.delete(findPost);
    }

    /**
     * 게시물 추천
     */
    @Transactional
    public Res likePost(Long postId, PostLikeReq postLikeReq, Long memberId) {
        Member findMember = memberRepository.findById(memberId).get();

        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시물이 존재하지 않습니다."));

        Optional<LikePost> findLikePost = likePostRepository.findByPostIdAndMemberId(postId, memberId);

        if (postLikeReq.getCancel()){
            if (!findLikePost.isPresent()) log.info("추천정보가 존재하지 않음");
            else {
                findPost.updateLikeCnt(postLikeReq.getIsLike(), false);
                likePostRepository.delete(findLikePost.get());
            }
        }

        else {
            if (!findLikePost.isPresent()){
                LikePost likePost = LikePost.createLikePost()
                        .member(findMember)
                        .post(findPost)
                        .isLike(postLikeReq.getIsLike())
                        .build();

                likePostRepository.save(likePost);
            }
            else {
                findLikePost.get().updateState(postLikeReq.getIsLike());

            }
        }
        String message = postLikeReq.getIsLike()?"추천" : "비추천";
        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 게시물을 "+message+"했습니다."));
    }


    private <T> void setIfNotNull(T value, Consumer<T> setter){
        if (value != null) {
            setter.accept(value);
        }
    }
}
