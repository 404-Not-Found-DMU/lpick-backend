package com.notfound.lpickbackend.community.query.repository;

import com.notfound.lpickbackend.community.command.domain.CommentStatus;
import com.notfound.lpickbackend.community.command.domain.QArticle;
import com.notfound.lpickbackend.community.command.domain.QComment;
import com.notfound.lpickbackend.community.command.domain.QCommentLike;
import com.notfound.lpickbackend.community.query.dto.ChildsCommentResponse;
import com.notfound.lpickbackend.community.query.dto.CommentListResponse;
import com.notfound.lpickbackend.community.query.dto.ParentsCommentResponse;
import com.notfound.lpickbackend.servicedata.query.inherenceEnum.CommentListFilter;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.QUserInfo;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
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
public class CommentQueryRepositoryImpl implements CustomCommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    public CommentQueryRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ParentsCommentResponse> findCommentsWithChildrenAndLikes(String articleId, String oauthId, Pageable pageable) {

        QComment c = QComment.comment;
        QCommentLike cl = QCommentLike.commentLike;

        // 좋아요 유무를 처리할 변수
        BooleanExpression likedExpr;

        // 만약 비회원 조회라면 좋아요 유무 false로 고정
        if (oauthId == null) {
            likedExpr = Expressions.FALSE;
        } else {
            likedExpr = Expressions.cases()
                    .when(cl.oauth.oauthId.eq(oauthId)).then(1)
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
                        c.oauth.nickname.as("author"),
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
                        c.oauth.oauthId,
                        c.oauth.nickname
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
                            c.oauth.nickname.as("author"),
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
                            child.oauth.oauthId,
                            c.oauth.nickname
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

    @Override
    public Page<ParentsCommentResponse> findParentsByOauthIdAndCommentLike(String oAuthId, Pageable pageable) {

        QComment c = QComment.comment;
        QCommentLike cl = QCommentLike.commentLike;

        List<ParentsCommentResponse> results = queryFactory
                .select(Projections.fields(
                        ParentsCommentResponse.class,
                        c.commentId,
                        c.content,
                        c.createdAt,
                        c.modifiedAt,
                        c.isDel,
                        c.article.articleId.as("articleId"),
                        c.oauth.oauthId.as("oauthId"),
                        c.oauth.nickname.as("author"),
                        Expressions.TRUE.as("liked"), // 내가 좋아요 누른 목록이므로 항상 true
                        cl.count().intValue().as("likeCount")
                ))
                .from(cl)
                .join(cl.comment, c)
                .where(
                        cl.oauth.oauthId.eq(oAuthId),
                        c.parentComment.isNull()
                )
                .groupBy(
                        c.commentId,
                        c.content,
                        c.createdAt,
                        c.modifiedAt,
                        c.isDel,
                        c.article.articleId,
                        c.oauth.oauthId,
                        c.oauth.nickname
                )
                .orderBy(c.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(cl.countDistinct())
                .from(cl)
                .join(cl.comment, c)
                .where(
                        cl.oauth.oauthId.eq(oAuthId),
                        c.parentComment.isNull()
                )
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }


    // 이 코드는 사실 1줄만 다른거라 위에 있는 부모 댓글 조회와 합칠지 이대로 구분할지 고민중입니다.
    @Override
    public Page<ParentsCommentResponse> findChildByOauthIdAndCommentLike(String oAuthId, Pageable pageable) {

        QComment c = QComment.comment;
        QCommentLike cl = QCommentLike.commentLike;

        List<ParentsCommentResponse> results = queryFactory
                .select(Projections.fields(
                        ParentsCommentResponse.class,
                        c.commentId,
                        c.content,
                        c.createdAt,
                        c.modifiedAt,
                        c.isDel,
                        c.article.articleId.as("articleId"),
                        c.oauth.oauthId.as("oauthId"),
                        c.oauth.nickname.as("author"),
                        Expressions.TRUE.as("liked"), // 내가 좋아요 누른 목록이므로 항상 true
                        cl.count().intValue().as("likeCount")
                ))
                .from(cl)
                .join(cl.comment, c)
                .where(
                        cl.oauth.oauthId.eq(oAuthId),
                        c.parentComment.isNotNull()
                )
                .groupBy(
                        c.commentId,
                        c.content,
                        c.createdAt,
                        c.modifiedAt,
                        c.isDel,
                        c.article.articleId,
                        c.oauth.oauthId,
                        c.oauth.nickname
                )
                .orderBy(c.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(cl.countDistinct())
                .from(cl)
                .join(cl.comment, c)
                .where(
                        cl.oauth.oauthId.eq(oAuthId),
                        c.parentComment.isNull()
                )
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<CommentListResponse> findCommentListByOauthIdWithFilter(
            String oauthId,
            CommentListFilter filter,
            Pageable pageable
    ) {
        QComment comment = QComment.comment;
        QComment parent = new QComment("parentComment"); // 부모 댓글 alias
        QUserInfo parentWriter = new QUserInfo("parentWriter"); // 부모 작성자 alias
        QCommentLike commentLike = QCommentLike.commentLike;
        QArticle article = QArticle.article;

        // 공통 where 조건: 삭제 안 된 + 내가 쓴 댓글
        BooleanBuilder where = new BooleanBuilder()
                .and(comment.isDel.eq(CommentStatus.N))
                .and(comment.oauth.oauthId.eq(oauthId));

        // 필터 조건
        switch (filter) {
            case ONLY_COMMENT -> where.and(comment.parentComment.isNull());
            case ONLY_REPLY -> where.and(comment.parentComment.isNotNull());
            case ALL, LIKE_DESC -> { /* 추가 조건 없음 */ }
        }

        // 좋아요 수 집계 표현식
        var likeCountExpression = commentLike.comment.commentId.countDistinct();

        // 정렬 조건
        var orderSpecifier = switch (filter) {
            case LIKE_DESC -> likeCountExpression.desc().nullsLast();
            default -> comment.createdAt.desc();
        };

        // 본문 조회 쿼리
        JPAQuery<CommentListResponse> contentQuery = queryFactory
                .select(Projections.constructor(
                        CommentListResponse.class,
                        comment.parentComment.isNotNull(),   // isReplyComment
                        parentWriter.nickname,               // parentCommentWriterName
                        comment.content,                     // commentValue
                        comment.createdAt,                   // createdAt
                        article.articleId,                   // articleId
                        article.title,                       // articleTitle
                        article.oauth.nickname,              // articleWriterName
                        likeCountExpression                  // commentLikeCount
                ))
                .from(comment)
                .join(comment.article, article)

// 부모 댓글 LEFT JOIN
                .leftJoin(comment.parentComment, parent)

// 부모 댓글 작성자 LEFT JOIN (여기가 새로 추가)
                .leftJoin(parent.oauth, parentWriter)

// 좋아요 LEFT JOIN
                .leftJoin(commentLike).on(commentLike.comment.eq(comment))
                .where(where)
                .groupBy(
                        comment.commentId,
                        comment.content,
                        comment.createdAt,
                        parent.commentId,
                        parentWriter.nickname,       // ← 여기
                        article.articleId,
                        article.title,
                        article.oauth.nickname
                )
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<CommentListResponse> content = contentQuery.fetch();

        // total count (groupBy 필요 없음, distinct commentId만 세면 됨)
        Long total = queryFactory
                .select(comment.commentId.countDistinct())
                .from(comment)
                .where(where)
                .fetchOne();

        if (total == null) total = 0L;

        return new PageImpl<>(content, pageable, total);
    }

}
