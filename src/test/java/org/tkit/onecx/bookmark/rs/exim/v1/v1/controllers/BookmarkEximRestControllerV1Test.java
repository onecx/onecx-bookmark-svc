package org.tkit.onecx.bookmark.rs.exim.v1.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.bookmark.rs.exim.v1.controllers.BookmarkEximRestController;
import org.tkit.onecx.bookmark.test.AbstractTest;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.bookmark.rs.exim.v1.model.*;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(BookmarkEximRestController.class)
@WithDBData(value = "data/test-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
@GenerateKeycloakClient(clientName = "testClient", scopes = "ocx-bm:all")
class BookmarkEximRestControllerV1Test extends AbstractTest {

    @Test
    void exportImportSnapshotTest() {
        ExportBookmarksRequestDTOV1 requestDTOV1 = new ExportBookmarksRequestDTOV1();
        requestDTOV1.setWorkspaceName("workspaceName1");
        requestDTOV1.setScopes(List.of(EximBookmarkScopeDTOV1.PRIVATE, EximBookmarkScopeDTOV1.PUBLIC));
        var data = given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org3"))
                .contentType(APPLICATION_JSON)
                .body(requestDTOV1)
                .post("/export")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(BookmarkSnapshotDTOV1.class);

        assertThat(data).isNotNull();

        //add image to snapshot:
        data.getBookmarks().get("PUBLIC").get(0)
                .setImage(new ImageDTOV1().imageData(new byte[] { 1, 2, 3 }).mimeType("image/*"));
        //import and overwrite in same workspace
        given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org3"))
                .contentType(APPLICATION_JSON)
                .body(data)
                .pathParam("workspaceName", "workspaceName1")
                .queryParam("importMode", EximModeDTOV1.OVERWRITE)
                .post("/{workspaceName}/import")
                .then()
                .statusCode(OK.getStatusCode());

        //without overwrite
        given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org3"))
                .contentType(APPLICATION_JSON)
                .body(data)
                .pathParam("workspaceName", "workspaceName1")
                .queryParam("importMode", EximModeDTOV1.APPEND)
                .post("/{workspaceName}/import")
                .then()
                .statusCode(OK.getStatusCode());

        //export only privates
        requestDTOV1.setScopes(List.of(EximBookmarkScopeDTOV1.PRIVATE));
        data = given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org3"))
                .contentType(APPLICATION_JSON)
                .body(requestDTOV1)
                .post("/export")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(BookmarkSnapshotDTOV1.class);

        assertThat(data).isNotNull();

        //export only publics
        requestDTOV1.setScopes(List.of(EximBookmarkScopeDTOV1.PUBLIC));
        data = given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org3"))
                .contentType(APPLICATION_JSON)
                .body(requestDTOV1)
                .post("/export")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(BookmarkSnapshotDTOV1.class);
        System.out.println("PUBLICS " + data);

        assertThat(data).isNotNull();

        data = given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org3"))
                .contentType(APPLICATION_JSON)
                .body(requestDTOV1)
                .post("/export")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(BookmarkSnapshotDTOV1.class);

        assertThat(data).isNotNull();
    }

    @Test
    void importMissingBody() {
        given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("workspaceName", "someWorkspace")
                .post("/{workspaceName}/import")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    void exportSnapshotNoBookmarksTest() {
        ExportBookmarksRequestDTOV1 requestDTOV1 = new ExportBookmarksRequestDTOV1();
        requestDTOV1.setWorkspaceName("workspaceName1234567");
        requestDTOV1.setScopes(List.of(EximBookmarkScopeDTOV1.PRIVATE));
        var data = given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org3"))
                .contentType(APPLICATION_JSON)
                .body(requestDTOV1)
                .post("/export")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(BookmarkSnapshotDTOV1.class);

        assertThat(data).isNotNull();
        assertThat(data.getBookmarks()).isEmpty();
    }
}
