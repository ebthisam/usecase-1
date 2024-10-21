package com.example.BatchJob;

import org.springframework.batch.item.ItemProcessor;

public class CsvProcessor implements ItemProcessor<OriginalData, ProcessedData> {

    @Override
    public ProcessedData process(OriginalData originalData) {
        String processedField1 = originalData.getField1().toUpperCase();  // Example processing logic
        String processedField2 = originalData.getField2().toUpperCase();  // Example processing logic
        return new ProcessedData(processedField1, processedField2);
    }
}
