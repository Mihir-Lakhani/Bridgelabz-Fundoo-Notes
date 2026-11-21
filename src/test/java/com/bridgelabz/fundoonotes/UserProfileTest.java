package com.bridgelabz.fundoonotes;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:profile_test;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class UserProfileTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test
    void onlyAuthenticatedUserCanReadAndUpdateOwnProfile() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name":"First","email":"profile@example.com","password":"password123"}
                        """)).andExpect(status().isCreated());
        String loginResponse = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"profile@example.com","password":"password123"}
                        """)).andReturn().getResponse().getContentAsString();
        String bearer = "Bearer " + json.readTree(loginResponse).get("token").asText();

        mvc.perform(get("/api/users/me")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/users/me").header("Authorization", bearer))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("First"));
        mvc.perform(put("/api/users/me").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON).content("""
                        {"name":"Updated"}
                        """)).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Updated"));
    }
}
