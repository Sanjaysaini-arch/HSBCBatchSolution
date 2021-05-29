package com.hsbc.writers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hsbc.dto.TransactionRecord;
import com.hsbc.repository.TransactionRecordRepository;


@Component
public class DbWriter implements org.springframework.batch.item.ItemWriter<TransactionRecord>  {
	
	
    
    private TransactionRecordRepository transactionRecordRepository;
    @Autowired
    public DbWriter (TransactionRecordRepository transactionRecordRepository) {
        this.transactionRecordRepository = transactionRecordRepository;
    }
	@Override
    public void write(List<? extends TransactionRecord> records) throws Exception {
		
        
        	transactionRecordRepository.saveAll(records);
        	
        
        
    }}