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
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.map;

import java.util.TreeMap;

public class Employee {
    private int id;
    private TreeMap addresses;

    public Employee() {
        super();
        addresses = new TreeMap();
    }

    public int getID() {
        return id;
    }

    public void setID(int newId) {
        id = newId;
    }

    public TreeMap getAddresses() {
        return addresses;
    }

    public void setAddresses(TreeMap newAddresses) {
        addresses = newAddresses;
    }

    public MailingAddress getMailingAddress(String type) {
        return (MailingAddress)addresses.get(type);
    }

    public void addMailingAddress(MailingAddress newMailingAddress) {
        addresses.put(newMailingAddress.getAddressType(), newMailingAddress);
    }

    public String toString() {
        String output = "Employee: " + this.getID();
        MailingAddress work = getMailingAddress(MailingAddress.WORK_TYPE);
        MailingAddress home = getMailingAddress(MailingAddress.HOME_TYPE);
        if (work != null) {
            output += getMailingAddress(MailingAddress.WORK_TYPE);
        }
        if (home != null) {
            output += getMailingAddress(MailingAddress.HOME_TYPE);
        }
        return output;
    }

    public boolean equals(Object object) {
        if (!(object instanceof Employee)) {
            return false;
        }
        Employee employeeObject = (Employee)object;

        if (this.getID() == employeeObject.getID()) {
            MailingAddress thisHome = this.getMailingAddress(MailingAddress.HOME_TYPE);
            MailingAddress objectHome = employeeObject.getMailingAddress(MailingAddress.HOME_TYPE);
            if (((thisHome == null) && (objectHome == null)) || (thisHome.equals(objectHome))) {
                MailingAddress thisWork = this.getMailingAddress(MailingAddress.WORK_TYPE);
                MailingAddress objectWork = employeeObject.getMailingAddress(MailingAddress.WORK_TYPE);
                if (((thisWork == null) && (objectWork == null)) || (thisWork.equals(objectWork))) {
                    return true;
                }
            }
        }

        return false;
    }
}
