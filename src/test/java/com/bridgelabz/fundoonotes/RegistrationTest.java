package com.bridgelabz.fundoonotes;

import com.bridgelabz.fundoonotes.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:registration_test;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class RegistrationTest {
    @Autowired MockMvc mvc;
    @Autowired UserRepository users;

    @Test
    void registrationStoresEncodedPasswordAndRejectsDuplicateEmail() throws Exception {
        String body = """
                {"name":"Mihir","email":"mihir@example.com","password":"password123"}
                """;
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        assertNotEquals("password123", users.findByEmail("mihir@example.com").orElseThrow().getPassword());
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
