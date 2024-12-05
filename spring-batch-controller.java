@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchController {
    private final JobLauncher jobLauncher;
    private final Job dataProcessingJob;

    @PostMapping("/start")
    public ResponseEntity<String> startJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("JobID", String.valueOf(System.currentTimeMillis()))
                    .toJobParameters();
            
            // Launch the job asynchronously
            CompletableFuture.runAsync(() -> {
                try {
                    jobLauncher.run(dataProcessingJob, jobParameters);
                } catch (Exception e) {
                    log.error("Error running job", e);
                }
            });

            return ResponseEntity.ok("Job started successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error starting job: " + e.getMessage());
        }
    }
}
