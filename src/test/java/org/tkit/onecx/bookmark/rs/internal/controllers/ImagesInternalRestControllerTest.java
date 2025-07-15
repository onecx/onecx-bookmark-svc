package org.tkit.onecx.bookmark.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.tkit.onecx.bookmark.rs.internal.mappers.ExceptionMapper.ErrorKeys.CONSTRAINT_VIOLATIONS;

import java.io.File;
import java.util.Objects;
import java.util.Random;

import jakarta.ws.rs.core.HttpHeaders;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tkit.onecx.bookmark.test.AbstractTest;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.bookmark.rs.image.internal.model.ImageInfoDTO;
import gen.org.tkit.onecx.bookmark.rs.image.internal.model.ProblemDetailResponseDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(ImagesInternalRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
@GenerateKeycloakClient(clientName = "testClient", scopes = { "ocx-bm:read", "ocx-bm:write", "ocx-bm:delete", "ocx-bm:all" })
class ImagesInternalRestControllerTest extends AbstractTest {

    private static final String MEDIA_TYPE_IMAGE_PNG = "image/png";
    private static final String MEDIA_TYPE_IMAGE_JPG = "image/jpg";

    private static final File FILE = new File(
            Objects.requireNonNull(ImagesInternalRestControllerTest.class.getResource("/images/Testimage.png")).getFile());

    @Test
    void uploadImage() {
        given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .pathParam("refId", "productName")
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(ImageInfoDTO.class);

    }

    @Test
    void uploadImageEmptyBody() {

        var exception = given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .pathParam("refId", "productName")
                .when()
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo(CONSTRAINT_VIOLATIONS.name());
    }

    @Test
    void uploadImage_shouldReturnBadRequest_whenImageIs() {

        var refId = "productNameUpload";

        given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .pathParam("refId", refId)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode());
    }

    @Test
    void getImagePngTest() {

        var refId = "themPngTest";

        given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .pathParam("refId", refId)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode());

        var data = given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("refId", refId)
                .get()
                .then()
                .statusCode(OK.getStatusCode())
                .header(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE_IMAGE_PNG)
                .extract().body().asByteArray();

        assertThat(data).isNotNull().isNotEmpty();
    }

    @Test
    void getImageJpgTest() {

        var refId = "nameJpg";

        given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .pathParam("refId", refId)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_JPG)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode());

        var data = given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("refId", refId)
                .get()
                .then()
                .statusCode(OK.getStatusCode())
                .header(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE_IMAGE_JPG)
                .extract().body().asByteArray();

        assertThat(data).isNotNull().isNotEmpty();
    }

    @Test
    void getImageTest_shouldReturnNotFound_whenImagesDoesNotExist() {

        var refId = "productNameGetTest";

        given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .pathParam("refId", refId)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode());

        given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("refId", refId + "_not_exists")
                .get()
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void updateImage() {

        var refId = "productUpdateTest";

        given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .pathParam("refId", refId)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(ImageInfoDTO.class);

        var res = given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .pathParam("refId", refId)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(ImageInfoDTO.class);

        Assertions.assertNotNull(res);

        given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .pathParam("refId", "does-not-exists")
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode());
    }

    @Test
    void updateImage_create_whenEntryNotExists() {

        var refId = "productNameUpdateFailed";

        given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .pathParam("refId", refId)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(ImageInfoDTO.class);
    }

    @Test
    void testMaxUploadSize() {

        var refId = "productMaxUpload";

        byte[] body = new byte[210001];
        new Random().nextBytes(body);

        var exception = given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .pathParam("refId", refId)
                .when()
                .body(body)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo(CONSTRAINT_VIOLATIONS.name());
    }

    @Test
    void deleteImage() {

        var refId = "productDeleteTest";

        given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .pathParam("refId", refId)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(ImageInfoDTO.class);

        var res = given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .pathParam("refId", refId)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .delete()
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        Assertions.assertNotNull(res);

        given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("refId", refId)
                .get()
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }
}
