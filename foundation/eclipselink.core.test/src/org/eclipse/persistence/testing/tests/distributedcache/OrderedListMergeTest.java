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
 *     08/15/2008-1.0.1 Chris Delahunt 
 *       - 237545: List attribute types on OneToMany using @OrderBy does not work with attribute change tracking
 ******************************************************************************/ 
package org.eclipse.persistence.testing.tests.distributedcache;

import org.eclipse.persistence.descriptors.changetracking.AttributeChangeTrackingPolicy;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.employee.domain.Child;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;
import java.util.Collection;
import java.util.Iterator;

//Bugs 4458089 & 4454532
//adds one to the collection
public class OrderedListMergeTest extends DistributedCacheMergeTest {
    public OrderedListMergeTest(){
        super();
    }

    protected void modifyCollection(UnitOfWork uow, Object objectToModify) {
        Child newChildWC = (Child)uow.registerNewObject(newItemForCollection());
        ((Employee)objectToModify).children.add(newChildWC);
        newChildWC.parent = (Employee)objectToModify;
    }

    protected int getCollectionSize(Object rootObject) {
        return ((Employee)rootObject).children.size();
    }

    protected Object buildOriginalObject() {
        Employee emp = new Employee();
        emp.setFirstName("Sally");
        emp.setLastName("Hamilton");
        emp.setFemale();
        java.util.Vector children = new java.util.Vector();
        java.util.Calendar cal =java.util.Calendar.getInstance();
        
        Child child = new Child();
        child.setFirstName("Sarah");
        child.setLastName("Hamilton");
        cal.set(2005,8,8);
        child.setBirthday( new java.util.Date(cal.getTimeInMillis()) );
        child.setGender("Female");
        child.setParent(emp);
        children.add(child);
        
        
        child = new Child();
        child.setFirstName("Billy");
        child.setLastName("Hamilton");
        cal.set(2006,8,8);
        child.setBirthday( new java.util.Date(cal.getTimeInMillis()) );
        child.setGender("Male");
        child.setParent( emp );
        children.add(child);
        
        child = new Child();
        child.setFirstName("Samantha");
        child.setLastName("Hamilton");
        cal.set(2007,8,8);
        child.setBirthday(new java.util.Date(cal.getTimeInMillis()) );
        child.setGender("Female");
        child.setParent(emp);
        children.add(child);

        emp.children = children;
        return emp;
    }

    protected Object newItemForCollection() {
        Child child = new Child();
        child.firstName = "Susy";
        child.lastName = "Hamilton";
        child.birthday = new java.util.Date(System.currentTimeMillis());
        child.gender = "Female";
        return child;
    }
    
    protected Project getNewProject() {
        Project empProject = new EmployeeProject();
        empProject.getDescriptor(Employee.class).setObjectChangePolicy(new AttributeChangeTrackingPolicy());
        return empProject;
    }
    
    protected boolean compareObjectsCollections(Object object1, Object object2){
        if ( getCollectionSize(object1)!=getCollectionSize(object2) ){
            return false;
        }
        return compareCollections( ((Employee)object1).children, ((Employee)object2).children );
    }
    
    protected boolean compareCollections(Collection col1, Collection col2){
        boolean comparison=true;
        Iterator i1 = col1.iterator();
        Iterator i2 = col2.iterator();
        while (comparison && i1.hasNext()){
            Object o = i1.next();
            comparison = o.equals(i2.next());
        }
        return comparison;
    }

}
