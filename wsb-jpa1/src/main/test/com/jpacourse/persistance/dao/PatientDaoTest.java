package com.jpacourse.persistence.dao;

import com.jpacourse.persistence.entity.PatientEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true",
        "logging.level.org.hibernate.SQL=debug",
})
@Transactional
public class PatientDaoTest {

    @Autowired
    private PatientDao patientDao;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private PlatformTransactionManager transactionManager;

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

    @Test
    public void shouldFindPatientsByLastName() {
        // given
        String lastName = "Kowalski";

        // when
        List<PatientEntity> patients = patientDao.findByLastName(lastName);

        // then
        assertThat(patients).hasSize(2);
        assertThat(patients).extracting("firstName")
                .containsExactlyInAnyOrder("Jan", "Adam");
    }

    @Test
    public void shouldFindPatientsWithMoreThanXVisits() {
        // given
        int threshold = 2;

        // when
        List<PatientEntity> results = patientDao.findPatientsWithMoreThanXVisits(threshold);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(3L);
    }

    @Test
    public void shouldFindPatientsByAgeGreaterThan() {
        // given
        int age = 30;

        // when
        List<PatientEntity> patients = patientDao.findByAgeGreaterThan(age);

        // then
        assertThat(patients)
                .extracting(PatientEntity::getAge)
                .allMatch(a -> a > 30);
    }

    @Test
    public void testOptimisticLockingOnPatientWithThreads() throws InterruptedException {
        CountDownLatch latchTx1Flushed = new CountDownLatch(1);
        CountDownLatch latchTx2Read = new CountDownLatch(1);

        // ====== WĄTEK 1: Transakcja 1 ======
        Thread tx1Thread = new Thread(() -> {
            new TransactionTemplate(transactionManager).execute(status -> {
                PatientEntity patient1 = patientDao.findOne(1L);
                System.out.println("Tx1 - pobrano patient1. version=" + patient1.getVersion());


                patient1.setLastName("Nowe");
                System.out.println("Tx1 - ustawiam lastName='Nowe'");


                em.flush();
                System.out.println("Tx1 - po flush. version=" + patient1.getVersion());


                latchTx1Flushed.countDown();


                try {
                    latchTx2Read.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return null;
            });
        });

        // ====== WĄTEK 2: Transakcja 2 ======
        Thread tx2Thread = new Thread(() -> {
            try {
                latchTx1Flushed.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            new TransactionTemplate(transactionManager).execute(status -> {
                PatientEntity patient2 = patientDao.findOne(1L);
                System.out.println("Tx2 - pobrano patient2. version=" + patient2.getVersion());

                patient2.setLastName("Nowe2");
                System.out.println("Tx2 - ustawiam lastName='Nowe2'");


                latchTx2Read.countDown();

                try {
                    em.flush();
                    System.out.println("Tx2 - po flush. version=" + patient2.getVersion());

                    fail("Oczekiwano rzucenia OptimisticLockException, ale tak się nie stało!");
                } catch (OptimisticLockException e) {
                    System.out.println("Złapano OptimisticLockException – jest to oczekiwane zachowanie.");
                }
                return null;
            });
        });

        tx1Thread.start();
        tx2Thread.start();
        tx1Thread.join();
        tx2Thread.join();
    }


}
