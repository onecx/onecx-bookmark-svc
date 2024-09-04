package org.tkit.onecx.bookmark.rs.internal.controllers;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.bookmark.domain.daos.BookmarkDAO;
import org.tkit.onecx.bookmark.rs.internal.mappers.BookmarkMapper;
import org.tkit.onecx.bookmark.rs.internal.mappers.ExceptionMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.bookmark.rs.internal.BookmarksInternalApi;
import gen.org.tkit.onecx.bookmark.rs.internal.model.BookmarkSearchCriteriaDTO;
import gen.org.tkit.onecx.bookmark.rs.internal.model.CreateBookmarkDTO;
import gen.org.tkit.onecx.bookmark.rs.internal.model.ProblemDetailResponseDTO;
import gen.org.tkit.onecx.bookmark.rs.internal.model.UpdateBookmarkDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@LogService
@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
public class BookmarkRestController implements BookmarksInternalApi {

    @Inject
    BookmarkDAO bookmarkDAO;

    @Inject
    BookmarkMapper bookmarkMapper;

    @Inject
    ExceptionMapper exceptionMapper;

    @Context
    UriInfo uriInfo;

    @Override
    public Response createNewBookmark(CreateBookmarkDTO createBookmarkDTO) {
        var bookmark = bookmarkMapper.create(createBookmarkDTO);
        bookmark = bookmarkDAO.create(bookmark);
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(bookmark.getId()).build())
                .entity(bookmarkMapper.map(bookmark))
                .build();
    }

    @Override
    public Response deleteBookmarkById(String id) {
        bookmarkDAO.deleteQueryById(id);
        return Response.noContent().build();
    }

    @Override
    public Response updateBookmark(String id, UpdateBookmarkDTO updateBookmarkDTO) {
        var bookmark = bookmarkDAO.findById(id);
        if (bookmark == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        bookmarkMapper.update(updateBookmarkDTO, bookmark);
        bookmarkDAO.update(bookmark);
        return Response.noContent().build();
    }

    @Override
    public Response searchBookmarksByCriteria(BookmarkSearchCriteriaDTO bookmarkSearchCriteriaDTO) {
        var criteria = bookmarkMapper.map(bookmarkSearchCriteriaDTO);
        var bookmarks = bookmarkDAO.findBookmarksByCriteria(criteria);
        return Response.ok(bookmarkMapper.mapPage(bookmarks)).build();
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> exception(ConstraintException ex) {
        return exceptionMapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> optimisticLock(OptimisticLockException ex) {
        return exceptionMapper.optimisticLock(ex);
    }
}
