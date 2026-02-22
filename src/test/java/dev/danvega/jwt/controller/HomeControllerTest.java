// src/test/java/dev/danvega/jwt/controller/HomeControllerTest.java
package dev.danvega.jwt.controller;

import dev.danvega.jwt.config.SecurityConfig;
import dev.danvega.jwt.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({HomeController.class, AuthController.class})
@Import({SecurityConfig.class, TokenService.class})
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rootUnauthenticatedThen401() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    void rootWhenAuthenticatedThenSaysHelloUser() throws Exception {
        // get token from /token endpoint (test stub)
        String token = mockMvc.perform(post("/token")
                        .with(httpBasic("dvega", "password")))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(get("/")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, dvega"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "MockUser")
    void rootWithMockUserStatusIsOk() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello, MockUser")))
                .andDo(print());
    }

}