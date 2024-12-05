@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessedData {
    private String restData;
    private String db1Data;
    private String db2Data;
}

@Component
@RequiredArgsConstructor
public class DataReader implements ItemReader<Data> {
    private final WebClient webClient;
    private final JdbcTemplate primaryJdbcTemplate;
    private final JdbcTemplate secondaryJdbcTemplate;
    private boolean dataFetched = false;
    private Data data;

    @Override
    public Data read() {
        if (dataFetched) {
            return null;
        }

        // Fetch REST API data
        String restData = webClient.get()
                .uri("/api/endpoint")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Query first database
        String db1Data = primaryJdbcTemplate.queryForObject(
                "SELECT data FROM table1 WHERE condition = ?",
                String.class,
                "someValue"
        );

        // Query second database
        String db2Data = secondaryJdbcTemplate.queryForObject(
                "SELECT data FROM table2 WHERE condition = ?",
                String.class,
                "someValue"
        );

        data = new Data(restData, db1Data, db2Data);
        dataFetched = true;
        return data;
    }
}

@Component
public class DataProcessor implements ItemProcessor<Data, ProcessedData> {
    @Override
    public ProcessedData process(Data data) {
        // Add any processing logic here
        return new ProcessedData(
            data.getRestData(),
            data.getDb1Data(),
            data.getDb2Data()
        );
    }
}

@Component
@RequiredArgsConstructor
public class ExcelWriter implements ItemWriter<ProcessedData> {
    @Value("${output.directory}")
    private String outputDirectory;

    @Override
    public void write(List<? extends ProcessedData> items) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        // Create headers
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("REST Data");
        headerRow.createCell(1).setCellValue("DB1 Data");
        headerRow.createCell(2).setCellValue("DB2 Data");

        // Write data
        int rowNum = 1;
        for (ProcessedData item : items) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getRestData());
            row.createCell(1).setCellValue(item.getDb1Data());
            row.createCell(2).setCellValue(item.getDb2Data());
        }

        // Save the workbook
        String fileName = "output_" + System.currentTimeMillis() + ".xlsx";
        try (FileOutputStream fos = new FileOutputStream(outputDirectory + "/" + fileName)) {
            workbook.write(fos);
        }
        workbook.close();
    }
}
