package com.hsbc.repository;
import org.springframework.data.jpa.repository.JpaRepository;


import com.hsbc.dto.ReportDto;





public interface ReportDtoRepository extends JpaRepository<ReportDto, Integer> {
}