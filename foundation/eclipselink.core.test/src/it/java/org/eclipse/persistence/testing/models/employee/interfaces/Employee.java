/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.employee.interfaces;

import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.Vector;

/**
 * <p><b>Purpose</b>: Represent a employee of a fictional organization for eaxample &amp; testing purposes.
 * <p><b>Description</b>: An Employee is a root object in the EmployeeDemo. It maintains relationships to all
 * of the other objects in the system.
 */
public interface Employee extends Serializable {
    void addManagedEmployee(org.eclipse.persistence.testing.models.employee.interfaces.Employee employee);

    void addPhoneNumber(PhoneNumber phoneNumber);

    void addProject(org.eclipse.persistence.testing.models.employee.interfaces.Project project);

    void addResponsibility(String responsibility);

    Address getAddress();

    Time getEndTime();

    String getFirstName();

    String getGender();

    BigDecimal getId();

    String getLastName();

    Vector getManagedEmployees();

    org.eclipse.persistence.testing.models.employee.interfaces.Employee getManager();

    Time[] getNormalHours();

    EmploymentPeriod getPeriod();

    Vector getPhoneNumbers();

    Vector getProjects();

    Vector getResponsibilitiesList();

    int getSalary();

    Time getStartTime();

    void removeManagedEmployee(org.eclipse.persistence.testing.models.employee.interfaces.Employee employee);

    void removePhoneNumber(PhoneNumber phoneNumber);

    void removeProject(org.eclipse.persistence.testing.models.employee.interfaces.Project project);

    void removeResponsibility(String responsibility);

    void setAddress(Address address);

    void setEndTime(Time endTime);

    void setFemale();

    void setFirstName(String firstName);

    void setGender(String gender);

    void setLastName(String lastName);

    void setMale();

    void setManagedEmployees(Vector managedEmployees);

    void setManager(org.eclipse.persistence.testing.models.employee.interfaces.Employee manager);

    void setNormalHours(Time[] normalHours);

    void setPeriod(EmploymentPeriod period);

    void setPhoneNumbers(Vector phoneNumbers);

    void setProjects(Vector projects);

    void setResponsibilitiesList(Vector responsibilities);

    void setSalary(int salary);

    void setStartTime(Time startTime);

    String toString();
}
