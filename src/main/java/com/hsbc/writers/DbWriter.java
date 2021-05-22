package com.hsbc.writers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.hsbc.dto.Person;

@Component
public class DbWriter implements org.springframework.batch.item.ItemWriter<Person>  {
	
	@Override
    public void write(List<? extends Person> list) throws Exception {
        for(Person person: list){
        	System.out.println("HI i am db Writer"+person.getFirstName());
        	
            // code which compresses the file
        }
    }}