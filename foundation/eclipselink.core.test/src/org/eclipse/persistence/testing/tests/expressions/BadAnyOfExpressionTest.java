/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 */
// Contributors:Suresh Balakrishnan

package org.eclipse.persistence.testing.tests.expressions;

import java.util.Vector;

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.queries.InMemoryQueryIndirectionPolicy;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Contact;
import org.eclipse.persistence.testing.models.employee.domain.Customer;
import org.eclipse.persistence.testing.models.employee.domain.TkeEmployee;

public class BadAnyOfExpressionTest extends TestCase {

    protected EclipseLinkException exception;

    public BadAnyOfExpressionTest() {
        super();
        setDescription("Test using a LiteralExpression through ExpressionBuilder.literal()");
    }

    public void test() {
        // loadTestDataForAnyOfExpression();
        // addAnyOfExpressionWithMemoryQueryPolicy();
    }

    private void addAnyOfExpressionWithMemoryQueryPolicy() {
        UnitOfWorkImpl uow = (UnitOfWorkImpl) getSession().acquireUnitOfWork();

        // Init all contacts (!)
        ReadAllQuery raqCon = getNewReadAllQuery(Contact.class);
        Vector<Contact> resultSetCon = (Vector<Contact>) uow.executeQuery(raqCon);
        // Read Customer CompA
        ReadAllQuery raqCompA = getNewReadAllQuery(Customer.class);
        ExpressionBuilder ebCompA = new ExpressionBuilder();
        raqCompA.setSelectionCriteria(ebCompA.get("name").equal("CompA"));
        Vector<Customer> resultSetCompA = (Vector<Customer>) uow.executeQuery(raqCompA);
        Customer compA = resultSetCompA.firstElement();
        // Read all Employees, visited CompA for 5 days -> only Bob via Con1
        ReadAllQuery raqEmp = getNewReadAllQuery(TkeEmployee.class);
        ExpressionBuilder ebEmp = new ExpressionBuilder();
        Expression exprAnyOf = ebEmp.anyOf("contacts");
        Expression exp = exprAnyOf.get("customer").get("name").equal("CompA").and(exprAnyOf.get("daysVisted").equal(5));
        raqEmp.setSelectionCriteria(exp);
        Vector<TkeEmployee> resultSetEmp = (Vector<TkeEmployee>) uow.executeQuery(raqEmp);
        if (resultSetEmp.size() != 1) {
            throw new TestErrorException("Wrong number of Employees, expected 1, but was " + resultSetEmp.size());
        }

        uow.release();
    }

    private ReadAllQuery getNewReadAllQuery(Class<?> c) {
        ReadAllQuery raq = new ReadAllQuery(c);
        raq.conformResultsInUnitOfWork();
        raq.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(InMemoryQueryIndirectionPolicy.SHOULD_TRIGGER_INDIRECTION));
        return raq;
    }

    // store entities to DB
    private void loadTestDataForAnyOfExpression() {
        UnitOfWorkImpl uow = (UnitOfWorkImpl) getSession().acquireUnitOfWork();

        TkeEmployee bob = getNewEmployee("Bob", uow);
        TkeEmployee paul = getNewEmployee("Paul", uow);

        Customer compA = getNewCustomer("CompA", uow);
        Customer compB = getNewCustomer("CompB", uow);

        Contact con1 = getNewContact(uow);
        con1.daysVisted = 5;
        bob.addContact(con1);
        compA.addContact(con1);

        Contact con2 = getNewContact(uow);
        con2.daysVisted = 10;
        paul.addContact(con2);
        compA.addContact(con2);

        Contact con3 = getNewContact(uow);
        con3.daysVisted = 5;
        paul.addContact(con3);
        compB.addContact(con3);

        uow.commit();
        uow.release();
    }

    private TkeEmployee getNewEmployee(String name, UnitOfWorkImpl uow) {
        TkeEmployee emp = new TkeEmployee(name);
        uow.registerNewObject(emp);
        return emp;
    }

    private static Customer getNewCustomer(String name, UnitOfWorkImpl uow) {
        Customer cus = new Customer(name);
        uow.registerNewObject(cus);
        return cus;
    }

    private static Contact getNewContact(UnitOfWorkImpl uow) {
        Contact con = new Contact();
        uow.registerNewObject(con);
        return con;
    }

}
