package org.tkit.onecx.bookmark.rs.exim.v1.mappers;

import java.time.OffsetDateTime;
import java.util.*;

import org.mapstruct.*;
import org.tkit.onecx.bookmark.domain.models.Bookmark;
import org.tkit.onecx.bookmark.domain.models.enums.Scope;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.bookmark.rs.exim.v1.model.BookmarkSnapshotDTOV1;
import gen.org.tkit.onecx.bookmark.rs.exim.v1.model.EximBookmarkDTOV1;
import gen.org.tkit.onecx.bookmark.rs.exim.v1.model.EximBookmarkScopeDTOV1;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface EximBookmarkMapper {

    List<Scope> mapScopeList(List<EximBookmarkScopeDTOV1> scopes);

    default BookmarkSnapshotDTOV1 mapToSnapshot(List<Bookmark> bookmarks) {
        BookmarkSnapshotDTOV1 bookmarkSnapshotDTO = new BookmarkSnapshotDTOV1();
        bookmarkSnapshotDTO.setCreated(OffsetDateTime.now());
        bookmarkSnapshotDTO.setId(UUID.randomUUID().toString());

        Map<String, List<EximBookmarkDTOV1>> bookmarksMap = new HashMap<>();
        List<EximBookmarkDTOV1> publics = new ArrayList<>();
        List<EximBookmarkDTOV1> privates = new ArrayList<>();

        bookmarks.forEach(bookmark -> {
            if (Scope.PUBLIC.name().equals(bookmark.getScope().toString())) {
                publics.add(mapExim(bookmark));
            } else if (Scope.PRIVATE.name().equals(bookmark.getScope().toString())) {
                privates.add(mapExim(bookmark));
            }
        });
        if (!publics.isEmpty()) {
            bookmarksMap.put(Scope.PUBLIC.name(), publics);
        }
        if (!privates.isEmpty()) {
            bookmarksMap.put(Scope.PRIVATE.name(), new ArrayList<>());
        }
        bookmarkSnapshotDTO.setBookmarks(bookmarksMap);
        return bookmarkSnapshotDTO;
    }

    @Mapping(target = "removeQueryItem", ignore = true)
    @Mapping(target = "removeEndpointParametersItem", ignore = true)
    EximBookmarkDTOV1 mapExim(Bookmark bookmark);

    default List<Bookmark> mapEximList(List<EximBookmarkDTOV1> bookmarkList, String userId, String workspaceName) {
        List<Bookmark> bookmarks = new ArrayList<>();
        bookmarkList.forEach(eximBookmarkDTOV1 -> bookmarks.add(map(eximBookmarkDTOV1, userId, workspaceName)));
        return bookmarks;
    }

    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "workspaceName", source = "workspaceName")
    Bookmark map(EximBookmarkDTOV1 dto, String userId, String workspaceName);
}
