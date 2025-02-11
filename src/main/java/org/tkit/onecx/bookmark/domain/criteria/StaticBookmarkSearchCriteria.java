package org.tkit.onecx.bookmark.domain.criteria;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RegisterForReflection
public class StaticBookmarkSearchCriteria {

    private String workspaceName;

    private Integer pageNumber;

    private Integer pageSize;

}
