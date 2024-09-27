package org.tkit.onecx.bookmark.domain.models;

import java.util.Map;

import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.TenantId;
import org.hibernate.type.SqlTypes;
import org.tkit.onecx.bookmark.domain.models.enums.Scope;
import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "BOOKMARK", indexes = {
        @Index(name = "IDX_USER_WORKSPACE_SCOPE", columnList = "USER_ID, WORKSPACE_NAME, SCOPE") })
@SuppressWarnings("java:S2160")
public class Bookmark extends TraceableEntity {

    @Column(name = "DISPLAY_NAME")
    private String displayName;

    @Column(name = "ENDPOINT_NAME")
    private String endpointName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ENDPOINT_PARAMETERS")
    private Map<String, String> endpointParameters;

    @Column(name = "QUERY")
    private String query;

    @Column(name = "HASH")
    private String hash;

    @Column(name = "USER_ID")
    private String userId;

    @TenantId
    @Column(name = "TENANT_ID")
    private String tenantId;

    @Column(name = "WORKSPACE_NAME")
    private String workspaceName;

    @Column(name = "PRODUCT_NAME")
    private String productName;

    @Column(name = "APP_ID")
    private String appId;

    @Column(name = "SCOPE")
    @Enumerated(EnumType.STRING)
    private Scope scope;

    @Column(name = "POSITION", nullable = false)
    private Integer position;
}
