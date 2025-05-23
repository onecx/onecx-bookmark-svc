package org.tkit.onecx.bookmark.rs.exim.v1.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import org.tkit.onecx.bookmark.domain.daos.BookmarkDAO;
import org.tkit.onecx.bookmark.domain.daos.ImageDAO;
import org.tkit.onecx.bookmark.domain.models.Bookmark;
import org.tkit.onecx.bookmark.domain.services.BookmarkService;
import org.tkit.onecx.bookmark.rs.exim.v1.mappers.EximBookmarkMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.bookmark.rs.exim.v1.BookmarkExportImportApi;
import gen.org.tkit.onecx.bookmark.rs.exim.v1.model.BookmarkSnapshotDTOV1;
import gen.org.tkit.onecx.bookmark.rs.exim.v1.model.ExportBookmarksRequestDTOV1;
import gen.org.tkit.onecx.bookmark.rs.exim.v1.model.ImportBookmarkRequestDTOV1;

@LogService
@ApplicationScoped
public class BookmarkEximRestController implements BookmarkExportImportApi {

    @Inject
    BookmarkDAO dao;

    @Inject
    ImageDAO imageDAO;

    @Inject
    EximBookmarkMapper bookmarkMapper;

    @Inject
    BookmarkService bookmarkService;

    @Override
    public Response exportBookmarks(ExportBookmarksRequestDTOV1 exportBookmarksRequestDTO) {
        var bookmarks = dao.findAllBookmarksByWorkspaceAndScope(exportBookmarksRequestDTO.getWorkspaceName(),
                bookmarkMapper.mapScopeList(exportBookmarksRequestDTO.getScopes())).toList();
        var images = imageDAO.findByRefIds(bookmarks.stream().map(Bookmark::getId).toList());
        BookmarkSnapshotDTOV1 snapshotDTO = bookmarkMapper.mapToSnapshot(bookmarks, images);
        return Response.status(Response.Status.OK).entity(snapshotDTO).build();
    }

    @Override
    public Response importBookmarks(ImportBookmarkRequestDTOV1 bookmarkSnapshotDTO) {
        bookmarkService.importBookmarks(bookmarkSnapshotDTO);
        return Response.status(Response.Status.OK).build();
    }
}
