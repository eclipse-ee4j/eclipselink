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
package org.eclipse.persistence.testing.tests.jpql;

import java.util.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class DomainObjectComparer {
    Session theSession;

    public boolean compareObjects(Object obj1, Object obj2) {
        if ((obj1 == null) && (obj2 == null)) {
            return true;
        }
        if ((obj1 == null) || (obj2 == null)) {
            return false;
        }

        if ((obj1.getClass() == Vector.class) && (obj2.getClass() != Vector.class)) {
            return compareObjects(obj2, (Vector)obj1);
        } else if ((obj1.getClass() != Vector.class) && (obj2.getClass() == Vector.class)) {
            return compareObjects(obj1, (Vector)obj2);
        } else if ((obj1.getClass() == Vector.class) && (obj2.getClass() == Vector.class)) {
            return compareObjects((Vector)obj1, (Vector)obj2);
        } else {
            return ((AbstractSession)getSession()).compareObjects(obj1, obj2);
        }
    }

    public boolean compareObjects(Object domainObject1, Vector aVector) {
        Enumeration enumtr = aVector.elements();
        while (enumtr.hasMoreElements()) {
            Object domainObject2 = enumtr.nextElement();
            if (compareObjects(domainObject1, domainObject2)) {
                return true;
            }
        }
        return false;
    }

    public boolean compareObjects(Vector objects1, Vector objects2) {
        boolean allMatched = true;

        if (objects1.size() != objects2.size()) {
            allMatched = false;
        }
        /*else if (objects1.isEmpty() || objects2.isEmpty()) {
          allMatched = false;
        }*/

        Enumeration enum1 = objects1.elements();
        while (enum1.hasMoreElements()) {
            Object obj1 = enum1.nextElement();

            if (obj1.getClass().equals(Employee.class)) {
                Employee emp1 = (Employee)obj1;
                Employee emp2 = findEmployeeByIdIn(emp1, objects2);
                allMatched = allMatched && compareObjects(emp1, emp2);
            }
            if (obj1.getClass().equals(Project.class)) {
                Project proj1 = (Project)obj1;
                Project proj2 = findProjectByIdIn(proj1, objects2);
                allMatched = allMatched && compareObjects(proj1, proj2);
            }
            if (obj1.getClass().equals(LargeProject.class)) {
                Project proj1 = (Project)obj1;
                Project proj2 = findProjectByIdIn(proj1, objects2);
                allMatched = allMatched && compareObjects(proj1, proj2);
            }
            if (obj1.getClass().equals(PhoneNumber.class)) {
                PhoneNumber phone1 = (PhoneNumber)obj1;
                PhoneNumber phone2 = findPhoneNumberIn(phone1, objects2);
                allMatched = allMatched && compareObjects(phone1, phone2);
            }
            if (obj1.getClass().equals(String.class)) {
                allMatched = allMatched && (objects2.indexOf(obj1) != -1);
            }
        }
        return allMatched;
    }

    public PhoneNumber findPhoneNumberIn(PhoneNumber phone1, Vector phones) {
        Enumeration enumtr = phones.elements();
        while (enumtr.hasMoreElements()) {
            PhoneNumber pTemp = (PhoneNumber)enumtr.nextElement();
            if (pTemp.getNumber().equals(phone1.getNumber())) {
                if (pTemp.getAreaCode().equals(phone1.getAreaCode())) {
                    if (pTemp.getOwner().getId().equals(phone1.getOwner().getId())) {
                        return pTemp;
                    }
                }
            }
        }
        return null;
    }

    public Project findProjectByIdIn(Project project, Vector projects) {
        Enumeration enumtr = projects.elements();
        while (enumtr.hasMoreElements()) {
            Project pTemp = (Project)enumtr.nextElement();
            if (pTemp.getId().equals(project.getId())) {
                return pTemp;
            }
        }
        return null;
    }

    public Employee findEmployeeByIdIn(Employee emp, Vector employees) {
        Enumeration enumtr = employees.elements();
        while (enumtr.hasMoreElements()) {
            Employee eTemp = (Employee)enumtr.nextElement();
            if (eTemp.getId().equals(emp.getId())) {
                return eTemp;
            }
        }
        return null;
    }

    public Address findAddressByIdIn(Address addr, Vector addresses) {
        Enumeration enumtr = addresses.elements();
        while (enumtr.hasMoreElements()) {
            Address aTemp = (Address)enumtr.nextElement();
            if (aTemp.getId() == addr.getId()) {
                return aTemp;
            }
        }
        return null;
    }

    public Session getSession() {
        return theSession;
    }

    public void setSession(Session theSession) {
        this.theSession = theSession;
    }
}
