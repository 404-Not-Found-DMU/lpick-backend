package com.notfound.lpickbackend.AUTO_ENTITIES;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "eq_class", nullable = false, length = 50)
    private String eqClass;

    @Column(name = "wiki_id", length = 40)
    private String wikiId;

}