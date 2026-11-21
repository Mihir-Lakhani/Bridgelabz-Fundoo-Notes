package com.bridgelabz.fundoonotes;

import com.bridgelabz.fundoonotes.service.NoteService;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:aop_test;DB_CLOSE_DELAY=-1")
class AopConfigTest {
    @Autowired NoteService notes;

    @Test
    void noteServiceIsWrappedByLoggingAspect() {
        assertTrue(AopUtils.isAopProxy(notes));
    }
}
