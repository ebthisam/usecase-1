package com.example.BatchJob;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class CsvWriter implements ItemWriter<ProcessedData> {

    private static final Logger logger = LoggerFactory.getLogger(CsvWriter.class);
    private static final String OUTPUT_FILE_PATH = "C:/Users/284784/eclipse-workspace/BatchJob/output.csv";
    @Override
    public void write(Chunk<? extends ProcessedData> items) throws IOException {
        System.out.println("Starting to write to CSV file...");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH, false));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Column1", "Column2"))) {

            for (ProcessedData item : items) {
                logger.info("Writing to file: {} , {}", item.getProcessedField1(), item.getProcessedField2());
                System.out.println("Writing record: " + item.getProcessedField1() + ", " + item.getProcessedField2());
                csvPrinter.printRecord(item.getProcessedField1(), item.getProcessedField2());
            }

            System.out.println("Finished writing chunk to CSV.");
        } catch (IOException e) {
            logger.error("Error occurred while writing to CSV: {}", e.getMessage());
            throw e;
        }
    }
}
