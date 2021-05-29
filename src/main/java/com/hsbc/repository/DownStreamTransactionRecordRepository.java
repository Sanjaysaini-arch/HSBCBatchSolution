package com.hsbc.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hsbc.dto.DownStreamdto;






public interface DownStreamTransactionRecordRepository extends JpaRepository<DownStreamdto, Integer> {
}