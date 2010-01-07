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
package org.eclipse.persistence.testing.models.nativeapitest;

import java.util.*;

import javax.ejb.Remote;
import javax.ejb.Stateless;

// EclipseLink imports
import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;

import org.eclipse.persistence.testing.models.nativeapitest.Employee;

/**
 * EmployeeService session bean.
 */
@Stateless(mappedName="EmployeeService")
@Remote(EmployeeService.class)
public class EmployeeServiceBean implements EmployeeService {
    
    public List findAll() {
        ClientSession clientSession = getSession().acquireClientSession();
        List collection = clientSession.readAllObjects(Employee.class);
        return collection;
    }
    
    public Employee findById(int id) {
        ReadObjectQuery readObjectQuery = new ReadObjectQuery(Employee.class);
        ExpressionBuilder builder = new ExpressionBuilder();
        readObjectQuery.setSelectionCriteria(builder.get("id").equal(id));
        Employee employee = (Employee)getUnitOfWork().executeQuery(readObjectQuery);
        employee.getAddress();
        return employee;
    }
    
    public Employee fetchById(int id) {
        ReadObjectQuery readObjectQuery = new ReadObjectQuery(Employee.class);
        ExpressionBuilder builder = new ExpressionBuilder();
        readObjectQuery.setSelectionCriteria(builder.get("id").equal(id));
        Employee employee = (Employee)getUnitOfWork().executeQuery(readObjectQuery);
        employee.getAddress();
        employee.getManager();
        return employee;
    }
    
    public List findByFirstName(String fname) {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ExpressionBuilder builder = query.getExpressionBuilder();
        Expression expression = builder.get("firstName").equal(fname);
        query.setSelectionCriteria(expression);
        List result = (List)getSession().executeQuery(query);
        return result;
    }
    
    public void update(Employee employee) {
        ClientSession clientSession = getSession().acquireClientSession();
        UnitOfWork uow = clientSession.getActiveUnitOfWork();
        uow.deepMergeClone(employee);
    }
    
    public int insert(Employee employee) {
        ClientSession clientSession = getSession().acquireClientSession();
        UnitOfWork uow = clientSession.getActiveUnitOfWork();
        Employee emp = (Employee)uow.registerObject(employee);
        uow.assignSequenceNumber(emp);
        uow.commit();
        return emp.getId();
    }
    
    public void delete(Employee employee) {
        ClientSession clientSession = getSession().acquireClientSession();
        Employee employeeToDelete = (Employee) clientSession.readObject(employee);
        UnitOfWork uow = clientSession.getActiveUnitOfWork();
        uow.deleteObject(employeeToDelete);
    }
    
    /** 
     * Return the EclipseLink Session
     */
    public Server getSession() {
        return (Server)SessionManager.getManager().getSession("NativeAPITest", this.getClass().getClassLoader());
    }
    /** 
     * Return a UnitOfWork for reading single objects and updating. 
     * This assumes the JTS has already created a unit of work through
     * the Required attribute on the session bean.
     */
    public UnitOfWork getUnitOfWork(){
        ClientSession clientSession = getSession().acquireClientSession();
        return clientSession.getActiveUnitOfWork();
    }
}
