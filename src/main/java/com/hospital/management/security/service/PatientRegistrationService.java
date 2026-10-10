package com.hospital.management.security.service;

import com.hospital.management.exception.DuplicateResourceException;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.patient.dto.PatientCreateRequest;
import com.hospital.management.patient.dto.PatientResponse;
import com.hospital.management.patient.model.Patient;
import com.hospital.management.patient.repository.PatientRepository;
import com.hospital.management.patient.service.PatientService;
import com.hospital.management.security.dto.PatientRegistrationRequest;
import com.hospital.management.security.dto.PatientRegistrationResponse;
import com.hospital.management.security.model.UserAccount;
import com.hospital.management.security.model.UserRole;
import com.hospital.management.security.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientRegistrationService {

    private final PatientService patientService;
    private final PatientRepository patientRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;



    @Transactional
    public PatientRegistrationResponse registerPatient(
            PatientRegistrationRequest request) {

        PatientCreateRequest patientRequest = request.patient();

        String email = patientRequest.email().trim();

        // 1. Validate that an account doesn't already use this email.
        if (userAccountRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException(
                    "An account already exists with this email"
            );
        }

        // 2. Create the patient using the existing patient module.
        PatientResponse patientResponse =
                patientService.createPatient(patientRequest);

        // 3. Retrieve the persisted patient entity.
        Patient patient = patientRepository
                .findByPatientId(patientResponse.patientId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Created patient could not be found"
                        ));

        // 4. Create the login account.
        UserAccount account = new UserAccount();
        account.setEmail(email);
        account.setPasswordHash(
                passwordEncoder.encode(request.password())
        );
        account.setRole(UserRole.PATIENT);
        account.setEnabled(true);
        account.setPatient(patient);
        account.setEmployee(null);

        // 5. Save the account in the same transaction.
        userAccountRepository.save(account);

        return new PatientRegistrationResponse(
                patient.getPatientId(),
                account.getEmail(),
                account.getRole(),
                "Patient registered successfully"
        );
    }
}
