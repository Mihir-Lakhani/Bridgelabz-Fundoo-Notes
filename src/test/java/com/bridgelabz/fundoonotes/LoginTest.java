package com.bridgelabz.fundoonotes;

import com.bridgelabz.fundoonotes.entity.AccountToken;
import com.bridgelabz.fundoonotes.repository.AccountTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:login_test;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class LoginTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired JwtDecoder jwtDecoder;
    @Autowired AccountTokenRepository tokens;

    @Test
    void loginReturnsSignedTokenAndRejectsWrongPassword() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name":"Mihir","email":"login@example.com","password":"password123"}
                        """)).andExpect(status().isCreated());

        String credentials = """
                {"email":"login@example.com","password":"password123"}
                """;
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(credentials)).andExpect(status().isForbidden());
        String verifyToken = tokens.findAll().stream()
                .filter(token -> token.getPurpose() == AccountToken.Purpose.VERIFY)
                .findFirst().orElseThrow().getValue();
        mvc.perform(post("/api/auth/verify").contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"" + verifyToken + "\"}"))
                .andExpect(status().isNoContent());

        String response = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(credentials)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String token = json.readTree(response).get("token").asText();
        assertEquals("login@example.com", jwtDecoder.decode(token).getSubject());

        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"login@example.com","password":"wrong-password"}
                        """)).andExpect(status().isUnauthorized());
    }
}
