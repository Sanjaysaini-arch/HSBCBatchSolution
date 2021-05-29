package com.hsbc.listener;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemWriteListener;

import com.hsbc.dto.TransactionRecord;

public class CustomItemWriterListener implements ItemWriteListener<TransactionRecord> {
	 private static final Logger logger = LoggerFactory.getLogger("CustomItemWriterListener");
		
	  @Override
	  public void beforeWrite(List<? extends TransactionRecord> items) {
		  logger.info("ItemWriteListener ---- before write" + items);
	  }
	 
	  @Override
	  public void afterWrite(List<? extends TransactionRecord> items) {
		  logger.info("ItemWriteListener ---- after write" + items);
	 
	  }
	 
	  @Override
	  public void onWriteError(Exception exception, List<? extends TransactionRecord> items) {
		  logger.info("ItemWriteListener ---- exception" + items);
	  }
	}
