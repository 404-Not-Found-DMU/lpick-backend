package com.notfound.lpickbackend.userinfo.query.dto.response.usergear;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserGearCollectionResponse {
    private String userId;
    private GearInfoResponse ownedSpeaker;
    private GearInfoResponse ownedHeadPhone;
    private GearInfoResponse ownedTurnTable;
}
