package com.notfound.lpickbackend.servicedata.query.dto;

import com.notfound.lpickbackend.userinfo.command.application.domain.inherenceENUM.GearClass;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GearSearchResultDTO {

    private String gearId;
    private GearClass eqClass;
    private String modelName;
    private String brand;
    private String name;
    private String img;

}
