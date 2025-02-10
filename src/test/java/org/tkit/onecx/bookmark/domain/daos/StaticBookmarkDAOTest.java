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
public class StaticBookmarkDAOTest {
    @Inject
    StaticBookmarkDAO dao;

    @InjectMock
    EntityManager em;

    @BeforeEach
    void beforeAll() {
        Mockito.when(em.getCriteriaBuilder()).thenThrow(new RuntimeException("Test technical error exception"));
    }

    @Test
    void findTenantIdByOrgIdExceptionTest() {
        methodExceptionTests(() -> dao.findStaticBookmarksByCriteria(null));
    }

    void methodExceptionTests(Executable fn) {
        var exc = Assertions.assertThrows(DAOException.class, fn);
        Assertions.assertEquals(StaticBookmarkDAO.ErrorKeys.ERROR_GET_BY_STATIC_BOOKMARK_CRITERIA, exc.key);
    }
}
