package dev.danvega.jwt.controller;

import dev.danvega.jwt.config.SecurityConfig;
import dev.danvega.jwt.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest({HomeController.class, AuthController.class})
@Import({SecurityConfig.class, TokenService.class})
class HomeControllerRestTestClientTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rootUnauthenticatedThen401() {
        RestTestClient.bindTo(mockMvc).build()
                .get().uri("/")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void rootWhenAuthenticatedThenSaysHelloUser() {
        RestTestClient client = RestTestClient.bindTo(mockMvc).build();
        String credentials = Base64.getEncoder().encodeToString("dvega:password".getBytes());

        String token = client.post().uri("/token")
                .header("Authorization", "Basic " + credentials)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).returnResult().getResponseBody();

        client.get().uri("/")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello, dvega");
    }

    @Test
    @WithMockUser(username = "dvega")
    void rootWithMockUserStatusIsOk() {
        RestTestClient.bindTo(mockMvc).build()
                .get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Hello, dvega"));
    }
}