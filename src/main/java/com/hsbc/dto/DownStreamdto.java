package com.hsbc.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
@Entity(name = "DOWNSTREAMDTO")
public class DownStreamdto {
	
	@Id
	private int id;
	@Column(name = "JOBTITLE")
	private String jobTitle;
	@Column(name = "EMAILADDRESS")
	private String emailAddress;
	@Column(name = "FIRSTNAME")
	private String firstName;
	@Column(name = "LASTNAME")
	private String lastName;
	@Column(name = "SALARY")
	private float salary;
	@Column(name = "AMOUNTADDTOSALARY")
	private float amountAddToSalary;
	@Column(name = "PHONENUMBER")
	private String phoneNumber;
	@Column(name = "DOWNSTREAMTYPE")
	private String downStreamType;
	


	public DownStreamdto(int id, String jobTitle, String emailAddress, String firstName, String lastName,
			float salary, float amountAddToSalary, String phoneNumber, String downStreamType) {
		super();
		this.id = id;
		this.jobTitle = jobTitle;
		this.emailAddress = emailAddress;
		this.firstName = firstName;
		this.lastName = lastName;
		this.salary = salary;
		this.amountAddToSalary = amountAddToSalary;
		this.phoneNumber = phoneNumber;
		this.downStreamType = downStreamType;
	}


	public float getAmountAddToSalary() {
		return amountAddToSalary;
	}


	public void setAmountAddToSalary(float amountAddToSalary) {
		this.amountAddToSalary = amountAddToSalary;
	}


	public String getDownStreamType() {
		return downStreamType;
	}


	public void setDownStreamType(String downStreamType) {
		this.downStreamType = downStreamType;
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
		return amountAddToSalary;
	}


	public void setAmoutAddToSalary(float amoutAddToSalary) {
		this.amountAddToSalary = amoutAddToSalary;
	}


	public String getPhoneNumber() {
		return phoneNumber;
	}


	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}



	


	public DownStreamdto() {
	}


	@Override
	public String toString() {
		return "TransactionRecord [id=" + id + ", jobTitle=" + jobTitle + ", emailAddress=" + emailAddress
				+ ", firstName=" + firstName + ", lastName=" + lastName + ", salary=" + salary + ", amountAddToSalary="
				+ amountAddToSalary + ", phoneNumber=" + phoneNumber + ", downStreamType=" + downStreamType + "]";
	}





	

}
