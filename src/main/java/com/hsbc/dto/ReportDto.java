package com.hsbc.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.Id;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.Instant;
@Entity(name="REPORTDTO")
public class ReportDto {
	 @Id
	int recordId;
	public int getRecordId() {
		return recordId;
	}
	public void setRecordId(int recordId) {
		this.recordId = recordId;
	}
	String Client_Id;
	int status;
	LocalDateTime recordProcessStratTime;
	LocalDateTime recordProcessEndTime;
	long recordProcessDuration;
	public String getClient_Id() {
		return Client_Id;
	}
	public void setClient_Id(String client_Id) {
		Client_Id = client_Id;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public LocalDateTime getRecordProcessStratTime() {
		return recordProcessStratTime;
	}
	public void setRecordProcessStratTime(LocalDateTime recordProcessStratTime) {
		this.recordProcessStratTime = recordProcessStratTime;
	}
	public LocalDateTime getRecordProcessEndTime() {
		return recordProcessEndTime;
	}
	public void setRecordProcessEndTime(LocalDateTime recordProcessEndTime) {
		this.recordProcessEndTime = recordProcessEndTime;
	}
	public long getRecordProcessDuration() {
		return recordProcessDuration;
	}
	public void setRecordProcessDuration(long recordProcessDuration) {
		this.recordProcessDuration = recordProcessDuration;
	}
	@Override
	public String toString() {
		return "ReportDto [recordId=" + recordId + ", Client_Id=" + Client_Id + ", status=" + status
				+ ", recordProcessStratTime=" + recordProcessStratTime + ", recordProcessEndTime="
				+ recordProcessEndTime + ", recordProcessDuration=" + recordProcessDuration + "]";
	}
	
	
	
	

}
