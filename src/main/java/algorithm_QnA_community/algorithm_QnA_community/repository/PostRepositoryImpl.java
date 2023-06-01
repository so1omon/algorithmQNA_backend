package algorithm_QnA_community.algorithm_QnA_community.repository;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.PostSearchDto;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.PostSimpleDto;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.QPostSimpleDto;
import algorithm_QnA_community.algorithm_QnA_community.domain.comment.QComment;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

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
                .where(post.postCategory.stringValue().eq(postSearchDto.getPostCategory()),
                        post.type.stringValue().eq(postSearchDto.getPostType()),
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
                .where(post.postCategory.stringValue().eq(postSearchDto.getPostCategory()),
                        post.type.stringValue().eq(postSearchDto.getPostType()),
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
                        post.comments.size())).distinct()
                .from(post)
                .leftJoin(post.member, member)
                .leftJoin(comment).on(post.id.eq(comment.post.id))
                .where(post.postCategory.stringValue().eq(postSearchDto.getPostCategory()),
                        post.type.stringValue().eq(postSearchDto.getPostType()),
                        allCond(postSearchDto.getKeyWordsCond(), postSearchDto.getMemberNameCond(), postSearchDto.getTitleCond(), postSearchDto.getHasCommentCond(), postSearchDto.getIsAcceptedCommentCond())
                )
                .orderBy(post.likeCnt.desc())
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
                        post.comments.size())).distinct()
                .from(post)
                .leftJoin(post.member, member)
                .leftJoin(comment).on(post.id.eq(comment.post.id))
                .where(post.postCategory.stringValue().eq(postSearchDto.getPostCategory()),
                        post.type.stringValue().eq(postSearchDto.getPostType()),
                        allCond(postSearchDto.getKeyWordsCond(), postSearchDto.getMemberNameCond(), postSearchDto.getTitleCond(), postSearchDto.getHasCommentCond(), postSearchDto.getIsAcceptedCommentCond())
                )
                .orderBy(post.likeCnt.asc())
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
                        post.comments.size())).distinct()
                .from(post)
                .leftJoin(post.member, member)
                .leftJoin(comment).on(post.id.eq(comment.post.id))
                .where(post.postCategory.stringValue().eq(postSearchDto.getPostCategory()),
                        post.type.stringValue().eq(postSearchDto.getPostType()),
                        allCond(postSearchDto.getKeyWordsCond(), postSearchDto.getMemberNameCond(), postSearchDto.getTitleCond(), postSearchDto.getHasCommentCond(), postSearchDto.getIsAcceptedCommentCond())
                )
                .orderBy(post.comments.size().desc())
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
                .where(post.postCategory.stringValue().eq(postSearchDto.getPostCategory()),
                        post.type.stringValue().eq(postSearchDto.getPostType()),
                        allCond(postSearchDto.getKeyWordsCond(), postSearchDto.getMemberNameCond(), postSearchDto.getTitleCond(), postSearchDto.getHasCommentCond(), postSearchDto.getIsAcceptedCommentCond())
                )
                .orderBy(post.comments.size().asc())
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
                .where(post.postCategory.stringValue().eq(postSearchDto.getPostCategory()),
                        post.type.stringValue().eq(postSearchDto.getPostType()),
                        allCond(postSearchDto.getKeyWordsCond(), postSearchDto.getMemberNameCond(), postSearchDto.getTitleCond(), postSearchDto.getHasCommentCond(), postSearchDto.getIsAcceptedCommentCond())
                )
                .orderBy(post.views.asc())
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
                .where(post.postCategory.stringValue().eq(postSearchDto.getPostCategory()),
                        post.type.stringValue().eq(postSearchDto.getPostType()),
                        allCond(postSearchDto.getKeyWordsCond(), postSearchDto.getMemberNameCond(), postSearchDto.getTitleCond(), postSearchDto.getHasCommentCond(), postSearchDto.getIsAcceptedCommentCond())
                )
                .orderBy(post.views.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<PostSimpleDto> content = results.getResults();
        long total = results.getTotal();


        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<PostSimpleDto> findPostsOrderByPopularDesc(PostSearchDto postSearchDto, Pageable pageable) {
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
                .where(post.postCategory.stringValue().eq(postSearchDto.getPostCategory()),
                        post.type.stringValue().eq(postSearchDto.getPostType()),
                        allCond(postSearchDto.getKeyWordsCond(), postSearchDto.getMemberNameCond(), postSearchDto.getTitleCond(), postSearchDto.getHasCommentCond(), postSearchDto.getIsAcceptedCommentCond())
                )
                .groupBy(post)
                .orderBy((post.views.multiply(Expressions.asNumber(0.5).floatValue())
                        .add((post.likeCnt.multiply(post.likeCnt).divide(post.likeCnt.add(post.dislikeCnt)).multiply(Expressions.asNumber(0.3).floatValue())))
                        .add(comment.count().multiply(Expressions.asNumber(0.2).floatValue()))).desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<PostSimpleDto> contents = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(contents, pageable, total);
    }

    // 필터 조건 조합
    private BooleanExpression allCond(List<String> keyWordsCond, String memberNameCond, String titleCond, Boolean hasCommentCond, Boolean isAcceptedCommentCond) {
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

        if (keyWordsCond != null && !keyWordsCond.isEmpty()) {
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

    private BooleanExpression postTitleContain(String titleCond) { return isEmpty(titleCond) ? null : post.title.contains(titleCond);}

    private BooleanExpression memberNameContain(String memberNameCond) { return isEmpty(memberNameCond) ? null : member.name.contains(memberNameCond); }

    private BooleanExpression keyWordsContain(List<String> keyWordsCond) {
        if (keyWordsCond.isEmpty())
            return null;
        else
            return keyWordsCond.stream().map(post.keyWords::contains).reduce(BooleanExpression::or).orElse(null);
    }
}
