package com.notfound.lpickbackend.common._aop.point;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("pointArg") // ← SpEL에서 @pointArg 로 접근할 이름
@RequiredArgsConstructor
public class PointArgExtractor {

//    private final CommentRepository commentRepository; // 예: 방금 저장한 댓글을 찾기 위해 사용 (필요 시)
//
//    // userId를 문자열로 반환 (예: OAuth ID 문자열, 또는 DB PK를 문자열화)
//    public String userId(Object[] args, Object result, Authentication auth) {
//        // 1) auth.principal 우선
//        if (auth != null && auth.getPrincipal() instanceof MyUserPrincipal p) {
//            return p.getOauthId(); // 또는 String.valueOf(p.getUserId())
//        }
//        // 2) 파라미터에서 꺼내보기 (프로젝트 DTO에 맞게 변경)
//        if (args.length > 0 && args[0] instanceof WriteCommentCommand cmd) {
//            // cmd에 authorId(문자열)나 oauthId가 있다면:
//            return cmd.getAuthorOauthId(); // 예시
//        }
//        return null; // 못 찾으면 null (어스펙트에서 스킵/WARN)
//    }
//
//    // sourceId를 문자열로 반환
//    public String sourceId(Object[] args, Object result) {
//        // 1) 리턴값이 ID인 경우
//        if (result != null) return String.valueOf(result);
//
//        // 2) 리턴값 없고, 파라미터에 clientTempId 같은 게 있다면 그걸로 DB에서 조회
//        if (args.length > 0 && args[0] instanceof WriteCommentCommand cmd) {
//            String tempKey = cmd.getClientTempId(); // 클라가 보낸 임시키(중복방지/조회키)
//            if (tempKey != null) {
//                Long id = commentRepository.findIdByClientTempId(tempKey).orElse(null);
//                return id == null ? null : String.valueOf(id);
//            }
//        }
//        return null; // 못 찾으면 null → 어스펙트에서 스킵/WARN
//    }
}