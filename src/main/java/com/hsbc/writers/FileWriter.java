package com.hsbc.writers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.hsbc.dto.TransactionRecord;

@Component
public class FileWriter implements org.springframework.batch.item.ItemWriter<TransactionRecord>  {
	
		@Override
	    public void write(List<? extends TransactionRecord> list) throws Exception {
	        for(TransactionRecord record: list){
	        	
	            // code which compresses the file
	        	System.out.println("HI i am file Writer"+record.getId());
	        }
	    }
	

}
