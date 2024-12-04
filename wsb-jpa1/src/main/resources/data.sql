-- Tabela Address
INSERT INTO address (id, address_line1, address_line2, city, postal_code)
VALUES
    (1, 'xx', 'yy', 'city', '62-030'),
    (2, 'Main Street', 'Suite 100', 'New York', '10001');

-- Tabela Doctor
INSERT INTO doctor (id, first_name, last_name, specialization, telephone_number, email, doctor_number)
VALUES
    (1, 'Jan', 'Kowalski', 'Cardiology', '123456789', 'jan.kowalski@example.com', 'D001'),
    (2, 'Anna', 'Nowak', 'Pediatrics', '987654321', 'anna.nowak@example.com', 'D002');

-- Tabela Doctor_Address
INSERT INTO doctor_address (doctor_id, address_id)
VALUES
    (1, 1),
    (2, 2);

-- Tabela Patient
INSERT INTO patient (id, first_name, last_name, telephone_number, email, date_of_birth, patient_number)
VALUES
    (1, 'Jan', 'Kowalski', '123456789', 'jan.kowalski@example.com', '1985-01-01', 'P001'),
    (2, 'Anna', 'Nowak', '987654321', 'anna.nowak@example.com', '1990-05-15', 'P002');

-- Tabela Patient_Address
INSERT INTO patient_address (patient_id, address_id)
VALUES
    (1, 1),
    (2, 2);

-- Tabela Visit
INSERT INTO visit (id, description, time, doctor_id, patient_id)
VALUES
    (1, 'Cardiology consultation', '2023-01-01 12:00:00', 1, 1),
    (2, 'Pediatric check-up', '2023-01-02 15:00:00', 2, 2);

-- Tabela Medical_Treatment
INSERT INTO medical_treatment (id, description, type, visit_id)
VALUES
    (1, 'Ultrasound examination', 'USG', 1),
    (2, 'Blood test', 'Laboratory', 2);
