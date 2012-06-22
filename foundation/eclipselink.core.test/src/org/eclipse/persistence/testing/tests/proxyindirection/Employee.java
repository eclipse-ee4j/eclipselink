/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
    public void addManagedEmployee(Employee value);

    public Address getAddress();

    public int getAge();

    public Contact getContact();

    public int getCubicleID();

    public String getFirstName();

    public String getGender();

    public int getID();

    public String getLastName();

    public Collection getManagedEmployees();

    public Employee getManager();

    public Project getProject();

    public LargeProject getLargeProject();

    public void setAddress(Address value);

    public void setAge(int value);

    public void setContact(Contact value);

    public void setCubicleID(int value);

    public void setFirstName(String value);

    public void setGender(String value);

    public void setID(int value);

    public void setLastName(String value);

    public void setManagedEmployees(Collection value);

    public void setManager(Employee value);

    public void setProject(Project value);

    public void setLargeProject(LargeProject value);
}
