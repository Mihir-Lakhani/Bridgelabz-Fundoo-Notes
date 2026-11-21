package com.bridgelabz.fundoonotes.batch;

import com.bridgelabz.fundoonotes.service.NoteCsvExportService;
import java.nio.file.Path;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class NoteExportBatchConfig {
    @Bean
    public Step noteExportStep(JobRepository jobs, PlatformTransactionManager transactions,
                               NoteCsvExportService exporter) {
        return new StepBuilder("noteExportStep", jobs)
                .tasklet((contribution, context) -> {
                    var parameters = contribution.getStepExecution().getJobParameters();
                    exporter.exportFile(Path.of(parameters.getString("file")), parameters.getString("email"));
                    return RepeatStatus.FINISHED;
                }, transactions).build();
    }

    @Bean
    public Job noteExportJob(JobRepository jobs, Step noteExportStep) {
        return new JobBuilder("noteExportJob", jobs).start(noteExportStep).build();
    }
}
