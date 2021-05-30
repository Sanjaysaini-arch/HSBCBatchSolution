package com.hsbc.writers;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.hsbc.dto.TransactionRecord;
@Component
public class KafkaWriterDownStreamTopic2 implements ItemWriter<TransactionRecord> {

    
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaWriterDownStreamTopic2.class);
	@Value("${topic2.json}")
    private String jsonTopic;

    @Autowired
    private KafkaTemplate<Long, TransactionRecord> kafkaTemplate;

    @Override
    public void write(List<? extends TransactionRecord> records) throws Exception {
    	records.forEach(record -> kafkaTemplate.send(jsonTopic, record));
    }
} 


