package com.iamhusrev.entity;

import lombok.*;
import org.hibernate.annotations.Where;
import jakarta.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "organizations")
@Where(clause = "is_deleted=false")
public class Organization extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
}
