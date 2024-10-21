package com.example.BatchJob;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BatchJobApplicationTests {

    private CsvProcessor csvProcessor;

    @BeforeEach
    public void setUp() {
        // Initialize CsvProcessor before each test
        csvProcessor = new CsvProcessor();
    }

    @Test
    public void testProcess() {
        // Given: An OriginalData input
        OriginalData input = new OriginalData("test", "info");

        // When: Processing the input
        ProcessedData result = null;
        try {
            result = csvProcessor.process(input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Then: Check if the output matches expected values
        assertEquals("TEST", result.getProcessedField1());
        assertEquals("info", result.getProcessedField2());
    }
}
