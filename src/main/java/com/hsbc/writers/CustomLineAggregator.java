package com.hsbc.writers;
import org.springframework.batch.item.file.transform.LineAggregator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsbc.dto.TransactionRecord;

public class CustomLineAggregator implements LineAggregator<TransactionRecord> {

	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String aggregate(TransactionRecord record) {
		try {
			return objectMapper.writeValueAsString(record);
		} catch (Exception e) {
			throw new RuntimeException("Unable to serialize Employee", e);
		}
	}
}
