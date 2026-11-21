package com.bridgelabz.fundoonotes.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class NoteActivityAspect {
    private static final Logger log = LoggerFactory.getLogger(NoteActivityAspect.class);

    @Around("execution(public * com.bridgelabz.fundoonotes.service.NoteService.*(..))")
    public Object timeNoteService(ProceedingJoinPoint call) throws Throwable {
        long started = System.nanoTime();
        try {
            return call.proceed();
        } finally {
            long milliseconds = (System.nanoTime() - started) / 1_000_000;
            log.info("NoteService.{} took {} ms", call.getSignature().getName(), milliseconds);
        }
    }

    @AfterReturning("execution(* com.bridgelabz.fundoonotes.service.NoteService.create(..))"
            + " || execution(* com.bridgelabz.fundoonotes.service.NoteService.update(..))"
            + " || execution(* com.bridgelabz.fundoonotes.service.NoteService.delete(..))")
    public void noteChanged() {
        log.info("A note was created, updated, or moved to Trash");
    }
}
