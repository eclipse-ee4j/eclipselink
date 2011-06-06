/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.models.employee;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.Vector;

/** 
 * <p><b>Purpose</b>: Represent a employee of a fictional organization for eaxample & testing purposes.
 * <p><b>Description</b>: An Employee is a root object in the EmployeeDemo. It maintains relationships to all
 * of the other objects in the system.
 */

public interface EmployeeInterface extends Serializable {
void addManagedEmployee(EmployeeInterface employee);
void addPhoneNumber(PhoneNumber phoneNumber);
void addProject(ProjectInterface project);
void addResponsibility(String responsibility);
Address getAddress();
Time getEndTime();
String getFirstName();
String getGender();
BigDecimal getId();
String getLastName();
Vector getManagedEmployees();
EmployeeInterface getManager();
Time[] getNormalHours();
EmploymentPeriod getPeriod();
Vector getPhoneNumbers();
Vector getProjects();
Vector getResponsibilitiesList();
int getSalary();
Time getStartTime();
void removeManagedEmployee(EmployeeInterface employee);
void removePhoneNumber(PhoneNumber phoneNumber);
void removeProject(ProjectInterface project);
void removeResponsibility(String responsibility);
void setAddress(Address address);
void setEndTime(Time endTime);
void setFemale();
void setFirstName(String firstName);
void setGender(String gender);
void setLastName(String lastName);
void setMale();
void setManagedEmployees(Vector managedEmployees);
void setManager(EmployeeInterface manager);
void setNormalHours(Time[] normalHours);
void setPeriod(EmploymentPeriod period);
void setPhoneNumbers(Vector phoneNumbers);
void setProjects(Vector projects);
void setResponsibilitiesList(Vector responsibilities);
void setSalary(int salary);
void setStartTime(Time startTime);
String toString();
}
