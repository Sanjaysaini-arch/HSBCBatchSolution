package com.hsbc.processor;

import javax.validation.ValidationException;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.beans.factory.annotation.Autowired;

import com.hsbc.dto.ReportDto;
import com.hsbc.dto.TransactionRecord;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class PersonItemProcessor extends BeanValidatingItemProcessor<TransactionRecord> {
	

	private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);

	public TransactionRecord process(TransactionRecord trx) {
		log.info("record is valid"+trx.getId());
			
		return trx;
	}

}
