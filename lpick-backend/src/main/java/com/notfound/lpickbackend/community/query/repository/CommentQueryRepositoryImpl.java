package com.notfound.lpickbackend.community.query.repository;

import com.notfound.lpickbackend.community.command.domain.QComment;
import com.notfound.lpickbackend.community.command.domain.QCommentLike;
import com.notfound.lpickbackend.community.query.application.dto.ChildsCommentResponse;
import com.notfound.lpickbackend.community.query.application.dto.ParentsCommentResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
* <QueryDSL 사용 이유>
* 복잡한 쿼리 및 동적 쿼리를 지원해야 할 때 JPQL만으로는 어려움이 있음.
* QueryDSL은 동적쿼리를 지원하고, 쿼리를 자바 코드처럼 작성하고 컴파일 할 수 있기 때문에
* 복잡한 쿼리 실행 시 디버깅하기에도 용이합니다.
* 이번 댓글 기능 구현시 부모객체와 자식객체를 함께 저장해야 하고 동시에 좋아요 개수, 좋아요 유무 등도 함께 가져와야 하기에
* QueryDSL을 추가했습니다.
* */
public class CommentQueryRepositoryImpl implements CustomCommentQueryRepository{

    private final JPAQueryFactory queryFactory;

    public CommentQueryRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ParentsCommentResponse> findParentsCommentsWithChildrenAndLikes(String articleId, String currentUserId, Pageable pageable) {

        QComment c = QComment.comment;
        QCommentLike cl = QCommentLike.commentLike;

        // 좋아요 유무를 처리할 변수
        BooleanExpression likedExpr;

        // 만약 비회원 조회라면 좋아요 유무 false로 고정
        if (currentUserId == null) {
            likedExpr = Expressions.FALSE;
        } else {
            likedExpr = Expressions.cases()
                    .when(cl.oauth.oauthId.eq(currentUserId)).then(1)
                    .otherwise(0)
                    .sum().gt(0);
        }

        // 부모 댓글 목록 + 좋아요 개수 + 유저 좋아요 여부 조회
        List<ParentsCommentResponse> parentResponses = queryFactory
                .select(Projections.fields(
                        ParentsCommentResponse.class,
                        c.commentId,
                        c.content,
                        c.createdAt,
                        c.modifiedAt,
                        c.isDel,
                        c.article.articleId.as("articleId"),
                        c.oauth.oauthId.as("oauthId"),
                        cl.count().intValue().as("likeCount"), // 좋아요 개수
                        likedExpr.as("liked") // 좋아요 유무 liked 필드에 매핑
                ))
                .from(c)
                .leftJoin(cl).on(cl.comment.eq(c))
                .where(
                        c.article.articleId.eq(articleId),
                        c.parentComment.isNull()
                )
                .groupBy(
                        c.commentId,
                        c.content,
                        c.createdAt,
                        c.modifiedAt,
                        c.isDel,
                        c.article.articleId,
                        c.oauth.oauthId
                )
                .orderBy(c.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 카운트 (Page 객체 생성을 위한 total)
        long total = queryFactory
                .select(c.count())
                .from(c)
                .where(
                        c.article.articleId.eq(articleId),
                        c.parentComment.isNull()
                )
                .fetchOne();

        // 부모 ID 추출
        List<String> parentIds = parentResponses.stream()
                .map(ParentsCommentResponse::getCommentId)
                .toList();

        if (!parentIds.isEmpty()) {
            QComment child = QComment.comment;
            QCommentLike clChild = QCommentLike.commentLike;

            // 대댓글 목록 조회
            List<ChildsCommentResponse> childResponses = queryFactory
                    .select(Projections.fields(
                            ChildsCommentResponse.class,
                            child.commentId,
                            child.content,
                            child.createdAt,
                            child.modifiedAt,
                            child.article.articleId.as("articleId"),
                            child.parentComment.commentId.as("parentCommentId"), // 부모 아이디
                            child.oauth.oauthId.as("oauthId"),
                            clChild.count().intValue().as("likeCount"), // 좋아요 개수
                            likedExpr.as("liked")
                    ))
                    .from(child)
                    .leftJoin(clChild).on(clChild.comment.eq(child))
                    .where(child.parentComment.commentId.in(parentIds))
                    .groupBy(
                            child.commentId,
                            child.content,
                            child.createdAt,
                            child.modifiedAt,
                            child.article.articleId,
                            child.parentComment.commentId,
                            child.oauth.oauthId
                    )
                    .orderBy(child.createdAt.asc())
                    .fetch();

            // 대댓글을 부모 ID 기준으로 매핑
            Map<String, List<ChildsCommentResponse>> childMap = childResponses.stream()
                    .collect(Collectors.groupingBy(ChildsCommentResponse::getParentCommentId));

            parentResponses.forEach(parent ->
                    parent.setChildsCommentList(childMap.getOrDefault(parent.getCommentId(), new ArrayList<>()))
            );
        }

        return new PageImpl<>(parentResponses, pageable, total);
    }
}
