package com.jpacourse.persistence.dao;

import com.jpacourse.persistence.entity.PatientEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface PatientDao extends Dao<PatientEntity, Long> {
    default void addVisitToPatient(Long patientId, Long doctorId, LocalDateTime visitDate, String description) {

    }
    List<PatientEntity> findByLastName(String lastName);
    List<PatientEntity> findPatientsWithMoreThanXVisits(int x);
    List<PatientEntity> findByAgeGreaterThan(int age);
}

