/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
