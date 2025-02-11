package org.tkit.onecx.bookmark.rs.internal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.tkit.onecx.bookmark.domain.criteria.StaticBookmarkSearchCriteria;
import org.tkit.onecx.bookmark.domain.models.StaticBookmark;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.bookmark.rs.internal.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface StaticBookmarkMapper {
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    StaticBookmark create(CreateStaticBookmarkDTO createStaticBookmarkDTO);

    StaticBookmarkDTO map(StaticBookmark bookmark);

    StaticBookmarkSearchCriteria mapCriteria(StaticBookmarkSearchCriteriaDTO staticBookmarkSearchCriteriaDTO);

    @Mapping(target = "removeStreamItem", ignore = true)
    StaticBookmarkPageResultDTO mapPage(PageResult<StaticBookmark> bookmarks);

    @Mapping(target = "workspaceName", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    void update(UpdateStaticBookmarkDTO updateStaticBookmarkDTO, @MappingTarget StaticBookmark staticBookmark);
}
