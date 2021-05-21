package com.hsbc.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

import com.hsbc.dto.Person;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {

	private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);

	@Override
	public Person process(final Person person) throws Exception {
		person.setSalary(person.getSalary()+person.getAmoutAddToSalary());
		/*final String firstName = person.getFirstName().toUpperCase();
		final String lastName = person.getLastName().toUpperCase();

		final Person transformedPerson = new Person(jobTitle,emailAddress,firstName,lastName,salary,amoutAddToSalary,phoneNumber);
*/
		log.info("updating salary (" + person + ")");

		return person;
	}

}
