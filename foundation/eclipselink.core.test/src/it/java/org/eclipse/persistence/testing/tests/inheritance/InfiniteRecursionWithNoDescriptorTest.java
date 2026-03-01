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
package org.eclipse.persistence.testing.tests.inheritance;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.inheritance.ImaginaryCar;
import org.eclipse.persistence.testing.models.inheritance.Person;

/**
 * Tests that should an object without a descriptor extends a mapped class TopLink
 * Will not get into infinite recursion.
 * @author Gordon Yorke
 */
public class InfiniteRecursionWithNoDescriptorTest extends TestCase {

    @Override
    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Person person = (Person)uow.readObject(Person.class);
        person.getCar().setValue(ImaginaryCar.example3());
        try{
            uow.commit();
        }catch (StackOverflowError error){
            uow.unregisterObject(person);
            uow.release();
            getAbstractSession().rollbackTransaction();
            throw new TestErrorException("Stack overflow occurred");
        }
    }
}
