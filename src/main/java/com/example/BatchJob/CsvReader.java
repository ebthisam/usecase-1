package com.example.BatchJob;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.batch.item.ItemReader;
import org.springframework.core.io.ClassPathResource;

public class CsvReader implements ItemReader<OriginalData> {
    private final Iterator<CSVRecord> iterator;

    public CsvReader() throws Exception {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("input.csv").getInputStream()));
        this.iterator = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader).iterator();
    }

    @Override
    public OriginalData read() throws Exception {
        if (iterator.hasNext()) {
            CSVRecord record = iterator.next();
            return new OriginalData(record.get("Column1"), record.get("Column2"));
        }
        return null; // No more data
    }
    
    
    
    
    
}
