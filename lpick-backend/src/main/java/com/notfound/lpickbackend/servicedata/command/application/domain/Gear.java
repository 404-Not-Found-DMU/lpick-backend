package com.notfound.lpickbackend.servicedata.command.application.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.notfound.lpickbackend.common._super.BaseEntity;
import com.notfound.lpickbackend.servicedata.command.application.domain.dto.TempGearRequest;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "gear")
public class Gear extends BaseEntity {

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "model_name", length = 100)
    private String modelName;

    @Column(name = "brand", length = 50)
    private String brand;

    @Column(name = "is_temp", nullable = false)
    private boolean isTemp;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "eq_class", nullable = false)
    private GearClass eqClass;

    @OneToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "wiki_id")
    private WikiPage wiki;

    @Column(name = "img", length = 512)
    private String img;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "meta", columnDefinition = "jsonb", nullable = true)
    private JsonNode meta;

    // isTemp True -> False로 변환. 사용자가 임시로 작성해뒀던 Gear를 수정없이 허가한다.
    public void approveTempGear() {
        this.isTemp = false;
    }

    public void updateTempGear(TempGearRequest req, GearClass eqClass) {
        this.modelName = req.getModelName();
        this.eqClass = eqClass;
        this.brand = req.getBrand();
    }

}