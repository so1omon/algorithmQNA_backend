package algorithm_QnA_community.algorithm_QnA_community.repository;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.PostSearchDto;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.PostSimpleDto;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.QPostSimpleDto;
import algorithm_QnA_community.algorithm_QnA_community.domain.comment.QComment;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static algorithm_QnA_community.algorithm_QnA_community.domain.comment.QComment.*;
import static algorithm_QnA_community.algorithm_QnA_community.domain.member.QMember.*;
import static algorithm_QnA_community.algorithm_QnA_community.domain.post.QPost.post;
import static org.springframework.util.StringUtils.isEmpty;

@RequiredArgsConstructor
@Repository
@Slf4j
public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostSimpleDto> findPostsOrderByCreatedDateDesc(PostSearchDto postSearchDto, Pageable pageable) {
        List<PostSimpleDto> results = queryFactory
                .select(new QPostSimpleDto(
                        post.id,
                        post.title,
                        member.id,
                        member.name,
                        member.profileImgUrl,
                        post.createdDate,
                        post.views,
                        post.comments.size()))
                .from(post)
                .leftJoin(post.member, member)
                .leftJoin(post.comments, comment)
                .where(post.postCategory.stringValue().eq(postSearchDto.getPostCategory()),
                        post.type.stringValue().eq(postSearchDto.getPostType()),
                        keyWordsContain(postSearchDto.getKeyWordsCond()),
                        memberNameContain(postSearchDto.getMemberNameCond()),
                        postTitleContain(postSearchDto.getTitleCond()),
                        hasComments(postSearchDto.getHasCommentCond()),
                        isAcceptedCommend(postSearchDto.getIsAcceptedCommentCond())
                )
                .orderBy(post.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(post)
                .from(post)
                .where(post.postCategory.stringValue().eq(postSearchDto.getPostCategory()),
                        post.type.stringValue().eq(postSearchDto.getPostType()))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    private BooleanExpression isAcceptedCommend(Boolean isAcceptedCommentCond) {
        if (isAcceptedCommentCond == null) return null;
        else {
            return null;
        }
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
        if (keyWordsCond.isEmpty() || keyWordsCond==null)
            return null;
        else
            return keyWordsCond.stream().map(post.keyWords::contains).reduce(BooleanExpression::and).orElse(null);
    }
}
