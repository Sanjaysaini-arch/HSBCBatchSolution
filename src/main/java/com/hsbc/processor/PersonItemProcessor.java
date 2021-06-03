package com.hsbc.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;

import com.hsbc.dto.TransactionRecord;

public class PersonItemProcessor extends BeanValidatingItemProcessor<TransactionRecord> {
	

	private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);

	public TransactionRecord process(TransactionRecord trx) {
		log.info("record is valid"+trx.getId());
			
		return trx;
	}

}
