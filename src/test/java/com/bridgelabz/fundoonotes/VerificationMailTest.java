package com.bridgelabz.fundoonotes;

import com.bridgelabz.fundoonotes.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:verification_mail_test;DB_CLOSE_DELAY=-1",
        "fundoo.mail.log-only=false"
})
@AutoConfigureMockMvc
class VerificationMailTest {
    @Autowired MockMvc mvc;
    @Autowired UserRepository users;

    @Test
    void registrationFailsWithoutEmailDeliveryAndDoesNotSaveTheUser() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name":"Mihir","email":"no-mail@example.com","password":"password123"}
                        """))
                .andExpect(status().isServiceUnavailable());
        assertFalse(users.existsByEmail("no-mail@example.com"));
    }
}
