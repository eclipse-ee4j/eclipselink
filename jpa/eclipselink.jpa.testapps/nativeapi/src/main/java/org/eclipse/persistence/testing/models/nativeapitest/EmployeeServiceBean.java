/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.nativeapitest;

import java.util.*;

import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;

// EclipseLink imports
import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;

/**
 * EmployeeService session bean.
 */
@Stateless(mappedName="EmployeeService")
@Remote(EmployeeService.class)
public class EmployeeServiceBean implements EmployeeService {

    @Override
    public List<Employee> findAll() {
        ClientSession clientSession = getSession().acquireClientSession();
        @SuppressWarnings("unchecked")
        List<Employee> collection = (List<Employee>) clientSession.readAllObjects(Employee.class);
        return collection;
    }

    @Override
    public Employee findById(int id) {
        ReadObjectQuery readObjectQuery = new ReadObjectQuery(Employee.class);
        ExpressionBuilder builder = new ExpressionBuilder();
        readObjectQuery.setSelectionCriteria(builder.get("id").equal(id));
        Employee employee = (Employee)getUnitOfWork().executeQuery(readObjectQuery);
        employee.getAddress();
        return employee;
    }

    @Override
    public Employee fetchById(int id) {
        ReadObjectQuery readObjectQuery = new ReadObjectQuery(Employee.class);
        ExpressionBuilder builder = new ExpressionBuilder();
        readObjectQuery.setSelectionCriteria(builder.get("id").equal(id));
        Employee employee = (Employee)getUnitOfWork().executeQuery(readObjectQuery);
        employee.getAddress();
        employee.getManager();
        return employee;
    }

    @Override
    public List<Employee> findByFirstName(String fname) {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ExpressionBuilder builder = query.getExpressionBuilder();
        Expression expression = builder.get("firstName").equal(fname);
        query.setSelectionCriteria(expression);
        @SuppressWarnings("unchecked")
        List<Employee> result = (List<Employee>)getSession().executeQuery(query);
        return result;
    }

    @Override
    public void update(Employee employee) {
        ClientSession clientSession = getSession().acquireClientSession();
        UnitOfWork uow = clientSession.getActiveUnitOfWork();
        uow.deepMergeClone(employee);
    }

    @Override
    public int insert(Employee employee) {
        ClientSession clientSession = getSession().acquireClientSession();
        UnitOfWork uow = clientSession.getActiveUnitOfWork();
        Employee emp = (Employee)uow.registerObject(employee);
        uow.assignSequenceNumber(emp);
        uow.commit();
        return emp.getId();
    }

    @Override
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
