package org.tkit.onecx.bookmark.domain.daos;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class BookmarkDAOTest {
    @Inject
    BookmarkDAO dao;

    @InjectMock
    EntityManager em;

    @BeforeEach
    void beforeAll() {
        Mockito.when(em.getCriteriaBuilder()).thenThrow(new RuntimeException("Test technical error exception"));
    }

    @Test
    void findTenantIdByOrgIdExceptionTest() {
        methodExceptionTests(() -> dao.findBookmarksByCriteria(null),
                BookmarkDAO.ErrorKeys.ERROR_GET_BY_BOOKMARK_CRITERIA);
        methodExceptionTests(() -> dao.findUserBookmarksByCriteria(null),
                BookmarkDAO.ErrorKeys.ERROR_GET_BY_BOOKMARK_CRITERIA);
        methodExceptionTests(() -> dao.findAllBookmarksByWorkspaceAndScope(null, null),
                BookmarkDAO.ErrorKeys.ERROR_GET_ALL_BY_WORKSPACE_SCOPE);
        methodExceptionTests(() -> dao.deleteAllByWorkspaceName(null),
                BookmarkDAO.ErrorKeys.ERROR_DELETE_ALL_BY_WORKSPACE);
    }

    void methodExceptionTests(Executable fn, Enum<?> key) {
        var exc = Assertions.assertThrows(DAOException.class, fn);
        Assertions.assertEquals(key, exc.key);
    }
}
