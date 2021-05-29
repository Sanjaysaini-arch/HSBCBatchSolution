package com.hsbc.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;

import com.hsbc.dto.TransactionRecord;

public class CustomItemReaderListener implements ItemReadListener<TransactionRecord> {
	 private static final Logger logger = LoggerFactory.getLogger("CustomItemReaderListener");
		
	  @Override
	  public void beforeRead() {
		  logger.info("ItemReadListener ---- before read ");
	  }
	 
	  @Override
	  public void afterRead(TransactionRecord item) {
		  logger.info("ItemReadListener ---- after read ");
	  }
	 
	  @Override
	  public void onReadError(Exception ex) {
		  logger.info("ItemReadListener ---- exception");
	  }
	}

