package com.notfound.lpickbackend.support.query.util;

import com.notfound.lpickbackend.support.command.domain.Question;
import org.springframework.data.jpa.domain.Specification;

public class QuestionSpec {

    public static Specification<Question> keywordLike(String keyword) {
        if (keyword == null) return null;
        String kw = keyword.trim();
        if (kw.isEmpty()) return null;

        // LIKE 와일드카드('%','_')가 키워드에 들어오면 이스케이프
        kw = kw.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");

        String pattern = "%" + kw.toLowerCase() + "%";
        return (root, q, cb) -> cb.like(cb.lower(root.get("title")), pattern, '\\');
    }
}
