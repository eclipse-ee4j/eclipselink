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
package org.eclipse.persistence.tools.workbench.test.utility.diff.model;

import java.util.Iterator;
import java.util.ListIterator;

public interface Employee {

	String getPosition();
	void setPosition(String string);

	int getId();
	void setId(int id);

	String getName();
	void setName(String string);

	float getSalary();
	void setSalary(float f);

	Iterator comments();
	void addComment(String comment);
	void clearComments();

	Address getAddress();
	void setAddress(Address address);

	Iterator dependents();
	Dependent addDependent(String depName, String depDescription);
	void clearDependents();
	Dependent dependentNamed(String depName);

	ListIterator cars();
	Car addCar(String carName, String carDescription);
	void clearCars();
	Car carNamed(String carName);

	Iterator phoneNumbers();
	PhoneNumber addPhoneNumber(String phoneDescription, String areaCode, String exchange, String number, String extension);
	PhoneNumber addPhoneNumber(String phoneDescription, String areaCode, String exchange, String number);
	void clearPhoneNumbers();
	PhoneNumber getPhoneNumber(String phoneDescription);

	Iterator underlings();
	void addUnderling(Employee underling);
	void clearUnderlings();
	Employee underlingNamed(String underlingName);

	Iterator vacationBackups();
	void addVacationBackup(Employee vacationBackup);
	void clearVacationBackups();
	Employee vacationBackupNamed(String vacationBackupName);

	Iterator eatingPartners();
	void setEatingPartner(String meal, Employee partner);
	void clearEatingPartners();
	Employee getEatingPartner(String meal);

}
