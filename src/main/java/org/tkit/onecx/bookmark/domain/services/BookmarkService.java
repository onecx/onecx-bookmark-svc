package org.tkit.onecx.bookmark.domain.services;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.onecx.bookmark.domain.daos.BookmarkDAO;
import org.tkit.onecx.bookmark.domain.daos.ImageDAO;
import org.tkit.onecx.bookmark.domain.models.Image;
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

    @Inject
    ImageDAO imageDAO;

    @Transactional
    public void importBookmarks(ImportBookmarkRequestDTOV1 requestDTO) {
        var userId = ApplicationContext.get().getPrincipal();

        List<Image> createImages = new ArrayList<>();

        requestDTO.getSnapshot().getBookmarks().values()
                .forEach(bookmarkList -> bookmarkList.forEach(eximBookmarkDTOV1 -> {
                    var createdBookmark = dao
                            .create(bookmarkMapper.map(eximBookmarkDTOV1, userId, requestDTO.getWorkspace()));
                    if (eximBookmarkDTOV1.getImage() != null) {
                        var imageToImport = bookmarkMapper.createImage(eximBookmarkDTOV1.getImage(),
                                createdBookmark.getId());
                        createImages.add(imageToImport);
                    }
                }));

        if (requestDTO.getImportMode().equals(EximModeDTOV1.OVERWRITE)) {
            dao.deleteAllByWorkspaceName(requestDTO.getWorkspace());
        }
        imageDAO.create(createImages);
    }

    @Transactional
    public void deleteBookmark(String id) {
        var bookmark = dao.findById(id);
        if (bookmark != null) {
            dao.delete(bookmark);
            imageDAO.deleteQueryByRefId(bookmark.getId());
        }
    }
}
