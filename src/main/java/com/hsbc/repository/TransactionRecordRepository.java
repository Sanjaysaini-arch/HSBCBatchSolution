package com.hsbc.repository;
import org.springframework.data.jpa.repository.JpaRepository;


import com.hsbc.dto.ReportDto;
import com.hsbc.dto.TransactionRecord;





public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Integer> {
}