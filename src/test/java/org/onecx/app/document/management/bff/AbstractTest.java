package org.onecx.app.document.management.bff;

import java.security.PrivateKey;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.jwt.Claims;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.quarkiverse.mockserver.test.MockServerTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.util.KeyUtils;

@QuarkusTestResource(MockServerTestResource.class)
public abstract class AbstractTest {

    protected static final String ADMIN = "alice";

    public KeycloakTestClient keycloakClient = new KeycloakTestClient();

    protected static final String APM_HEADER_PARAM = ConfigProvider.getConfig()
            .getValue("%test.tkit.rs.context.token.header-param", String.class);

    protected static String createToken(String userId, String orgId) {
        return createToken(userId, orgId, null, null);
    }

    protected static String createToken(String userId, String orgId, String claimName, Object value) {
        try {
            String userName = userId != null ? userId : "test-user";
            String organizationId = orgId != null ? orgId : "org1";
            JsonObjectBuilder claims = createClaims(userName, organizationId, claimName, value);

            PrivateKey privateKey = KeyUtils.generateKeyPair(2048).getPrivate();
            return Jwt.claims(claims.build()).sign(privateKey);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected static JsonObjectBuilder createClaims(String userName, String orgId, String claimName, Object claimValue) {
        JsonObjectBuilder claims = Json.createObjectBuilder();
        claims.add(Claims.preferred_username.name(), userName);
        claims.add(Claims.sub.name(), userName);
        claims.add(Claims.iss.name(), "testIssuer");
        claims.add("orgId", orgId);
        claims.add("name", "Given Family " + userName);
        claims.add(Claims.given_name.name(), "Given " + userName);
        claims.add(Claims.family_name.name(), "Family " + userName);
        claims.add(Claims.email.name(), userName + "@testOrg.de");

        if (claimName != null && claimValue != null) {
            if (claimValue instanceof Integer t) {
                claims.add(claimName, Json.createValue(t));
            } else if (claimValue instanceof Double t) {
                claims.add(claimName, Json.createValue(t));
            } else if (claimValue instanceof String t) {
                claims.add(claimName, Json.createValue(t));
            }
        }

        return claims;
    }

    static {
        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
                ObjectMapperConfig.objectMapperConfig().jackson2ObjectMapperFactory(
                        (cls, charset) -> {
                            ObjectMapper objectMapper = new ObjectMapper();
                            objectMapper.registerModule(new JavaTimeModule());
                            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
                            return objectMapper;
                        }));
    }

}
