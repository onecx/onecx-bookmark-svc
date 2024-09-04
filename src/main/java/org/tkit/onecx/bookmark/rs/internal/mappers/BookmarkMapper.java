package org.tkit.onecx.bookmark.rs.internal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.tkit.onecx.bookmark.domain.criteria.BookmarkSearchCriteria;
import org.tkit.onecx.bookmark.domain.models.Bookmark;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.bookmark.rs.internal.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface BookmarkMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "displayName", source = "displayName")
    @Mapping(target = "endpointName", source = "endpointName")
    @Mapping(target = "endpointParameters", source = "endpointParameters")
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    Bookmark create(CreateBookmarkDTO object);

    @Mapping(target = "removeEndpointParametersItem", ignore = true)
    BookmarkDTO map(Bookmark object);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "endpointName", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "query", ignore = true)
    @Mapping(target = "hash", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "workspaceName", ignore = true)
    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "appId", ignore = true)
    @Mapping(target = "scope", ignore = true)
    @Mapping(target = "endpointParameters", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    void update(UpdateBookmarkDTO bookmarkDTO, @MappingTarget Bookmark bookmark);

    BookmarkSearchCriteria map(BookmarkSearchCriteriaDTO bookmarkSearchCriteriaDTO);

    @Mapping(target = "removeStreamItem", ignore = true)
    BookmarkPageResultDTO mapPage(PageResult<Bookmark> pageResult);
}
