package com.nnk.springboot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nnk.springboot.domain.CurvePoint;

@Repository
public interface CurvePointRepository extends JpaRepository<CurvePoint, Integer> {

}
