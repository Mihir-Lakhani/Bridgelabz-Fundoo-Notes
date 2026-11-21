package com.bridgelabz.fundoonotes.batch;

import com.bridgelabz.fundoonotes.service.NoteCsvImportService;
import java.nio.file.Path;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class NoteImportBatchConfig {
    @Bean
    public Step noteImportStep(JobRepository jobs, PlatformTransactionManager transactions,
                               NoteCsvImportService importer) {
        return new StepBuilder("noteImportStep", jobs)
                .tasklet((contribution, context) -> {
                    var parameters = contribution.getStepExecution().getJobParameters();
                    importer.importFile(Path.of(parameters.getString("file")), parameters.getString("email"));
                    return RepeatStatus.FINISHED;
                }, transactions).build();
    }

    @Bean
    public Job noteImportJob(JobRepository jobs, Step noteImportStep) {
        return new JobBuilder("noteImportJob", jobs).start(noteImportStep).build();
    }
}
