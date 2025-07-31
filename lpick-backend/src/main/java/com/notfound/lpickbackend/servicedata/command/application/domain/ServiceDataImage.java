package com.notfound.lpickbackend.servicedata.command.application.domain;

import com.notfound.lpickbackend.common._super.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "service_data_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceDataImage extends BaseEntity {


    @Column(name = "src", nullable = false, length = 255)
    private String src;

    @Column(name = "type", nullable = false, length = 10)
    private String type;
}