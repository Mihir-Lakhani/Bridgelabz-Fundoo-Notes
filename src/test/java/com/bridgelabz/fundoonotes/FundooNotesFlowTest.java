package com.bridgelabz.fundoonotes;

import com.bridgelabz.fundoonotes.user.UserRepository;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:fundoo_test;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class FundooNotesFlowTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired UserRepository users;

    @Test
    void registrationLoginAndNoteOwnership() throws Exception {
        register("alice@example.com");
        register("bob@example.com");
        assertNotEquals("password123", users.findByEmail("alice@example.com").orElseThrow().getPassword());

        String aliceToken = login("alice@example.com");
        String bobToken = login("bob@example.com");

        mvc.perform(post("/api/notes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Private note\",\"description\":\"Hello\"}"))
                .andExpect(status().isUnauthorized());

        String created = mvc.perform(post("/api/notes").header("Authorization", "Bearer " + aliceToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Private note\",\"description\":\"Hello\",\"labels\":[\"work\"],\"reminderAt\":\"2026-12-01T09:00:00\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = json.readTree(created).get("id").asLong();

        mvc.perform(get("/api/notes/{id}", id).header("Authorization", "Bearer " + aliceToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Private note"));
        mvc.perform(get("/api/notes/{id}", id).header("Authorization", "Bearer " + bobToken))
                .andExpect(status().isNotFound());
        mvc.perform(get("/api/notes").param("label", "work")
                        .header("Authorization", "Bearer " + aliceToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].title").value("Private note"));
    }

    private void register(String email) throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Student\",\"email\":\"" + email + "\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated());
    }

    private String login(String email) throws Exception {
        String response = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"password123\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return json.readTree(response).get("token").asText();
    }
}
