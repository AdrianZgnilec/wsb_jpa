package com.jpacourse.service;

import com.jpacourse.dto.PatientTO;
import com.jpacourse.persistence.entity.VisitEntity;

import java.util.List;

public interface PatientService {
    PatientTO findById(final Long id);
    void deleteById(Long id);
    List<VisitEntity> getAllVisitsByPatientId(Long patientId);
}
