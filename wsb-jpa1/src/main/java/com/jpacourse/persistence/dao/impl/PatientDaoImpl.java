package com.jpacourse.persistence.dao.impl;

import com.jpacourse.persistence.dao.PatientDao;
import com.jpacourse.persistence.entity.DoctorEntity;
import com.jpacourse.persistence.entity.PatientEntity;
import com.jpacourse.persistence.entity.VisitEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class PatientDaoImpl extends AbstractDao<PatientEntity, Long> implements PatientDao {
    @PersistenceContext
    private EntityManager em;
    @Override
    public void addVisitToPatient(Long patientId, Long doctorId, LocalDateTime visitDate, String description) {
        PatientEntity patient = em.find(PatientEntity.class, patientId);
        if (patient == null) {
            throw new IllegalArgumentException("Patient not found with id: " + patientId);
        }

        DoctorEntity doctor = em.find(DoctorEntity.class, doctorId);
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor not found with id: " + doctorId);
        }

        VisitEntity visit = new VisitEntity();
        visit.setDescription(description);
        visit.setTime(visitDate);
        visit.setDoctor(doctor);
        visit.setPatient(patient);

        patient.getVisits().add(visit);

        em.merge(patient);
        em.flush();
    }
    @Override
    public List<PatientEntity> findByLastName(String lastName) {
        return em.createQuery(
                        "SELECT p FROM PatientEntity p WHERE p.lastName = :lastName",
                        PatientEntity.class)
                .setParameter("lastName", lastName)
                .getResultList();
    }

    @Override
    public List<PatientEntity> findPatientsWithMoreThanXVisits(int x) {
        return em.createQuery(
                        "SELECT p FROM PatientEntity p WHERE size(p.visits) > :x",
                        PatientEntity.class)
                .setParameter("x", x)
                .getResultList();
    }

    @Override
    public List<PatientEntity> findByAgeGreaterThan(int age) {
        return em.createQuery(
                        "SELECT p FROM PatientEntity p WHERE p.age > :age",
                        PatientEntity.class)
                .setParameter("age", age)
                .getResultList();
    }
}



