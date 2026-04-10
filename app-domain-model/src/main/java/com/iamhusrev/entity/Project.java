package com.iamhusrev.entity;

import com.iamhusrev.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.Where;

import jakarta.persistence.*;
import java.time.LocalDate;


@Entity
@Table(name = "projects", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"project_code", "organization_id"})
})
@NoArgsConstructor
@Getter
@Setter
@Where(clause = "is_deleted=false")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"},ignoreUnknown = true)
public class Project extends BaseEntity {

    @Column(name = "project_code")
    private String projectCode;

    private String projectName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User assignedManager;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private Status projectStatus;

    private String projectDetail;


}
