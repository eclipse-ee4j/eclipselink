/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
 
package org.eclipse.persistence.testing.tests.jpa.jpql;

// Java imports
import java.util.*;

// TopLink imports
import org.eclipse.persistence.internal.sessions.AbstractSession;

// Domain imports
import org.eclipse.persistence.testing.models.jpa.advanced.*;

public class JUnitDomainObjectComparer
{
    AbstractSession theSession;
    public boolean compareObjects(Object obj1, Object obj2) {
        if((obj1 == null) && (obj2 == null)) {
            return true;
        } 
        if((obj1 == null) || (obj2 == null)) {
            return false;
        }
        
        if ((obj1 instanceof Collection)  && !(obj2 instanceof Collection)) {
            return compareObjects(obj2, (Collection)obj1);
        } else if ( !(obj1  instanceof Collection)  && (obj2 instanceof Collection)) {
            return compareObjects(obj1, (Collection)obj2);
        } else if ((obj1 instanceof Collection) && (obj2 instanceof Collection)) {
            return compareObjects((Collection)obj1, (Collection)obj2);
        } else {
            return getSession().compareObjects(obj1, obj2);
        }
    }

    public boolean compareObjects(Object domainObject1, Collection aCollection) {
        Iterator itr = aCollection.iterator();      
        
        while (itr.hasNext()) {
            Object domainObject2 = itr.next();
            if (compareObjects(domainObject1, domainObject2)) {
                return true;
            }
        }
        return false;
    }

    public boolean compareObjects(Collection objects1, Collection objects2) {
        boolean allMatched = true;
        
        if (objects1.size() != objects2.size()) {
            allMatched = false;
        } /*else if (objects1.isEmpty() || objects2.isEmpty()) {
            allMatched = false;
        }*/
        //Enumeration enum1 = objects1.elements();
        Iterator itr1 = objects1.iterator();
        
        while (itr1.hasNext()) {
            Object obj1 = itr1.next();            

            if (obj1 == null) {
                allMatched = allMatched & objects2.contains(null);
                continue;
            }
            if(obj1.getClass().equals(Employee.class)){
                Employee emp1 = (Employee)obj1;
                Employee emp2 = findEmployeeByIdIn(emp1, objects2);
                allMatched = allMatched && compareObjects(emp1, emp2);
            }
            if(obj1.getClass().equals(Project.class)){
                Project proj1 = (Project)obj1;
                Project proj2 = findProjectByIdIn(proj1, objects2);
                allMatched = allMatched && compareObjects(proj1, proj2);
            }
            if(obj1.getClass().equals(LargeProject.class)){
                Project proj1 = (Project)obj1;
                Project proj2 = findProjectByIdIn(proj1, objects2);
                allMatched = allMatched && compareObjects(proj1, proj2);
            }
            if(obj1.getClass().equals(PhoneNumber.class)){
                PhoneNumber phone1 = (PhoneNumber)obj1;
                PhoneNumber phone2 = findPhoneNumberIn(phone1, objects2);
                allMatched = allMatched && compareObjects(phone1, phone2);
            }
            if(obj1.getClass().equals(String.class)){
                allMatched = allMatched && objects2.contains(obj1);           
                    
            }
        }
        return allMatched;
    }
    public PhoneNumber findPhoneNumberIn(PhoneNumber phone1, Collection phones){
        
        Iterator itr = phones.iterator();
        while (itr.hasNext()) {
            PhoneNumber pTemp = (PhoneNumber)itr.next();
            if ((pTemp != null) && pTemp.getNumber().equals(phone1.getNumber())) {
                if(pTemp.getAreaCode().equals(phone1.getAreaCode())) {
                    if (pTemp.getOwner().getId().equals(phone1.getOwner().getId())) {
                        return pTemp;
                    }
                }
            }
        }
        return null;
    }
    public Project findProjectByIdIn(Project project, Collection projects){
        
        Iterator itr = projects.iterator();        
        while (itr.hasNext()) {
        
            Project pTemp = (Project)itr.next();
            if ((pTemp != null) && pTemp.getId().equals(project.getId())) {
                return pTemp;
            }
        }
        return null;
    }
    public Employee findEmployeeByIdIn(Employee emp, Collection employees){
        Iterator itr = employees.iterator();        
        
        while (itr.hasNext()) {
            Employee eTemp = (Employee)itr.next();
            if ((eTemp != null) && eTemp.getId().equals(emp.getId())) {
                return eTemp;
            }
        }
        return null;
    }
    public Address findAddressByIdIn(Address addr, Collection addresses){
        Iterator itr = addresses.iterator();
        while (itr.hasNext()) {
            Address aTemp = (Address)itr.next();
            if ((aTemp != null) && aTemp.getId() == addr.getId()) {
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
