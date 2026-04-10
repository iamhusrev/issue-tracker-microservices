package com.iamhusrev.entity;

import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@MappedSuperclass
@FilterDef(name = "organizationFilter", parameters = @ParamDef(name = "orgId", type = Long.class))
@Filter(name = "organizationFilter", condition = "organization_id = :orgId")
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true,updatable = false)
    public LocalDateTime insertDateTime;
    @Column(nullable = true,updatable = false)
    public Long insertUserId;
    @Column(nullable = true)
    public LocalDateTime lastUpdateDateTime;
    @Column(nullable = true)
    public Long lastUpdateUserId;

    @Column(name = "organization_id")
    private Long organizationId;

    private Boolean isDeleted=false;

}
