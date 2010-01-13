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
package org.eclipse.persistence.testing.tests.queries.inmemory;

import java.util.Vector;
import java.util.Enumeration;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

public class MemoryQueryAcrossManyToManyAcrossOneToManyMapping extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    protected Employee employee;
    protected ReadAllQuery queryAll;
    protected ReadAllQuery queryObjects;
    protected ReadObjectQuery queryObject;
    protected java.util.Vector allEmployees;
    protected java.util.Vector employees;

    public MemoryQueryAcrossManyToManyAcrossOneToManyMapping() {
        super();
    }

    public void reset() {
        //clear the cache.
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    /**
     * This tests in-memory query in a convoluted way: across a many to many then a one to one and then a one to many
     *
     */
    public void setup() {
        allEmployees = new Vector();
        employees = new Vector();
        Vector leaders = new Vector();
        queryAll = new ReadAllQuery();
        queryAll.setReferenceClass(Employee.class);
        queryAll.setSelectionCriteria(new ExpressionBuilder().anyOf("projects").get("teamLeader").anyOf("phoneNumbers").get("areaCode").greaterThan("416"));
        getSession().removeQuery("getAllEmployees");
        getSession().addQuery("getAllEmployees", queryAll);
        allEmployees = getSession().readAllObjects(Employee.class); //BUG215359 test needs to trigger indirection on all employees
        for (Enumeration enumtr = allEmployees.elements(); enumtr.hasMoreElements();) {
            Vector projects = ((Employee)enumtr.nextElement()).getProjects();
            employees.addAll(projects);
            //trigger all the value holders of projects
        }
        for (Enumeration enumtr = employees.elements(); enumtr.hasMoreElements();) {
            Employee leader = (Employee)((Project)enumtr.nextElement()).getTeamLeader();
            if (leader != null) {
                leaders.add(leader);
            }

            //trigger all the value holders of teamLeaders
        }
        for (Enumeration enumtr = leaders.elements(); enumtr.hasMoreElements();) {
            Vector phones = ((Employee)enumtr.nextElement()).getPhoneNumbers();
            Vector allPhones = new Vector();
            allPhones.addAll(phones);

            //trigger all the value holders of phones
        }
    }

    public void test() {
        //all the employees who are team leaders with phonenumbers having area code greater than 416 should be
        //in the cache right now.
        queryObjects = new ReadAllQuery();
        queryObjects.setReferenceClass(Employee.class);
        queryObjects.checkCacheOnly();//read from cache only

        ExpressionBuilder bldr = new ExpressionBuilder();
        Expression exp = bldr.anyOf("projects").get("teamLeader").anyOf("phoneNumbers").get("areaCode").equal("613");
        queryObjects.setSelectionCriteria(exp);
        employees = (Vector)getSession().executeQuery(queryObjects);

    }
}
