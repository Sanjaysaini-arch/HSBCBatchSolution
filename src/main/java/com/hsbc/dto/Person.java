package com.hsbc.dto;

import java.util.Date;

public class Person {
	int id;
	private String jobTitle;
	private String emailAddress;
	private String firstName;
	private String lastName;
	private float salary;
	private float amoutAddToSalary;
	private String phoneNumber;
	public Person(int id, String jobTitle, String emailAddress, String firstName, String lastName, float salary,
			float amoutAddToSalary, String phoneNumber) {
		super();
		this.id = id;
		this.jobTitle = jobTitle;
		this.emailAddress = emailAddress;
		this.firstName = firstName;
		this.lastName = lastName;
		this.salary = salary;
		this.amoutAddToSalary = amoutAddToSalary;
		this.phoneNumber = phoneNumber;
	}
	


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getJobTitle() {
		return jobTitle;
	}


	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}


	public String getEmailAddress() {
		return emailAddress;
	}


	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public float getSalary() {
		return salary;
	}


	public void setSalary(float salary) {
		this.salary = salary;
	}


	public float getAmoutAddToSalary() {
		return amoutAddToSalary;
	}


	public void setAmoutAddToSalary(float amoutAddToSalary) {
		this.amoutAddToSalary = amoutAddToSalary;
	}


	public String getPhoneNumber() {
		return phoneNumber;
	}


	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}



	


	public Person() {
	}



	@Override
	public String toString() {
		return "Person [id=" + id + ", jobTitle=" + jobTitle + ", emailAddress=" + emailAddress + ", firstName="
				+ firstName + ", lastName=" + lastName + ", salary=" + salary + ", amoutAddToSalary=" + amoutAddToSalary
				+ ", phoneNumber=" + phoneNumber + "]";
	}

	

}
