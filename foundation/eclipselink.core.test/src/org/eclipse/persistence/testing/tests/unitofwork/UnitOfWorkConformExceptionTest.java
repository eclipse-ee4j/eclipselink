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
package org.eclipse.persistence.testing.tests.unitofwork;

import java.util.Vector;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;


/**
 * Test that exceptions are thrown when attempts to conform query results in a unit of work fail.
 * References: CR 2188.
 *
 * Description:
 * The test generally involves two classes, the second having a
 * pointer to the first.  In this case PhoneNumber has an Employee
 * owner, and Contact has a MailAddress instance.
 * In a Unit of Work, an object of the first class is modified, or has
 * one of its field's changed.  Then a query is executed on the second
 * class (i.e PhoneNumber), which is conditional on the fields of its
 * instance of the first class.
 * The changes and queries are coordinated so that the former should alter
 * the outcome of the latter.
 * If Toplink is unable to conform the results of the query with the
 * original change, a warning exception should be thrown.
 */
public class UnitOfWorkConformExceptionTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    protected Employee bob;
    protected UnitOfWork uow;
    boolean pass;
    ReadObjectQuery phoneNumberQuery;
    ReadAllQuery phoneNumbersQuery;
    ReadAllQuery contactsQuery;

    public UnitOfWorkConformExceptionTest() {
        setDescription("Test that exceptions are thrown when attempts to conform query results in a unit of work fail.");
    }

    public void setup() {
        getAbstractSession().beginTransaction();
        phoneNumberQuery = new ReadObjectQuery();
        phoneNumbersQuery = new ReadAllQuery();
        phoneNumberQuery.setReferenceClass(PhoneNumber.class);
        phoneNumbersQuery.setReferenceClass(PhoneNumber.class);
        Expression exp = new ExpressionBuilder().get("owner").get("firstName").equal("Bob");
        phoneNumberQuery.setSelectionCriteria(exp);
        phoneNumbersQuery.setSelectionCriteria(exp);
        phoneNumberQuery.conformResultsInUnitOfWork();
        phoneNumbersQuery.conformResultsInUnitOfWork();

        contactsQuery = new ReadAllQuery();
        contactsQuery.setReferenceClass(Contact.class);
        exp = new ExpressionBuilder().get("mailAddress").get("mailAddress").like("t%");
        contactsQuery.setSelectionCriteria(exp);
        contactsQuery.conformResultsInUnitOfWork();
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        testThrowNoConformException();
        testThrowConformExceptionWithIndirection();
        testThrowConformExceptionInAllCases();
    }

    protected void setupTrial(int value) {
        pass = false;
        uow = getSession().acquireUnitOfWork();
        uow.setShouldThrowConformExceptions(value);
    }

    protected void tearDownTrial() {
        uow.release();
    }

    protected void testThrowNoConformException() {
        // First test the default behavior.
        setupTrial(UnitOfWorkImpl.DO_NOT_THROW_CONFORM_EXCEPTIONS);
        changeBobToFred();
        try {
            String owner = getAPhoneNumberOfBob().getOwner().getFirstName();
            if (owner.equals("Fred")) {
                // The default behavior should be like this.
            } else {
                throw new TestErrorException("The first name was not conformed to UOW changes: " + owner);
            }
        } catch (Exception e) {
            throw new TestErrorException("An exception should not be thrown in the default case: " + e);
        } finally {
            tearDownTrial();
        }
    }

    protected void testThrowConformExceptionWithIndirection() {
        // First test that an exception is thrown with indirection and an update.
        setupTrial(UnitOfWorkImpl.THROW_ALL_CONFORM_EXCEPTIONS);
        changeBobToFred();
        try {
            getAPhoneNumberOfBob();
        } catch (QueryException e) {
            // This is the excpected behavior.
            pass = true;
        } finally {
            tearDownTrial();
        }
        if (!pass) {
            throw new TestErrorException("A conforming query exception should have been thrown in update case.");
        }

        // Second test that an exception is thrown with indirection and a deletion.
        setupTrial(UnitOfWorkImpl.THROW_ALL_CONFORM_EXCEPTIONS);
        deleteBob();
        try {
            getAPhoneNumberOfBob();
        } catch (QueryException e) {
            // This is the expected behavior.
            pass = true;
        } finally {
            tearDownTrial();
        }
        if (!pass) {
            throw new TestErrorException("A conforming query exception should have been thrown in delete case.");
        }

        // Third test that an exception is thrown with indirection and an insertion.
        setupTrial(UnitOfWorkImpl.THROW_ALL_CONFORM_EXCEPTIONS);
        addPhoneNumberToBob();
        try {
            getAPhoneNumberOfBob();
        } catch (QueryException e) {
            // This is the expected behavior.
            pass = true;
        } finally {
            tearDownTrial();
        }
        if (!pass) {
            throw new TestErrorException("A conforming query exception should be thrown in insertion case.");
        }

        // Fourth test that an exception is thrown for a readObjectQuery with Indirection.
        setupTrial(UnitOfWorkImpl.THROW_ALL_CONFORM_EXCEPTIONS);
        changeBobToFred();
        try {
            getASinglePhoneNumberOfBob();
        } catch (QueryException e) {
            // This is the excpected behavior.
            pass = true;
        } finally {
            tearDownTrial();
        }
        if (!pass) {
            throw new TestErrorException("A conforming query exception should have been thrown in read single object case.");
        }

        // Fifth test that an exception is not thrown when indirection is not used.
        setupTrial(UnitOfWorkImpl.THROW_ALL_CONFORM_EXCEPTIONS);
        changeAMailAddress();
        try {
            getContacts();
        } catch (QueryException e) {
            throw new TestErrorException("A conforming query exception should not have be thrown when indirection not used.");
        } finally {
            tearDownTrial();
        }
    }

    protected void testThrowConformExceptionInAllCases() {
        // First test that an exception is thrown with an update.
        setupTrial(UnitOfWorkImpl.THROW_ALL_CONFORM_EXCEPTIONS);
        changeBobToFred();
        try {
            getAPhoneNumberOfBob();
        } catch (QueryException e) {
            // This is the excpected behavior.
            pass = true;
        } finally {
            tearDownTrial();
        }
        if (!pass) {
            throw new TestErrorException("A conforming query exception should have been thrown with an update.");
        }

        // Second test that an exception is thrown even in a case without indirection.
        setupTrial(UnitOfWorkImpl.THROW_ALL_CONFORM_EXCEPTIONS);
        changeAMailAddress();
        try {
            Vector v = getContacts();
            String s = "";
            if (v.size() > 0) {
                Contact contact = (Contact)v.firstElement();
                s = contact.getMailAddress().mailAddress;
            }
            throw new TestErrorException("A conforming query exception should have been thrown even when indirection not involved: " + 
                                         s + v.size());
        } catch (QueryException e) {
            // This is the expected behavior.
        } finally {
            tearDownTrial();
        }
    }

    private void changeBobToFred() {
        Expression exp = new ExpressionBuilder().get("firstName").equal("Bob");
        bob = (Employee)uow.readObject(Employee.class, exp);
        bob.setFirstName("Fred");
    }

    private void deleteBob() {
        Expression exp = new ExpressionBuilder().get("firstName").equal("Bob");
        bob = (Employee)uow.readObject(Employee.class, exp);
        uow.deleteObject(bob);
    }

    private void addPhoneNumberToBob() throws QueryException {
        PhoneNumber phoneNumber = (PhoneNumber)uow.newInstance(PhoneNumber.class);
        bob.addPhoneNumber(phoneNumber);
        phoneNumber.setOwner(bob);
    }

    private PhoneNumber getAPhoneNumberOfBob() throws QueryException {
        Vector phoneNumbers = (Vector)uow.executeQuery(phoneNumbersQuery);
        return (PhoneNumber)phoneNumbers.firstElement();
    }

    private PhoneNumber getASinglePhoneNumberOfBob() throws QueryException {
        return (PhoneNumber)uow.executeQuery(phoneNumberQuery);
    }

    private void changeAMailAddress() {
        Expression exp = new ExpressionBuilder().get("mailAddress").equal("three@object.com");
        MailAddress mailAddress = (MailAddress)uow.readObject(MailAddress.class, exp);
        mailAddress.mailAddress = new String("four@object.com");
    }

    private Vector getContacts() throws QueryException {
        return (Vector)uow.executeQuery(contactsQuery);
    }
}
