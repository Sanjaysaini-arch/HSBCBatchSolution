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
		 //before write any manupulation to record can be done here;
	  }
	 
	  @Override
	  public void afterWrite(List<? extends TransactionRecord> items) {
		 items.stream().forEach(item->logger.info("SuccessFullyWrite:"+item));
	 
	  }
	 
	  @Override
	  public void onWriteError(Exception exception, List<? extends TransactionRecord> items) {
		  items.stream().forEach(
				  item->logger.error("FailedWrite:"+item+"FailedWriteReason"+exception.getMessage()));

	  }
	}
