package com.notfound.lpickbackend.servicedata.command.application.domain;

import com.notfound.lpickbackend.common._super.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "gear_class")
public class GearClass {

    // GearClass는 enum을 그대로 나타내는 목적의 엔티티.
    // 즉, 별도의 id 둘 필요 없이 className == PK로 사용하면 끝.
    @Id
    @Column(name = "class_name", length = 50)
    private String className;

}