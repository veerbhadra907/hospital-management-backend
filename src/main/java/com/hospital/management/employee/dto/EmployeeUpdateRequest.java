package com.hospital.management.employee.dto;

import com.hospital.management.employee.model.EmployeeRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmployeeUpdateRequest ( @Size(max = 50, message = "First name cannot exceed 50 characters")
                                      String firstName,

                                      @Size(max = 50, message = "Last name cannot exceed 50 characters")
                                      String lastName,

                                      EmployeeRole role,

                                      @Size(max = 100, message = "Department cannot exceed 100 characters")
                                      String department,

                                      @Pattern(
                                              regexp = "^[0-9]{10}$",
                                              message = "Phone number must contain exactly 10 digits"
                                      )
                                      String phone,

                                      @Email(message = "Invalid email address")
                                      @Size(max = 100, message = "Email cannot exceed 100 characters")
                                      String email)


        {};
