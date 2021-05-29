package com.hsbc.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;

import com.hsbc.dto.TransactionRecord;

public class CustomItemProcessorListener implements ItemProcessListener<TransactionRecord, TransactionRecord> {
	 private static final Logger logger = LoggerFactory.getLogger("CustomItemProcessorListener");
	   
		
	  @Override
	  public void beforeProcess(TransactionRecord item) {
		  logger.info("ItemProcessListener ---- beforeProcess");
	  }
	 
	  @Override
	  public void afterProcess(TransactionRecord item, TransactionRecord result) {
		  logger.info("ItemProcessListener ---- afterProcess");
	  }
	 
	  @Override
	  public void onProcessError(TransactionRecord item, Exception e) {
		  logger.info("ItemProcessListener ---- onProcessError"+item);
	  }
	}
