package com.bridgelabz.fundoonotes;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:notes_test;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class NoteCrudTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test
    void noteCrudIsLimitedToItsOwner() throws Exception {
        String alice = registerAndLogin("alice@example.com");
        String bob = registerAndLogin("bob@example.com");

        mvc.perform(post("/api/notes").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Private\"}"))
                .andExpect(status().isUnauthorized());
        String created = mvc.perform(post("/api/notes").header("Authorization", alice)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Private\",\"description\":\"Hello\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = json.readTree(created).get("id").asLong();

        mvc.perform(get("/api/notes/{id}", id).header("Authorization", bob))
                .andExpect(status().isNotFound());
        mvc.perform(put("/api/notes/{id}", id).header("Authorization", alice)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Changed\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Changed"));
        mvc.perform(delete("/api/notes/{id}", id).header("Authorization", alice))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/notes/{id}", id).header("Authorization", alice))
                .andExpect(status().isNotFound());
    }

    @Test
    void noteCanBePinnedArchivedTrashedRestoredAndPermanentlyDeleted() throws Exception {
        String user = registerAndLogin("lifecycle@example.com");
        String created = mvc.perform(post("/api/notes").header("Authorization", user)
                .contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Plan\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = json.readTree(created).get("id").asLong();

        mvc.perform(patch("/api/notes/{id}/pin", id).header("Authorization", user)
                .contentType(MediaType.APPLICATION_JSON).content("{\"enabled\":true}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.pinned").value(true));
        mvc.perform(patch("/api/notes/{id}/archive", id).header("Authorization", user)
                .contentType(MediaType.APPLICATION_JSON).content("{\"enabled\":true}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.archived").value(true));
        mvc.perform(delete("/api/notes/{id}", id).header("Authorization", user))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/notes/trash").header("Authorization", user))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(id));
        mvc.perform(patch("/api/notes/{id}/trash", id).header("Authorization", user)
                .contentType(MediaType.APPLICATION_JSON).content("{\"enabled\":false}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.trashed").value(false));
        mvc.perform(get("/api/notes/{id}", id).header("Authorization", user))
                .andExpect(status().isOk());
        mvc.perform(delete("/api/notes/{id}", id).header("Authorization", user))
                .andExpect(status().isNoContent());
        mvc.perform(delete("/api/notes/{id}/permanent", id).header("Authorization", user))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/notes/trash").header("Authorization", user))
                .andExpect(status().isOk()).andExpect(jsonPath("$").isEmpty());
    }

    private String registerAndLogin(String email) throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Student\",\"email\":\"" + email + "\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated());
        String response = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"password123\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return "Bearer " + json.readTree(response).get("token").asText();
    }
}
