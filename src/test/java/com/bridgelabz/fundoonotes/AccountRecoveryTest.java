package com.bridgelabz.fundoonotes;

import com.bridgelabz.fundoonotes.entity.AccountToken;
import com.bridgelabz.fundoonotes.repository.AccountTokenRepository;
import com.bridgelabz.fundoonotes.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:account_test;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class AccountRecoveryTest {
    @Autowired MockMvc mvc;
    @Autowired AccountTokenRepository tokens;
    @Autowired UserRepository users;

    @Test
    void verifiesEmailAndResetsPasswordWithOneTimeTokens() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name":"Mihir","email":"recover@example.com","password":"password123"}
                        """)).andExpect(status().isCreated());
        String verifyToken = tokens.findAll().stream()
                .filter(token -> token.getPurpose() == AccountToken.Purpose.VERIFY)
                .findFirst().orElseThrow().getValue();
        mvc.perform(post("/api/auth/verify").contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"" + verifyToken + "\"}"))
                .andExpect(status().isNoContent());
        assertTrue(users.findByEmail("recover@example.com").orElseThrow().isVerified());
        mvc.perform(post("/api/auth/verify").contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"" + verifyToken + "\"}"))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/api/auth/forgot-password").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"recover@example.com\"}"))
                .andExpect(status().isAccepted());
        String resetToken = tokens.findAll().stream()
                .filter(token -> token.getPurpose() == AccountToken.Purpose.RESET)
                .findFirst().orElseThrow().getValue();
        mvc.perform(post("/api/auth/reset-password").contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"" + resetToken + "\",\"password\":\"newpassword123\"}"))
                .andExpect(status().isNoContent());
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"recover@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"recover@example.com\",\"password\":\"newpassword123\"}"))
                .andExpect(status().isOk());
    }
}
