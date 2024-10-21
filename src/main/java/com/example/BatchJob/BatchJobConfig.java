package com.example.BatchJob;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

//@EnableBatchProcessing
@Configuration
@EnableScheduling

public class BatchJobConfig {

	@Bean
	public DataSource dataSource() {
	    DriverManagerDataSource dataSource = new DriverManagerDataSource();
	    dataSource.setDriverClassName("org.h2.Driver");  // Adjust this based on your database
	    dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
	    dataSource.setUsername("sa");
	    dataSource.setPassword(""); // Adjust this based on your configuration
	    return dataSource;
	}


    @Bean
    public JobRepository jobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource());
        factory.setTransactionManager(transactionManager()); // Updated transaction manager
        factory.setIsolationLevelForCreate("ISOLATION_SERIALIZABLE");
        factory.setTablePrefix("BATCH_"); // Ensure your tables use this prefix
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    // Using DataSourceTransactionManager instead of ResourcelessTransactionManager
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource()); // Use DataSourceTransactionManager
    }

    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        return jobLauncher;
    }

    @Bean
    public Job processJob(JobRepository jobRepository, Step processStep) {
        return new JobBuilderFactory(jobRepository)
                .get("processJob")
                .start(processStep)
                .build();
    }

    @Bean
    public Step processStep(JobRepository jobRepository, ItemReader<OriginalData> csvReader,
                            ItemProcessor<OriginalData, ProcessedData> csvProcessor,
                            ItemWriter<ProcessedData> csvWriter) {
        return new StepBuilderFactory(jobRepository)
                .get("processStep")
                .<OriginalData, ProcessedData>chunk(10)
                .reader(csvReader)
                .processor(csvProcessor)
                .writer(csvWriter)
                .transactionManager(transactionManager())
                .build();
    }

    @Bean
    public ItemReader<OriginalData> csvReader() throws Exception {
        return new CsvReader(); // Ensure CsvReader implementation is correct
    }

    @Bean
    public ItemProcessor<OriginalData, ProcessedData> csvProcessor() {
        return new CsvProcessor(); // Ensure CsvProcessor implementation is correct
    }

    @Bean
    public ItemWriter<ProcessedData> csvWriter() {
        return new CsvWriter(); // Ensure CsvWriter implementation is correct
    }
    @Scheduled(fixedRate = 600000) // Adjust the rate as needed (in milliseconds)
    public void scheduleJob() {
        try {
            JobLauncher jobLauncher = jobLauncher(jobRepository());
            jobLauncher.run(processJob(jobRepository(), processStep(jobRepository(), csvReader(), csvProcessor(), csvWriter())), new JobParameters());
            System.out.println("Batch job has been executed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
