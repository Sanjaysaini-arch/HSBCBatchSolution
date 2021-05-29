package com.hsbc.writers;

import org.springframework.batch.item.ItemWriter;
import org.springframework.classify.Classifier;

import com.hsbc.dto.TransactionRecord;


public class TransactionRecordClassifier implements Classifier<TransactionRecord, ItemWriter<? super TransactionRecord>> {

	private static final long serialVersionUID = 1L;
	
	private ItemWriter<TransactionRecord> fileWriter;
	private ItemWriter<TransactionRecord> dbWriter;

	public TransactionRecordClassifier(ItemWriter<TransactionRecord> fileWriter, ItemWriter<TransactionRecord> dbWriter) {
		this.dbWriter = dbWriter;
		this.fileWriter= fileWriter;
	}

	@Override
	public ItemWriter<? super TransactionRecord> classify(TransactionRecord record) {
		return record.getId() % 2 == 0 ? dbWriter : fileWriter;
	}
}
