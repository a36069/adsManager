package com.abdi.ads.manager.model.audit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public abstract class AbstractAuditingEntity {

    @CreationTimestamp
    @JsonIgnore
    private Instant createdAt;

    @UpdateTimestamp
    @JsonIgnore
    private Instant updatedAt;


}
