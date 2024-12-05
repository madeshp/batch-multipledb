@Configuration
@EnableBatchProcessing
@EnableAsync
public class BatchConfig {
    
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("${api.backend.url}")
                .build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.secondary")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public JdbcTemplate primaryJdbcTemplate(@Qualifier("primaryDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public JdbcTemplate secondaryJdbcTemplate(@Qualifier("secondaryDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public Job dataProcessingJob(Step processDataStep) {
        return jobBuilderFactory.get("dataProcessingJob")
                .incrementer(new RunIdIncrementer())
                .flow(processDataStep)
                .end()
                .build();
    }

    @Bean
    public Step processDataStep(ItemReader<Data> reader, 
                              ItemProcessor<Data, ProcessedData> processor,
                              ItemWriter<ProcessedData> writer) {
        return stepBuilderFactory.get("processDataStep")
                .<Data, ProcessedData>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
