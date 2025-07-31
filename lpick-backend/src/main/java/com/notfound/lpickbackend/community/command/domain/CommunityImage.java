package com.notfound.lpickbackend.community.command.domain;

import com.notfound.lpickbackend.common._super.BaseEntity;
import jakarta.persistence.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "community_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityImage extends BaseEntity {


    @Column(name = "src", nullable = false, length = 255)
    private String src;

    @Column(name = "type", nullable = false, length = 10)
    private String type;
}
