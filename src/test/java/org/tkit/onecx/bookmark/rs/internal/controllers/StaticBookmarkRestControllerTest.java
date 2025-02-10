package org.tkit.onecx.bookmark.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.tkit.quarkus.security.test.SecurityTestUtils.getKeycloakClientToken;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.bookmark.test.AbstractTest;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.bookmark.rs.internal.model.CreateStaticBookmarkDTO;
import gen.org.tkit.onecx.bookmark.rs.internal.model.StaticBookmarkPageResultDTO;
import gen.org.tkit.onecx.bookmark.rs.internal.model.StaticBookmarkSearchCriteriaDTO;
import gen.org.tkit.onecx.bookmark.rs.internal.model.UpdateStaticBookmarkDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(StaticBookmarkRestController.class)
@WithDBData(value = "data/test-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
@GenerateKeycloakClient(clientName = "testClient", scopes = "ocx-bm:all")
public class StaticBookmarkRestControllerTest extends AbstractTest {

    @Test
    void createStaticBookmark() {

        CreateStaticBookmarkDTO createStaticBookmarkDTO = new CreateStaticBookmarkDTO();
        createStaticBookmarkDTO.setDisplayName("newDisplayName");
        createStaticBookmarkDTO.setWorkspaceName("newWorkspace");
        createStaticBookmarkDTO.setPosition(1);

        var res = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .body(createStaticBookmarkDTO)
                .post()
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode());

        assertThat(res).isNotNull();

        StaticBookmarkSearchCriteriaDTO staticBookmarkSearchCriteriaDTO = new StaticBookmarkSearchCriteriaDTO();
        staticBookmarkSearchCriteriaDTO.setWorkspaceName("newWorkspace");

        var dto = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .body(staticBookmarkSearchCriteriaDTO)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(StaticBookmarkPageResultDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getStream()).isNotNull();
        assertThat(dto.getStream().size()).isEqualTo(1);
    }

    @Test
    void updateStaticBookmark() {

        UpdateStaticBookmarkDTO updateStaticBookmarkDTO = new UpdateStaticBookmarkDTO();
        updateStaticBookmarkDTO.setDisplayName("newDisplayName");
        updateStaticBookmarkDTO.setModificationCount(0);
        updateStaticBookmarkDTO.setPosition(2);

        given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .body(updateStaticBookmarkDTO)
                .pathParam("id", "11-111")
                .header(APM_HEADER_PARAM, createToken("org3"))
                .put("{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
        UpdateStaticBookmarkDTO updateOldStaticBookmarkDTO = new UpdateStaticBookmarkDTO();
        updateOldStaticBookmarkDTO.setDisplayName("shouldNotBeUpdated");
        updateOldStaticBookmarkDTO.setPosition(1);
        updateOldStaticBookmarkDTO.setModificationCount(12);

        // update static bookmark with old modificationCount
        given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .body(updateOldStaticBookmarkDTO)
                .pathParam("id", "11-111")
                .header(APM_HEADER_PARAM, createToken("org3"))
                .put("{id}")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    void updateStaticBookmark_shouldNotFindUpdateStaticBookmark() {

        UpdateStaticBookmarkDTO updateStaticBookmarkDTO = new UpdateStaticBookmarkDTO();
        updateStaticBookmarkDTO.setDisplayName("newDisplayName");
        updateStaticBookmarkDTO.setModificationCount(1);
        updateStaticBookmarkDTO.setPosition(1);

        given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .body(updateStaticBookmarkDTO)
                .pathParam("id", "not_exist")
                .header(APM_HEADER_PARAM, createToken("org3"))
                .put("{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

    }

    @Test
    void searchStaticBookmarksByCriteria() {

        StaticBookmarkSearchCriteriaDTO staticBookmarkSearchCriteriaDTO = new StaticBookmarkSearchCriteriaDTO();

        staticBookmarkSearchCriteriaDTO.setWorkspaceName("workspaceName1");
        var data = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org3"))
                .body(staticBookmarkSearchCriteriaDTO)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(StaticBookmarkPageResultDTO.class);

        assertThat(2).isEqualTo(data.getStream().size());
    }

    @Test
    void deleteStaticBookmark() {

        var res = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("id", "11-111")
                .header(APM_HEADER_PARAM, createToken("org3"))
                .delete("{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        assertThat(res).isNotNull();

        StaticBookmarkSearchCriteriaDTO staticBookmarkSearchCriteriaDTO = new StaticBookmarkSearchCriteriaDTO();
        staticBookmarkSearchCriteriaDTO.setWorkspaceName("workspaceName1");

        var output = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org3"))
                .body(staticBookmarkSearchCriteriaDTO)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(StaticBookmarkPageResultDTO.class);

        assertThat(output).isNotNull();
        assertThat(output.getStream()).isNotNull();
        assertThat(output.getStream().size()).isEqualTo(1);
    }

}
