package com.hsbc.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;

import com.hsbc.dto.TransactionRecord;

public class CustomItemProcessorListener implements ItemProcessListener<TransactionRecord, TransactionRecord> {
	 private static final Logger logger = LoggerFactory.getLogger("CustomItemProcessorListener");
	   
		
	  @Override
	  public void beforeProcess(TransactionRecord item) {
		  //Please apply business logic or record manupulation here if need 
	  }
	 
	  @Override
	  public void afterProcess(TransactionRecord item, TransactionRecord result) {
		  if(result!=null) {
		  logger.info("SuccessFullyProcess:"+result);
		  }else {
		  logger.info("FailedProcess:"+item);
		  }
			  
	  }
	 
	  @Override
	  public void onProcessError(TransactionRecord item, Exception e) {
		  logger.info("FailedProcess:"+item);
		  logger.error("FailedProcessExceptionReason:"+e.getMessage());
	  }
	}
