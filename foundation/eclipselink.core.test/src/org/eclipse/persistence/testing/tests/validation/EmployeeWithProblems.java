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
package org.eclipse.persistence.testing.tests.validation;

import java.sql.Time;

import java.util.Vector;


//Created by Ian Reid
//Date: Feb 6, 2k3
//used for testing (TL-ERROR 81) called from class org.eclipse.persistence.testing.tests.validation.ReturnTypeInGetAttributeAccessorTest

public class EmployeeWithProblems extends org.eclipse.persistence.testing.models.employee.domain.Employee {
    /** One-to-one mapping, employee references its address through a foreign key. */
    public
    //	public ValueHolderInterface addressWithProblems;//used to test TL-125
    org.eclipse.persistence.testing.models.employee.domain.Address addressWithProblems; //used to test TL-125  

    /** One-to-many mapping (same class relationship), inverse relationship to manager, uses manager foreign key in the target. */
    public String managedEmployeesWithProblems; //used to test TL-133

    /** One-to-one mapping (same class relationship), employee references its manager through a foreign key. */
    public EmployeeWithProblems managerWithProblems; //used to test TL-144

    /** Aggregate-collection mapping. */
    public Vector addressesWithProblems; //used to test TL-153 

    public String notMapped; //used to test TL-45

    private String illegalAccess; //used in tests which require an in-accessable attribute

    /* Varray Testing added
  */
    private Vector courses; //used to test TL-157

    public Vector getCourses() {
        return courses;
    }

    public void setCourses(Vector input) {
        courses = input;
    }

    public void addCourse(String input) {
        getCourses().add(new String(input));
    }

    public String getIllegalAccess() throws java.lang.IllegalAccessException {
        throw new java.lang.IllegalAccessException();
    }

    public void setIllegalAccess(String input) throws java.lang.IllegalAccessException {
        throw new java.lang.IllegalAccessException();
    }

    public EmployeeWithProblems() {
        super();
        // 	this.addressWithProblems = new org.eclipse.persistence.testing.models.employee.domain.Address();//new ValueHolder();//initilized to null to cause error
        //  	this.managedEmployeesWithProblems = new Vector();
        //    this.managerWithProblems = null;
        this.addressesWithProblems = new Vector();
        this.courses = new Vector();
    }
    //this method returns void (instead of Time[]) to produce the error (TL-ERROR 81)

    public void buildNormalHours2(org.eclipse.persistence.sessions.Record row, org.eclipse.persistence.sessions.Session session) { //used in test class org.eclipse.persistence.testing.tests.validation.ReturnTypeInGetAttributeAccessorTest
        Time[] hours = new Time[2];

        /** This conversion allows for the database type not to match, i.e. may be a Timestamp or String. */
        hours[0] = (Time)session.getPlatform().convertObject(row.get("START_TIME"), java.sql.Time.class);
        hours[1] = (Time)session.getPlatform().convertObject(row.get("END_TIME"), java.sql.Time.class);
        //	return hours;
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public

    org.eclipse.persistence.testing.models.employee.domain.Address getAddressWithProblems() {
        return addressWithProblems; //.getValue();
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public

    void setAddressWithProblems(org.eclipse.persistence.testing.models.employee.domain.Address address) {
        this.addressWithProblems = address; //.setValue(address);
    }

    public String getManagedEmployeesWithProblems() {
        return managedEmployeesWithProblems;
    }

    public void setManagedEmployeesWithProblems(String input) {
        managedEmployeesWithProblems = input;
    }

    public EmployeeWithProblems getManagerWithProblems() {
        return managerWithProblems;
    }

    public void setManagerWithProblems(EmployeeWithProblems input) {
        managerWithProblems = input;
    }


}
