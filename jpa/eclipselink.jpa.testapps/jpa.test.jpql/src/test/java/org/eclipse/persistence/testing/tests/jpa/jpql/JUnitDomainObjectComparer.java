/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.tests.jpa.jpql;

// Java imports

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.advanced.Project;

import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>
 * <b>Purpose</b>: Compare two objects
 * <p>
 * <b>Description</b>: This class compares the two objects passed. If they are
 * collections it Iterates through them and compares the elements.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Compare two objects and return true/false
 * </ul>
 */
public class JUnitDomainObjectComparer {

    AbstractSession theSession;
    public boolean compareObjects(Object obj1, Object obj2) {
        if((obj1 == null) && (obj2 == null)) {
            return true;
        }
        if((obj1 == null) || (obj2 == null)) {
            return false;
        }

        if ((obj1 instanceof Collection)  && !(obj2 instanceof Collection)) {
            return compareObjects(obj2, (Collection<?>)obj1);
        } else if ( !(obj1  instanceof Collection)  && (obj2 instanceof Collection)) {
            return compareObjects(obj1, (Collection<?>)obj2);
        } else if ((obj1 instanceof Collection) && (obj2 instanceof Collection)) {
            return compareObjects((Collection<?>)obj1, (Collection<?>)obj2);
        } else {
            if (getSession().compareObjects(obj1, obj2)) {
                return true;
            } else {
                boolean areEqual = false;
                if (JUnitTestCase.usesSOP()) {
                    // In SOP case the PhoneNumber may be read only from Employee's sopObject.
                    // That means that unless read-only attribute PhoneNumber.id is explicitly set (which never happens) it will stay null forever.
                    if ((obj1 instanceof PhoneNumber phone1) && (obj2 instanceof PhoneNumber phone2)) {
                        if (phone1.getId() == null && phone1.getOwner() != null && phone1.getOwner().getId() != null) {
                            // assign ownerId
                            phone1.setId(phone1.getOwner().getId());
                            areEqual = getSession().compareObjects(obj1, obj2);
                            // reset to the original state
                            phone1.setId(null);
                        } else if (phone2.getId() == null && phone2.getOwner() != null && phone2.getOwner().getId() != null) {
                            // assign ownerId
                            phone2.setId(phone2.getOwner().getId());
                            areEqual = getSession().compareObjects(obj1, obj2);
                            // reset to the original state
                            phone2.setId(null);
                        }
                    } else if ((obj1 instanceof Employee emp1) && (obj2 instanceof Employee emp2)) {
                        Collection<PhoneNumber> phoneNumbers1 = emp1.getPhoneNumbers();
                        Collection<PhoneNumber> phoneNumbers2 = emp2.getPhoneNumbers();
                        // compare PhoneNumbers so that PhoneNumber.id == null are worked around (see PhoneNumber case above)
                        if (compareObjects(phoneNumbers1, phoneNumbers2)) {
                            emp1.setPhoneNumbers(new ArrayList<>());
                            emp2.setPhoneNumbers(new ArrayList<>());
                            // now compare the rest
                            areEqual = getSession().compareObjects(obj1, obj2);
                            // reset to the original state
                            emp1.setPhoneNumbers(phoneNumbers1);
                            emp2.setPhoneNumbers(phoneNumbers2);
                        }
                    }
                }
                return areEqual;
            }
        }
    }

    public boolean compareObjects(Object domainObject1, Collection<?> aCollection) {

        for (Object domainObject2 : aCollection) {
            if (compareObjects(domainObject1, domainObject2)) {
                return true;
            }
        }
        return false;
    }

    public boolean compareObjects(Collection<?> objects1, Collection<?> objects2) {
        boolean allMatched = true;

        if (objects1.size() != objects2.size()) {
            allMatched = false;
        } /*else if (objects1.isEmpty() || objects2.isEmpty()) {
            allMatched = false;
        }*/
        //Enumeration enum1 = objects1.elements();

        for (Object obj1 : objects1) {
            if (obj1 == null) {
                allMatched = allMatched & objects2.contains(null);
                continue;
            }
            if (obj1.getClass().equals(Employee.class)) {
                Employee emp1 = (Employee) obj1;
                Employee emp2 = findEmployeeByIdIn(emp1, objects2);
                allMatched = allMatched && compareObjects(emp1, emp2);
            }
            if (obj1.getClass().equals(Project.class)) {
                Project proj1 = (Project) obj1;
                Project proj2 = findProjectByIdIn(proj1, objects2);
                allMatched = allMatched && compareObjects(proj1, proj2);
            }
            if (obj1.getClass().equals(LargeProject.class)) {
                Project proj1 = (Project) obj1;
                Project proj2 = findProjectByIdIn(proj1, objects2);
                allMatched = allMatched && compareObjects(proj1, proj2);
            }
            if (obj1.getClass().equals(PhoneNumber.class)) {
                PhoneNumber phone1 = (PhoneNumber) obj1;
                PhoneNumber phone2 = findPhoneNumberIn(phone1, objects2);
                allMatched = allMatched && compareObjects(phone1, phone2);
            }
            if (obj1.getClass().equals(String.class)) {
                allMatched = allMatched && objects2.contains(obj1);

            }
        }
        return allMatched;
    }
    public PhoneNumber findPhoneNumberIn(PhoneNumber phone1, Collection<?> phones){

        for (Object phone : phones) {
            PhoneNumber pTemp = (PhoneNumber) phone;
            if ((pTemp != null) && pTemp.getNumber().equals(phone1.getNumber())) {
                if (pTemp.getAreaCode().equals(phone1.getAreaCode())) {
                    if (pTemp.getOwner().getId().equals(phone1.getOwner().getId())) {
                        return pTemp;
                    }
                }
            }
        }
        return null;
    }
    public Project findProjectByIdIn(Project project, Collection<?> projects){

        for (Object o : projects) {

            Project pTemp = (Project) o;
            if ((pTemp != null) && pTemp.getId().equals(project.getId())) {
                return pTemp;
            }
        }
        return null;
    }
    public Employee findEmployeeByIdIn(Employee emp, Collection<?> employees){

        for (Object employee : employees) {
            Employee eTemp = (Employee) employee;
            if ((eTemp != null) && eTemp.getId().equals(emp.getId())) {
                return eTemp;
            }
        }
        return null;
    }
    public Address findAddressByIdIn(Address addr, Collection<?> addresses){
        for (Object address : addresses) {
            Address aTemp = (Address) address;
            if ((aTemp != null) && aTemp.getID() == addr.getID()) {
                return aTemp;
            }
        }
        return null;
    }
    public AbstractSession getSession() {
        return theSession;
    }
    public void setSession(AbstractSession theSession) {
        this.theSession = theSession;
    }
}
