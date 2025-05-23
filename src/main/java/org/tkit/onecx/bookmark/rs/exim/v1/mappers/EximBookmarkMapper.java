package org.tkit.onecx.bookmark.rs.exim.v1.mappers;

import java.time.OffsetDateTime;
import java.util.*;

import org.mapstruct.*;
import org.tkit.onecx.bookmark.domain.models.Bookmark;
import org.tkit.onecx.bookmark.domain.models.Image;
import org.tkit.onecx.bookmark.domain.models.enums.Scope;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.bookmark.rs.exim.v1.model.BookmarkSnapshotDTOV1;
import gen.org.tkit.onecx.bookmark.rs.exim.v1.model.EximBookmarkDTOV1;
import gen.org.tkit.onecx.bookmark.rs.exim.v1.model.EximBookmarkScopeDTOV1;
import gen.org.tkit.onecx.bookmark.rs.exim.v1.model.ImageDTOV1;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface EximBookmarkMapper {

    List<Scope> mapScopeList(List<EximBookmarkScopeDTOV1> scopes);

    default BookmarkSnapshotDTOV1 mapToSnapshot(List<Bookmark> bookmarks, List<Image> images) {
        BookmarkSnapshotDTOV1 bookmarkSnapshotDTO = new BookmarkSnapshotDTOV1();
        bookmarkSnapshotDTO.setCreated(OffsetDateTime.now());
        bookmarkSnapshotDTO.setId(UUID.randomUUID().toString());

        Map<String, List<EximBookmarkDTOV1>> bookmarksMap = new HashMap<>();
        List<EximBookmarkDTOV1> publics = new ArrayList<>();
        List<EximBookmarkDTOV1> privates = new ArrayList<>();

        List<ImageDTOV1> imageDTOV1s = new ArrayList<>();
        images.forEach(image -> imageDTOV1s.add(map(image)));

        bookmarks.forEach(bookmark -> {

            Optional<Image> matchingImage = images.stream()
                    .filter(image -> image.getRefId().equals(bookmark.getId()))
                    .findFirst();

            ImageDTOV1 image = null;
            if (matchingImage.isPresent()) {
                image = map(matchingImage.get());
            }

            if (Scope.PUBLIC.name().equals(bookmark.getScope().toString())) {
                publics.add(mapExim(bookmark, image));
            } else if (Scope.PRIVATE.name().equals(bookmark.getScope().toString())) {
                privates.add(mapExim(bookmark, image));
            }
        });
        if (!publics.isEmpty()) {
            bookmarksMap.put(Scope.PUBLIC.name(), publics);
        }
        if (!privates.isEmpty()) {
            bookmarksMap.put(Scope.PRIVATE.name(), privates);
        }

        bookmarkSnapshotDTO.setBookmarks(bookmarksMap);
        return bookmarkSnapshotDTO;
    }

    ImageDTOV1 map(Image image);

    @Named("length")
    default Integer length(byte[] data) {
        if (data == null) {
            return 0;
        }
        return data.length;
    }

    @Mapping(target = "image", ignore = true)
    @Mapping(target = "removeQueryItem", ignore = true)
    @Mapping(target = "removeEndpointParametersItem", ignore = true)
    EximBookmarkDTOV1 mapExim(Bookmark bookmark, ImageDTOV1 image);

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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "length", source = "image.imageData", qualifiedByName = "length")
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    Image createImage(ImageDTOV1 image, String refId);
}
