package org.tkit.onecx.bookmark.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.hibernate.annotations.TenantId;
import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "STATIC_BOOKMARK")
@SuppressWarnings("java:S2160")
public class StaticBookmark extends TraceableEntity {

    @TenantId
    @Column(name = "TENANT_ID")
    private String tenantId;

    @Column(name = "WORKSPACE_NAME")
    private String workspaceName;

    @Column(name = "DISPLAY_NAME")
    private String displayName;

    @Column(name = "POSITION", nullable = false)
    private Integer position;

    @Column(name = "URL")
    private String url;
}
