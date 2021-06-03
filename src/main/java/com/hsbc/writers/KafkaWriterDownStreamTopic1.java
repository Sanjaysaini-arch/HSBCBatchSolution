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
public class KafkaWriterDownStreamTopic1 implements ItemWriter<TransactionRecord> {

    
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaWriterDownStreamTopic1.class);
	@Value("${topic1.json}")
    private String jsonTopic;

    @Autowired
    private KafkaTemplate<String, TransactionRecord> kafkaTemplate;

    @Override
    public void write(List<? extends TransactionRecord> records) throws Exception {
    	records.forEach(record -> kafkaTemplate.send(jsonTopic, record));
    }
} 


