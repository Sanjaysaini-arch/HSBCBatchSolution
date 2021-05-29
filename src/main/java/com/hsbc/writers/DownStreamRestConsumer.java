package com.hsbc.writers;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.hsbc.dto.TransactionRecord;
@Component
public class DownStreamRestConsumer {
	@Autowired
	RestTemplate restTemplate;
	
	   public String createProducts(TransactionRecord  product) {
	      HttpHeaders headers = new HttpHeaders();
	      headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	      HttpEntity<TransactionRecord> entity = new HttpEntity<TransactionRecord>(product,headers);
	      
	      return restTemplate.exchange(
	         "http://localhost:8093/postDownStreamDto", HttpMethod.POST, entity, String.class).getBody();
	   }
	
	
	public String getProductList() {
	      HttpHeaders headers = new HttpHeaders();
	      headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	      HttpEntity <String> entity = new HttpEntity<String>(headers);
	      
	      return restTemplate.exchange("http://localhost:8080/products", HttpMethod.GET, entity, String.class).getBody();
	   }
}

