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
import org.tkit.onecx.bookmark.domain.daos.StaticBookmarkDAO;
import org.tkit.onecx.bookmark.rs.internal.mappers.ExceptionMapper;
import org.tkit.onecx.bookmark.rs.internal.mappers.StaticBookmarkMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.bookmark.rs.internal.StaticBookmarksInternalApi;
import gen.org.tkit.onecx.bookmark.rs.internal.model.CreateStaticBookmarkDTO;
import gen.org.tkit.onecx.bookmark.rs.internal.model.ProblemDetailResponseDTO;
import gen.org.tkit.onecx.bookmark.rs.internal.model.StaticBookmarkSearchCriteriaDTO;
import gen.org.tkit.onecx.bookmark.rs.internal.model.UpdateStaticBookmarkDTO;

@LogService
@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
public class StaticBookmarkRestController implements StaticBookmarksInternalApi {

    @Inject
    StaticBookmarkMapper mapper;

    @Inject
    ExceptionMapper exceptionMapper;

    @Inject
    StaticBookmarkDAO staticBookmarkDAO;

    @Context
    UriInfo uriInfo;

    @Override
    public Response createNewStaticBookmark(CreateStaticBookmarkDTO createStaticBookmarkDTO) {
        var bookmark = mapper.create(createStaticBookmarkDTO);
        bookmark = staticBookmarkDAO.create(bookmark);
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(bookmark.getId()).build())
                .entity(mapper.map(bookmark))
                .build();
    }

    @Override
    public Response deleteStaticBookmarkById(String id) {
        staticBookmarkDAO.deleteQueryById(id);
        return Response.noContent().build();
    }

    @Override
    public Response searchStaticBookmarksByCriteria(StaticBookmarkSearchCriteriaDTO staticBookmarkSearchCriteriaDTO) {
        var criteria = mapper.mapCriteria(staticBookmarkSearchCriteriaDTO);
        var bookmarks = staticBookmarkDAO.findStaticBookmarksByCriteria(criteria);
        return Response.ok(mapper.mapPage(bookmarks)).build();
    }

    @Override
    public Response updateStaticBookmark(String id, UpdateStaticBookmarkDTO updateStaticBookmarkDTO) {
        var staticBookmark = staticBookmarkDAO.findById(id);
        if (staticBookmark == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        mapper.update(updateStaticBookmarkDTO, staticBookmark);
        staticBookmarkDAO.update(staticBookmark);
        return Response.noContent().build();
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
