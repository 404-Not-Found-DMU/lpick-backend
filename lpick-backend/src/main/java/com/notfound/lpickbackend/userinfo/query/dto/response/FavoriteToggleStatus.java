package com.notfound.lpickbackend.userinfo.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 명칭은 status이나, enum이 아닌 boolean 기반임에 유의하기 */
@Getter
@AllArgsConstructor
public class FavoriteToggleStatus {
    private boolean isFavorite;
}
