package org.tkit.onecx.bookmark.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.Response.Status.*;

import java.io.File;
import java.util.Objects;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.tkit.onecx.bookmark.test.AbstractTest;
import org.tkit.onecx.quarkus.parameter.config.ParametersConfig;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;

import gen.org.tkit.onecx.bookmark.rs.image.internal.model.ProblemDetailResponseDTO;
import io.quarkus.test.InjectMock;
import io.quarkus.test.Mock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.config.SmallRyeConfig;

@QuarkusTest
@TestHTTPEndpoint(ImagesInternalRestController.class)
@GenerateKeycloakClient(clientName = "testClient", scopes = { "ocx-bm:read", "ocx-bm:write", "ocx-bm:delete", "ocx-bm:all" })
class ParameterConvertValueTest extends AbstractTest {
    @InjectMock
    ParametersConfig parametersConfig;

    @Inject
    Config config;

    public static class ConfigProducer {

        @Inject
        Config config;

        @Produces
        @ApplicationScoped
        @Mock
        ParametersConfig config() {
            return config.unwrap(SmallRyeConfig.class).getConfigMapping(ParametersConfig.class);
        }
    }

    @BeforeEach
    void enableParameters() {
        var tmp = config.unwrap(SmallRyeConfig.class).getConfigMapping(ParametersConfig.class);

        Mockito.when(parametersConfig.enabled()).thenReturn(true);
        Mockito.when(parametersConfig.applicationId()).thenReturn("onecx-bookmark-svc");
        Mockito.when(parametersConfig.productName()).thenReturn("onecx-bookmark");
        Mockito.when(parametersConfig.clientUrl()).thenReturn(tmp.clientUrl());
        Mockito.when(parametersConfig.tenant()).thenReturn(() -> false);
        Mockito.when(parametersConfig.cache()).thenReturn(new ParametersConfig.CacheConfig() {
            @Override
            public boolean enabled() {
                return false;
            }

            @Override
            public Long updateSchedule() {
                return 0L;
            }

            @Override
            public boolean updateAtStart() {
                return false;
            }

            @Override
            public boolean failedAtStart() {
                return false;
            }
        });
        Mockito.when(parametersConfig.history()).thenReturn(new ParametersConfig.HistoryConfig() {
            @Override
            public boolean enabled() {
                return false;
            }

            @Override
            public Long updateSchedule() {
                return 0L;
            }
        });

    }

    private static final String MEDIA_TYPE_IMAGE_PNG = "image/png";

    private static final File FILE = new File(
            Objects.requireNonNull(ParameterConvertValueTest.class.getResource("/images/Testimage.png")).getFile());

    @Test
    void uploadImageConvertValueException() {
        given()
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .pathParam("refId", "productName")
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode()).extract().as(ProblemDetailResponseDTO.class);
    }
}
