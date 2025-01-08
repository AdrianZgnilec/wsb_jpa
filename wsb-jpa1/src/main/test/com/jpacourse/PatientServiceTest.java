package com.jpacourse;

import com.jpacourse.dto.PatientTO;
import com.jpacourse.persistence.entity.VisitEntity;
import com.jpacourse.service.PatientService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class PatientServiceTest {
    @Autowired
    private PatientService patientService;


    @Test
    public void testShouldFindPatientWithVisits() {
        // given
        Long patientId = 2L;

        // when
        PatientTO patient = patientService.findById(patientId);

        // then
        assertThat(patient).isNotNull();
        assertThat(patient.getId()).isEqualTo(patientId);
        assertThat(patient.getFirstName()).isEqualTo("Anna");
        assertThat(patient.getLastName()).isEqualTo("Nowak");
        assertThat(patient.getAge()).isEqualTo(33);
        assertThat(patient.getVisits()).hasSize(1);
        VisitEntity visit = patient.getVisits().get(0);
        assertThat(visit.getDescription()).isEqualTo("Pediatric check-up");
        assertThat(visit.getTime().toString()).isEqualTo("2023-01-02T15:00");
    }

    @Test
    public void testShouldRemovePatientAndHisVisitsButNotAffectOtherDoctors() {
        // given
        Long patientIdToDelete = 1L;
        PatientTO patientBefore = patientService.findById(patientIdToDelete);
        assertThat(patientBefore).isNotNull();
        assertThat(patientBefore.getVisits()).isNotEmpty();


        Long otherPatientId = 2L;
        PatientTO otherPatientBefore = patientService.findById(otherPatientId);
        assertThat(otherPatientBefore).isNotNull();
        assertThat(otherPatientBefore.getVisits()).isNotEmpty();

        // when
        patientService.deleteById(patientIdToDelete);

        // then
        PatientTO patientAfter = patientService.findById(patientIdToDelete);
        assertThat(patientAfter).isNull();
        PatientTO otherPatientAfter = patientService.findById(otherPatientId);
        assertThat(otherPatientAfter).isNotNull();
        assertThat(otherPatientAfter.getVisits()).isNotEmpty();

    }

    @Test
    public void shouldFindAllVisitsForPatient() {
        // given
        Long patientId = 3L;

        // when
        List<VisitEntity> visits = patientService.getAllVisitsByPatientId(patientId);

        // then
        assertThat(visits).hasSize(3);
    }
}