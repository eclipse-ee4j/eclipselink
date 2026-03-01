/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.readonly;

import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.readonly.Address;
import org.eclipse.persistence.testing.models.readonly.Country;
import org.eclipse.persistence.testing.models.readonly.Promoter;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * <p>
 * <b>Purpose</b>: Test the public API with regards to manipulation of the sets of the
 *    read-only classes in a UnitOfWork and the default set in Project.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Test the accessors for the manipulation of the read-only class set in instances
 * <li> of UnitOfWork.
 * <li> Test the accessors for the manipulation of the default read-only class in an
 * <li> instance of Project.
 * </ul>
 */
public class ReadOnlyClassAccessingTestCase extends TestCase {
    public ReadOnlyClassAccessingTestCase() {
        super();
    }

    /**
     *  Compares a Hashtable and a vector for equality.
     */
    protected boolean areEqual(Set<?> ht, List<?> v1) {
        if (v1.size() != ht.size()) {
            return false;
        }
        return ht.containsAll(v1);
    }

    /**
     *  Compares two vectors for equality.
     */
    protected boolean areEqual(List<?> v1, List<?> v2) {
        if (v1.size() != v2.size()) {
            return false;
        }

        return v1.containsAll(v2);
    }

    @Override
    public void reset() {
        getSession().getProject().setDefaultReadOnlyClasses(new ArrayList<>());
    }

    @Override
    protected void setup() {
        getSession().getProject().setDefaultReadOnlyClasses(new ArrayList<>());

    }

    @Override
    protected void test() {
        // Test acquiring a unit of work.
        UnitOfWork uow1 = getSession().acquireUnitOfWork();
        if (!uow1.getReadOnlyClasses().isEmpty()) {
            throw new TestErrorException(" When acquiring a UnitOfWork from a Session, the read-only classes where not empty as expected.");
        }
        uow1.release();

        // Test acquiring a unit of work with a vector of read-only classes.
        Vector classes = new Vector();
        classes.add(Promoter.class);
        classes.add(Country.class);
        UnitOfWork uow2 = getSession().acquireUnitOfWork();
        uow2.removeAllReadOnlyClasses();
        uow2.addReadOnlyClasses(classes);
        if (!areEqual(uow2.getReadOnlyClasses(), classes)) {
            throw new TestErrorException("When acquiring a UnitOfWork from a Session, the read-only classes specified did not get set in the UnitOfWork;");
        }

        // Test the testing of read-only classes
        for (Enumeration enumtr = classes.elements(); enumtr.hasMoreElements();) {
            if (!uow2.isClassReadOnly((Class)enumtr.nextElement())) {
                throw new TestErrorException("Testing whether a class is read-only or not has failed.");
            }
        }
        if (uow2.isClassReadOnly(Vector.class)) {
            throw new TestErrorException("Testing whether a class is read-only or not has failed.");
        }

        // Test the add and remove of read-only classes.
        uow2.removeReadOnlyClass(Promoter.class);
        if (uow2.isClassReadOnly(Promoter.class)) {
            throw new TestErrorException("The method removeReadOnlyClass(Class) failed.");
        }
        uow2.addReadOnlyClass(Promoter.class);
        if (!uow2.isClassReadOnly(Promoter.class)) {
            throw new TestErrorException("The method addReadOnlyClass(Class) failed.");
        }

        // Test the removeAll.
        uow2.removeAllReadOnlyClasses();
        if ((uow2.isClassReadOnly(Country.class)) || (!uow2.getReadOnlyClasses().isEmpty())) {
            throw new TestErrorException("Did not remove all the read-only classes from a UnitOfWork properly");
        }

        // Check that we cannot make  changes to the read-only set after registering an object.
        try {
            uow2.registerObject(new Address());
            uow2.removeAllReadOnlyClasses();
            uow2.addReadOnlyClasses(classes);
            if (areEqual(uow2.getReadOnlyClasses(), classes)) {
                throw new TestErrorException("Shouldn't be able to change the readOnlyClasses of a UnitOfWork after an object was registered.");
            }
        } catch (org.eclipse.persistence.exceptions.ValidationException ex) {
            getSession().logMessage("Caught validation exeception...OK");
        } finally {
            uow2.release();
        }

        // Check that the default read-only classes work.
        List<Class<?>> someClasses = new ArrayList<>();
        someClasses.add(Country.class);
        someClasses.add(Address.class);
        getSession().getProject().setDefaultReadOnlyClasses(someClasses);
        UnitOfWork uow3 = getSession().acquireUnitOfWork();
        if (!areEqual(uow3.getReadOnlyClasses(), someClasses)) {
            throw new TestErrorException("The default read-only classes were not set properly when a UnitOfWork was aquired");
        }
        // Nested units of work should not be able to reduce the set of read-only classes.
        UnitOfWork uow4 = uow3.acquireUnitOfWork();
        try {
            uow4.removeAllReadOnlyClasses();
        } catch (org.eclipse.persistence.exceptions.ValidationException ex) {
            getSession().logMessage("Check the nested units of work read-only classes. OK");
        } finally {
            uow3.release();
            uow4.release();
        }

        // You should be able to get the default set of read-only classes from nested UnitOfWork objects.
        UnitOfWork uow5 = getSession().acquireUnitOfWork();
        UnitOfWork uow6 = uow5.acquireUnitOfWork();
        if (!areEqual(((UnitOfWorkImpl)uow5).getDefaultReadOnlyClasses(), ((UnitOfWorkImpl)uow6).getDefaultReadOnlyClasses()))
            throw new TestErrorException("Nested UnitOfWorks did not return consistent default read-only classes.");
        uow5.release();
        uow6.release();
    }

    /**
     *  Compares two vectors for equality.
     */
    protected boolean vectorsEqual(Vector v1, Vector v2) {
        if (v1.size() != v2.size()) {
            return false;
        }
        for (Enumeration enumtr = v1.elements(); enumtr.hasMoreElements();) {
            if (!v2.contains(enumtr.nextElement())) {
                return false;
            }
        }
        for (Enumeration enumtr = v2.elements(); enumtr.hasMoreElements();) {
            if (!v1.contains(enumtr.nextElement())) {
                return false;
            }
        }
        return true;
    }
}
