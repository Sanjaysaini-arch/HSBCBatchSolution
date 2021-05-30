package com.hsbc.writers;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.hsbc.dto.TransactionRecord;

@Component
public class RestApiDownStreamWriter implements org.springframework.batch.item.ItemWriter<TransactionRecord> {

	private static final Logger logger = LoggerFactory.getLogger("RestApiDownStreamWriter");

	@Autowired
	RestTemplate restTemplate;

	@Override
	public void write(List<? extends TransactionRecord> list) throws Exception {
		for (TransactionRecord record : list) {

			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			HttpEntity<TransactionRecord> entity = new HttpEntity<TransactionRecord>(record, headers);
			//try {
				String out = restTemplate
						.exchange("http://localhost:8093/postDownStreamDto", HttpMethod.POST, entity, String.class)
						.getBody();
				logger.info("RestApiDownStreamWriterSuccess:" + out);
			/*} catch (RestClientException ex) {
				logger.error("FailedWriteReason:RestDownStream:" + ex.getMessage() + "FailedWrite:" + list);
			}*/

		}
	}

}
