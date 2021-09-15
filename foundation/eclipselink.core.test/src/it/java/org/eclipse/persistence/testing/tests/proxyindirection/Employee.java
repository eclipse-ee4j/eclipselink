/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.proxyindirection;

import java.util.Collection;

/*
 * Employee interface.
 *
 * Define behavior for Employee objects.
 *
 * @author        Rick Barkhouse
 * @since        08/15/2000 15:51:05
 */
public interface Employee {
    void addManagedEmployee(Employee value);

    Address getAddress();

    int getAge();

    Contact getContact();

    int getCubicleID();

    String getFirstName();

    String getGender();

    int getID();

    String getLastName();

    Collection getManagedEmployees();

    Employee getManager();

    Project getProject();

    LargeProject getLargeProject();

    void setAddress(Address value);

    void setAge(int value);

    void setContact(Contact value);

    void setCubicleID(int value);

    void setFirstName(String value);

    void setGender(String value);

    void setID(int value);

    void setLastName(String value);

    void setManagedEmployees(Collection value);

    void setManager(Employee value);

    void setProject(Project value);

    void setLargeProject(LargeProject value);
}
