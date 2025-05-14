package com.notfound.lpickbackend.AUTO_ENTITIES;

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
    @Id
    @Column(name = "class_id", nullable = false, length = 40)
    private String classId;

    @Column(name = "class_name", length = 50)
    private String className;

}