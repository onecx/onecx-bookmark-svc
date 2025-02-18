package org.tkit.onecx.bookmark.domain.services;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.onecx.bookmark.domain.daos.BookmarkDAO;
import org.tkit.onecx.bookmark.domain.models.Bookmark;
import org.tkit.onecx.bookmark.rs.exim.v1.mappers.EximBookmarkMapper;
import org.tkit.quarkus.context.ApplicationContext;

import gen.org.tkit.onecx.bookmark.rs.exim.v1.model.EximModeDTOV1;
import gen.org.tkit.onecx.bookmark.rs.exim.v1.model.ImportBookmarkRequestDTOV1;

@ApplicationScoped
public class BookmarkService {

    @Inject
    EximBookmarkMapper bookmarkMapper;

    @Inject
    BookmarkDAO dao;

    @Transactional
    public void importBookmarks(ImportBookmarkRequestDTOV1 requestDTO) {
        var userId = ApplicationContext.get().getPrincipal();

        List<Bookmark> allBookmarks = new ArrayList<>();
        requestDTO.getSnapshot().getBookmarks().values()
                .forEach(bookmarkList -> allBookmarks
                        .addAll(bookmarkMapper.mapEximList(bookmarkList, userId, requestDTO.getWorkspace())));

        if (requestDTO.getImportMode().equals(EximModeDTOV1.OVERWRITE)) {
            dao.deleteAllByWorkspaceName(requestDTO.getWorkspace());
        }
        dao.create(allBookmarks);
    }
}
