package dev.danvega.jwt.controller;

import dev.danvega.jwt.config.SecurityConfig;
import dev.danvega.jwt.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.ExchangeResult;
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
        ExchangeResult exchangeResult = RestTestClient.bindTo(mockMvc).build()
                .get().uri("/")
                .exchange()
                .expectStatus().isUnauthorized()
                .returnResult();

        HttpStatusCode status = exchangeResult.getStatus();
        System.out.println("Status code: " + status);
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

        String responseBody = client.get().uri("/")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello, dvega")
                .returnResult().getResponseBody();

        assertThat(responseBody).isEqualTo("Hello, dvega");
        System.out.println("Response body: " + responseBody);
    }

    @Test
    @WithMockUser(username = "MockUser")
    void rootWithMockUserStatusIsOk() {
        RestTestClient.bindTo(mockMvc).build()
                .get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Hello, MockUser"))
                .consumeWith(exchangeResult -> {
                    String responseBody = exchangeResult.getResponseBody();
                    System.out.println("Response body: " + responseBody);
                    System.out.println("Status code: " + exchangeResult.getStatus());
                });
    }
}