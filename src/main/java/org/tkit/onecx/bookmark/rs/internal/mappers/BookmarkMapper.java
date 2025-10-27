package org.tkit.onecx.bookmark.rs.internal.mappers;

import java.util.Map;

import org.mapstruct.*;
import org.tkit.onecx.bookmark.domain.criteria.BookmarkSearchCriteria;
import org.tkit.onecx.bookmark.domain.models.Bookmark;
import org.tkit.onecx.bookmark.domain.models.enums.Scope;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.bookmark.rs.internal.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface BookmarkMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "endpointParameters", qualifiedByName = "emptyToNull")
    @Mapping(target = "query", qualifiedByName = "emptyToNull")
    Bookmark create(CreateBookmarkDTO object, String userId);

    @Named("emptyToNull")
    static Map<String, String> emptyToNull(Map<String, String> parameters) {
        return (parameters == null || parameters.isEmpty()) ? null : parameters;
    }

    @Mapping(target = "removeEndpointParametersItem", ignore = true)
    @Mapping(target = "removeQueryItem", ignore = true)
    @Mapping(target = "endpointParameters", qualifiedByName = "emptyToNull")
    BookmarkDTO map(Bookmark object);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "workspaceName", ignore = true)
    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "appId", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "endpointParameters", qualifiedByName = "emptyToNull")
    @Mapping(target = "query", qualifiedByName = "emptyToNull")
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "scope", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "userId", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(UpdateBookmarkDTO bookmarkDTO, @MappingTarget Bookmark bookmark);

    @Mapping(target = "workspaceName", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "appId", ignore = true)
    @Mapping(target = "userId", constant = "")
    void updateToPublic(UpdateBookmarkDTO bookmarkDTO, @MappingTarget Bookmark bookmark);

    BookmarkSearchCriteria map(BookmarkSearchCriteriaDTO bookmarkSearchCriteriaDTO);

    Scope map(BookmarkScopeDTO scopeDTO);

    @Mapping(target = "removeStreamItem", ignore = true)
    BookmarkPageResultDTO mapPage(PageResult<Bookmark> pageResult);

    default String mapTarget(TargetDTO targetDTO) {
        if (targetDTO != null) {
            return targetDTO.toString();
        }
        return "_self";
    }

    default TargetDTO mapTarget(String value) {
        try {
            return TargetDTO.fromValue(value);
        } catch (IllegalArgumentException | NullPointerException e) {
            return TargetDTO._SELF;
        }
    }
}
