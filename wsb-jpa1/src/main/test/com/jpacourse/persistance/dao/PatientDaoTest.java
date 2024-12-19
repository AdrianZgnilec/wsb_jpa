package com.jpacourse.persistence.dao;

import com.jpacourse.persistence.entity.PatientEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
})
@Transactional
public class PatientDaoTest {

    @Autowired
    private PatientDao patientDao;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void testAddVisitToPatient1() {
        // given
        Long patientId = 2L;
        Long doctorId = 1L;
        LocalDateTime visitDate = LocalDateTime.of(2024, 1, 10, 10, 0);
        String description = "Scheduled check-up";

        // when
        patientDao.addVisitToPatient(patientId, doctorId, visitDate, description);

        // then
        em.flush();
        em.clear();

        PatientEntity patientAfter = em.find(PatientEntity.class, patientId);
        assertThat(patientAfter).isNotNull();
        long visitCount = patientAfter.getVisits().stream()
                .filter(v -> v.getDescription().equals(description)
                        && v.getTime().equals(visitDate)
                        && v.getDoctor().getId().equals(doctorId))
                .count();
        assertThat(visitCount).isEqualTo(1);
    }

}
