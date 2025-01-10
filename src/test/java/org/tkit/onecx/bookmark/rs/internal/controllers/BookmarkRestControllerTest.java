package org.tkit.onecx.bookmark.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.tkit.quarkus.security.test.SecurityTestUtils.getKeycloakClientToken;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tkit.onecx.bookmark.test.AbstractTest;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.bookmark.rs.internal.model.*;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(BookmarkRestController.class)
@WithDBData(value = "data/test-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
@GenerateKeycloakClient(clientName = "testClient", scopes = "ocx-bm:all")
public class BookmarkRestControllerTest extends AbstractTest {

    @Test
    void createBookmark() {

        CreateBookmarkDTO createBookmarkDTO = new CreateBookmarkDTO();
        createBookmarkDTO.setDisplayName("newDisplayName");
        createBookmarkDTO.setAppId("newAppId");
        createBookmarkDTO.setProductName("newProduct");
        createBookmarkDTO.setWorkspaceName("newWorkspace");
        createBookmarkDTO.setEndpointName("newEndpoint");
        createBookmarkDTO.setScope(CreateBookmarkDTO.ScopeEnum.PUBLIC);
        createBookmarkDTO.setPosition(1);

        var res = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .body(createBookmarkDTO)
                .post()
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode());

        assertThat(res).isNotNull();

        BookmarkSearchCriteriaDTO bookmarkSearchCriteriaDTO = new BookmarkSearchCriteriaDTO();
        bookmarkSearchCriteriaDTO.setWorkspaceName("newWorkspace");

        var dto = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .body(bookmarkSearchCriteriaDTO)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(BookmarkPageResultDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getStream()).isNotNull();
        assertThat(dto.getStream().size()).isEqualTo(1);
        assertThat(dto.getStream().get(0).getEndpointParameters()).isNull();
    }

    @Test
    void updateBookmark() {

        UpdateBookmarkDTO updateBookmarkDTO = new UpdateBookmarkDTO();
        updateBookmarkDTO.setDisplayName("newDisplayName");
        updateBookmarkDTO.setModificationCount(0);
        updateBookmarkDTO.setPosition(2);

        given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .body(updateBookmarkDTO)
                .pathParam("id", "11-111")
                .header(APM_HEADER_PARAM, createToken("org3"))
                .put("{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        BookmarkSearchCriteriaDTO bookmarkSearchCriteriaDTO = new BookmarkSearchCriteriaDTO();
        bookmarkSearchCriteriaDTO.setWorkspaceName("workspaceName1");

        var res = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org3"))
                .body(bookmarkSearchCriteriaDTO)
                .post("/user")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(BookmarkPageResultDTO.class);

        Assertions.assertEquals("newDisplayName", res.getStream().get(0).getDisplayName());

        UpdateBookmarkDTO updateOldBookmarkDTO = new UpdateBookmarkDTO();
        updateOldBookmarkDTO.setDisplayName("shouldNotBeUpdated");
        updateOldBookmarkDTO.setScope(UpdateBookmarkDTO.ScopeEnum.PRIVATE);
        updateOldBookmarkDTO.setPosition(1);
        updateOldBookmarkDTO.setModificationCount(12);

        // update Slot with old modificationCount
        given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .body(updateOldBookmarkDTO)
                .pathParam("id", "11-111")
                .header(APM_HEADER_PARAM, createToken("org3"))
                .put("{id}")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

        var res2 = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org3"))
                .body(bookmarkSearchCriteriaDTO)
                .post("/user")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(BookmarkPageResultDTO.class);

        Assertions.assertEquals("newDisplayName", res2.getStream().get(0).getDisplayName());

        //update to public
        UpdateBookmarkDTO updateToPublic = new UpdateBookmarkDTO();
        updateToPublic.setDisplayName("madePublic");
        updateToPublic.setPosition(1);
        updateToPublic.setModificationCount(res2.getStream().get(0).getModificationCount());
        updateToPublic.setScope(UpdateBookmarkDTO.ScopeEnum.PUBLIC);

        given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .body(updateToPublic)
                .pathParam("id", "11-111")
                .header(APM_HEADER_PARAM, createToken("org3"))
                .put("{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        var afterPublicUpdate = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org3"))
                .body(bookmarkSearchCriteriaDTO)
                .post("/user")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(BookmarkPageResultDTO.class);

        Assertions.assertTrue(afterPublicUpdate.getStream().isEmpty());

    }

    @Test
    void updateBookmark_shouldNotFindUpdateBookmark() {

        UpdateBookmarkDTO updateBookmarkDTO = new UpdateBookmarkDTO();
        updateBookmarkDTO.setDisplayName("newDisplayName");
        updateBookmarkDTO.setModificationCount(1);
        updateBookmarkDTO.setPosition(1);

        given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .body(updateBookmarkDTO)
                .pathParam("id", "not_exist")
                .header(APM_HEADER_PARAM, createToken("org3"))
                .put("{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

    }

    @Test
    void searchPublicBookmarksByCriteria() {

        BookmarkSearchCriteriaDTO bookmarkSearchCriteriaDTO = new BookmarkSearchCriteriaDTO();
        bookmarkSearchCriteriaDTO.setWorkspaceName("workspaceName1");
        bookmarkSearchCriteriaDTO.setProductName("someProductName");

        var data = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org3"))
                .body(bookmarkSearchCriteriaDTO)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode()).extract().as(BookmarkPageResultDTO.class);

        assertThat(data.getStream().toArray()).isEmpty();

        BookmarkSearchCriteriaDTO bookmarkSearchCriteriaDTO2 = new BookmarkSearchCriteriaDTO();

        bookmarkSearchCriteriaDTO2.setWorkspaceName("workspaceName1");
        data = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org3"))
                .body(bookmarkSearchCriteriaDTO2)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(BookmarkPageResultDTO.class);

        assertThat(2).isEqualTo(data.getStream().size());
    }

    @Test
    void searchUserBookmarksByCriteria() {

        BookmarkSearchCriteriaDTO bookmarkSearchCriteriaDTO = new BookmarkSearchCriteriaDTO();
        bookmarkSearchCriteriaDTO.setWorkspaceName("workspaceName1");
        bookmarkSearchCriteriaDTO.setProductName("someProductName");

        var data = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org3"))
                .body(bookmarkSearchCriteriaDTO)
                .post("/user")
                .then()
                .statusCode(OK.getStatusCode()).extract().as(BookmarkPageResultDTO.class);

        assertThat(data.getStream().toArray()).isEmpty();

        BookmarkSearchCriteriaDTO bookmarkSearchCriteriaDTO2 = new BookmarkSearchCriteriaDTO();

        bookmarkSearchCriteriaDTO2.setWorkspaceName("workspaceName1");
        data = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org3"))
                .body(bookmarkSearchCriteriaDTO2)
                .post("/user")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(BookmarkPageResultDTO.class);

        assertThat(1).isEqualTo(data.getStream().size());
    }

    @Test
    void deleteBookmark() {

        var res = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("id", "11-111")
                .header(APM_HEADER_PARAM, createToken("org3"))
                .delete("{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        assertThat(res).isNotNull();

        BookmarkSearchCriteriaDTO bookmarkSearchCriteriaDTO = new BookmarkSearchCriteriaDTO();
        bookmarkSearchCriteriaDTO.setWorkspaceName("workspaceName1");

        var output = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org3"))
                .body(bookmarkSearchCriteriaDTO)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(BookmarkPageResultDTO.class);

        assertThat(output).isNotNull();
        assertThat(output.getStream()).isNotNull();
        assertThat(output.getStream().isEmpty());
    }

}
