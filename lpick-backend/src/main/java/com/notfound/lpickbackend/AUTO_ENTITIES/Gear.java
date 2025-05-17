package com.notfound.lpickbackend.AUTO_ENTITIES;

import com.notfound.lpickbackend.wikipage.command.application.domain.WikiPage;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "gear")
public class Gear {
    @Id
    @Column(name = "eq_id", nullable = false, length = 40)
    private String eqId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "model_name", length = 100)
    private String modelName;

    @Column(name = "brand", length = 50)
    private String brand;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "eq_class", nullable = false)
    private GearClass eqClass;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wiki_id")
    private WikiPage wiki;

}