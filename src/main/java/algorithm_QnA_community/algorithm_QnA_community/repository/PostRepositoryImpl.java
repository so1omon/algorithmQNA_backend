package algorithm_QnA_community.algorithm_QnA_community.repository;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.PostSearchDto;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.PostSimpleDto;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.QPostSimpleDto;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static algorithm_QnA_community.algorithm_QnA_community.domain.comment.QComment.*;
import static algorithm_QnA_community.algorithm_QnA_community.domain.member.QMember.*;
import static algorithm_QnA_community.algorithm_QnA_community.domain.post.QPost.post;
import static org.springframework.util.StringUtils.isEmpty;
/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.repository
 * fileName       : PostRepositoryImpl
 * author         : janguni
 * date           : 2023/05/31
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/31        janguni           최초 생성
 * 2023/06/01        janguni           PostSearchDto 변경 후 코드 수정 (Enum비교, keyWordsContain 변경)
 * 2023/06/17        janguni           likeCnt, di
 */
@RequiredArgsConstructor
@Repository
@Slf4j
public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostSimpleDto> findPostsOrderByCreatedDateDesc(PostSearchDto postSearchDto, Pageable pageable) {
        QueryResults<PostSimpleDto> results = queryFactory
                .select(new QPostSimpleDto(
                        post.id,
                        post.title,
                        member.id,
                        member.name,
                        member.profileImgUrl,
                        post.createdDate,
                        post.views,
                        post.comments.size())).distinct()
                .from(post)
                .leftJoin(post.member, member)
                .leftJoin(comment).on(post.id.eq(comment.post.id))
                .where(post.postCategory.eq(postSearchDto.getPostCategory()),
                        post.type.eq(postSearchDto.getPostType()),
                        allCond(postSearchDto.getKeyWordsCond(), postSearchDto.getMemberNameCond(), postSearchDto.getTitleCond(), postSearchDto.getHasCommentCond(), postSearchDto.getIsAcceptedCommentCond())
                )
                .orderBy(post.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<PostSimpleDto> content = results.getResults();
        long total = results.getTotal();


        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<PostSimpleDto> findPostsOrderByCreatedDateAsc(PostSearchDto postSearchDto, Pageable pageable) {
        QueryResults<PostSimpleDto> results = queryFactory
                .select(new QPostSimpleDto(
                        post.id,
                        post.title,
                        member.id,
                        member.name,
                        member.profileImgUrl,
                        post.createdDate,
                        post.views,
                        post.comments.size())).distinct()
                .from(post)
                .leftJoin(post.member, member)
                .leftJoin(comment).on(post.id.eq(comment.post.id))
                .where(post.postCategory.eq(postSearchDto.getPostCategory()),
                        post.type.eq(postSearchDto.getPostType()),
                        allCond(postSearchDto.getKeyWordsCond(), postSearchDto.getMemberNameCond(), postSearchDto.getTitleCond(), postSearchDto.getHasCommentCond(), postSearchDto.getIsAcceptedCommentCond())
                )
                .orderBy(post.createdDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<PostSimpleDto> content = results.getResults();
        long total = results.getTotal();


        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<PostSimpleDto> findPostsOrderByLikeDesc(PostSearchDto postSearchDto, Pageable pageable) {
        QueryResults<PostSimpleDto> results = queryFactory
                .select(new QPostSimpleDto(
                        post.id,
                        post.title,
                        member.id,
                        member.name,
                        member.profileImgUrl,
                        post.createdDate,
                        post.views,
                        post.comments.size(),
                        post.likeCnt,
                        post.dislikeCnt
                        )).distinct()
                .from(post)
                .leftJoin(post.member, member)
                .leftJoin(comment).on(post.id.eq(comment.post.id))
                .where(post.postCategory.eq(postSearchDto.getPostCategory()),
                        post.type.eq(postSearchDto.getPostType()),
                        allCond(postSearchDto.getKeyWordsCond(), postSearchDto.getMemberNameCond(), postSearchDto.getTitleCond(), postSearchDto.getHasCommentCond(), postSearchDto.getIsAcceptedCommentCond())
                )
                .orderBy(post.likeCnt.subtract(post.dislikeCnt).desc(), post.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<PostSimpleDto> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<PostSimpleDto> findPostsOrderByLikeAsc(PostSearchDto postSearchDto, Pageable pageable) {

        QueryResults<PostSimpleDto> results = queryFactory
                .select(new QPostSimpleDto(
                        post.id,
                        post.title,
                        member.id,
                        member.name,
                        member.profileImgUrl,
                        post.createdDate,
                        post.views,
                        post.comments.size(),
                        post.likeCnt,
                        post.dislikeCnt
                        )).distinct()
                .from(post)
                .leftJoin(post.member, member)
                .leftJoin(comment).on(post.id.eq(comment.post.id))
                .where(post.postCategory.eq(postSearchDto.getPostCategory()),
                        post.type.eq(postSearchDto.getPostType()),
                        allCond(postSearchDto.getKeyWordsCond(), postSearchDto.getMemberNameCond(), postSearchDto.getTitleCond(), postSearchDto.getHasCommentCond(), postSearchDto.getIsAcceptedCommentCond())
                )
                .orderBy(post.likeCnt.subtract(post.dislikeCnt).asc(), post.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<PostSimpleDto> content = results.getResults();
        long total = results.getTotal();


        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<PostSimpleDto> findPostsOrderByCommentSizeDesc(PostSearchDto postSearchDto, Pageable pageable) {
        QueryResults<PostSimpleDto> results = queryFactory
                .select(new QPostSimpleDto(
                        post.id,
                        post.title,
                        member.id,
                        member.name,
                        member.profileImgUrl,
                        post.createdDate,
                        post.views,
                        commentSizeByPost())).distinct()
                .from(post)
                .leftJoin(post.member, member)
                .leftJoin(comment).on(post.id.eq(comment.post.id))
                .where(post.postCategory.eq(postSearchDto.getPostCategory()),
                        post.type.eq(postSearchDto.getPostType()),
                        allCond(postSearchDto.getKeyWordsCond(), postSearchDto.getMemberNameCond(), postSearchDto.getTitleCond(), postSearchDto.getHasCommentCond(), postSearchDto.getIsAcceptedCommentCond())
                )
                .groupBy(post.id)
                .orderBy(commentSizeByPost().desc(), post.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<PostSimpleDto> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<PostSimpleDto> findPostsOrderByCommentSizeAsc(PostSearchDto postSearchDto, Pageable pageable) {
        QueryResults<PostSimpleDto> results = queryFactory
                .select(new QPostSimpleDto(
                        post.id,
                        post.title,
                        member.id,
                        member.name,
                        member.profileImgUrl,
                        post.createdDate,
                        post.views,
                        post.comments.size())).distinct()
                .from(post)
                .leftJoin(post.member, member)
                .leftJoin(comment).on(post.id.eq(comment.post.id))
                .where(post.postCategory.eq(postSearchDto.getPostCategory()),
                        post.type.eq(postSearchDto.getPostType()),
                        allCond(postSearchDto.getKeyWordsCond(), postSearchDto.getMemberNameCond(), postSearchDto.getTitleCond(), postSearchDto.getHasCommentCond(), postSearchDto.getIsAcceptedCommentCond())
                )
                .groupBy(post)
                .orderBy(post.comments.size().asc(), post.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<PostSimpleDto> content = results.getResults();
        long total = results.getTotal();


        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<PostSimpleDto> findPostsOrderByViewAsc(PostSearchDto postSearchDto, Pageable pageable) {
        QueryResults<PostSimpleDto> results = queryFactory
                .select(new QPostSimpleDto(
                        post.id,
                        post.title,
                        member.id,
                        member.name,
                        member.profileImgUrl,
                        post.createdDate,
                        post.views,
                        post.comments.size())).distinct()
                .from(post)
                .leftJoin(post.member, member)
                .leftJoin(comment).on(post.id.eq(comment.post.id))
                .where(post.postCategory.eq(postSearchDto.getPostCategory()),
                        post.type.eq(postSearchDto.getPostType()),
                        allCond(postSearchDto.getKeyWordsCond(), postSearchDto.getMemberNameCond(), postSearchDto.getTitleCond(), postSearchDto.getHasCommentCond(), postSearchDto.getIsAcceptedCommentCond())
                )
                .orderBy(post.views.asc(), post.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<PostSimpleDto> content = results.getResults();
        long total = results.getTotal();


        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<PostSimpleDto> findPostsOrderByViewDesc(PostSearchDto postSearchDto, Pageable pageable) {
        QueryResults<PostSimpleDto> results = queryFactory
                .select(new QPostSimpleDto(
                        post.id,
                        post.title,
                        member.id,
                        member.name,
                        member.profileImgUrl,
                        post.createdDate,
                        post.views,
                        post.comments.size())).distinct()
                .from(post)
                .leftJoin(post.member, member)
                .leftJoin(comment).on(post.id.eq(comment.post.id))
                .where(post.postCategory.eq(postSearchDto.getPostCategory()),
                        post.type.eq(postSearchDto.getPostType()),
                        allCond(postSearchDto.getKeyWordsCond(), postSearchDto.getMemberNameCond(), postSearchDto.getTitleCond(), postSearchDto.getHasCommentCond(), postSearchDto.getIsAcceptedCommentCond())
                )
                .orderBy(post.views.desc(), post.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<PostSimpleDto> content = results.getResults();
        long total = results.getTotal();


        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<PostSimpleDto> findPostsOrderByPopularDesc(PostSearchDto postSearchDto, Pageable pageable) {

        NumberExpression<Float> popularNumber = getPopularNumber();
        QueryResults<PostSimpleDto> results = queryFactory
                .select(new QPostSimpleDto(
                        post.id,
                        post.title,
                        member.id,
                        member.name,
                        member.profileImgUrl,
                        post.createdDate,
                        post.views,
                        post.comments.size(),
                        popularNumber)).distinct()
                .from(post)
                .leftJoin(post.member, member)
                .leftJoin(comment).on(post.id.eq(comment.post.id))
                .where(post.postCategory.eq(postSearchDto.getPostCategory()),
                        post.type.eq(postSearchDto.getPostType()),
                        allCond(postSearchDto.getKeyWordsCond(), postSearchDto.getMemberNameCond(), postSearchDto.getTitleCond(), postSearchDto.getHasCommentCond(), postSearchDto.getIsAcceptedCommentCond())
                )
                .groupBy(post)
                .orderBy(popularNumber.desc(), post.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<PostSimpleDto> contents = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(contents, pageable, total);
    }

    private NumberExpression<Float> getPopularNumber() {
        NumberExpression<Integer> likeCnt = post.likeCnt;
        NumberExpression<Integer> dislikeCnt = post.dislikeCnt;

        NumberExpression<Float> popularNumber = Expressions.numberTemplate(Float.class,
                "({0} * 50) + (CASE WHEN ({1} = 0 AND {2} = 0) THEN 0 ELSE (({1} * {1} / ({1} + {2})) * 30) END) + ({3} * 20)",
                post.views, likeCnt, dislikeCnt, post.comments.size());
        return popularNumber;
    }




    // 필터 조건 조합
    private BooleanExpression allCond(String keyWordsCond, String memberNameCond, String titleCond, Boolean hasCommentCond, Boolean isAcceptedCommentCond) {
        BooleanExpression expression = null;

        if (isAcceptedCommentCond != null) {
            expression = isAcceptedCommend(isAcceptedCommentCond);
        }

        if (hasCommentCond != null) {
            expression = addExpression(expression, hasComments(hasCommentCond));
        }

        if (!StringUtils.isEmpty(titleCond)) {
            expression = addExpression(expression, postTitleContain(titleCond));
        }

        if (!StringUtils.isEmpty(memberNameCond)) {
            expression = addExpression(expression, memberNameContain(memberNameCond));
        }

        if (!StringUtils.isEmpty(keyWordsCond)) {
            expression = addExpression(expression, keyWordsContain(keyWordsCond));
        }

        return expression;
    }

    private BooleanExpression addExpression(BooleanExpression base, BooleanExpression additional) {
        if (base == null) {
            return additional;
        }
        if (additional != null) {
            return base.and(additional);
        }
        return base;
    }

    private BooleanExpression isAcceptedCommend(Boolean isAcceptedCommentCond) {
        log.info("isAcceppedCommentCond={}", isAcceptedCommentCond);
        if (isAcceptedCommentCond == null) return null;
        else if (isAcceptedCommentCond){
            return comment.isPinned.isTrue();
        } else return comment.isPinned.isFalse().or(comment.isNull());
    }

    private BooleanExpression hasComments(Boolean hasCommentCond) {
        if (hasCommentCond==null){
            return null;
        } else if (hasCommentCond){
            return post.comments.isNotEmpty();
        } else {
            return post.comments.isEmpty();
        }
    }

    private NumberExpression<Integer> commentSizeByPost(){ return post.comments.size();}

    private BooleanExpression postTitleContain(String titleCond) { return isEmpty(titleCond) ? null : post.title.contains(titleCond);}

    private BooleanExpression memberNameContain(String memberNameCond) { return isEmpty(memberNameCond) ? null : member.name.contains(memberNameCond); }

    private BooleanExpression keyWordsContain(String keyWordsCond) {

        if (keyWordsCond.isEmpty())
            return null;
        else {
            List<String> keyWordsCondList = Arrays.stream(keyWordsCond.split("#")).collect(Collectors.toList());
            return keyWordsCondList.stream().map(post.keyWords::contains).reduce(BooleanExpression::or).orElse(null);
        }

    }
}
