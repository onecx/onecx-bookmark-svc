package org.tkit.onecx.bookmark.domain.criteria;

import org.tkit.onecx.bookmark.domain.models.enums.Scope;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RegisterForReflection
public class BookmarkSearchCriteria {

    private String workspaceName;

    private String productName;

    private String appId;

    private Scope scope;

    private Integer pageNumber;

    private Integer pageSize;

}
