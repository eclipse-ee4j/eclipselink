/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.mapping;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.mapping.Address;
import org.eclipse.persistence.testing.models.mapping.CompanyCard;

/**
 * Bug 3142898 - Ensure joining works to multiple levels
 */
public class OneToOnePKTest extends AutoVerifyTestCase {
    public Address emp;

    public OneToOnePKTest() {
        setDescription("Check that objects with foreign keys as PK can be queries correctly.");
    }

    public void setup() {
        CompanyCard card = CompanyCard.example1();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        emp = (Address)uow.readObject(Address.class);
        card = (CompanyCard)uow.registerObject(card);
        card.setOwner(emp);
        uow.commit();
    }

    public void test() {
        // read a baby
        ExpressionBuilder card = new ExpressionBuilder();
        Expression expression = card.get("owner").equal(emp);
        try {
            CompanyCard companyCard = (CompanyCard)getSession().readObject(CompanyCard.class, expression);
        } catch (ConversionException ex) {
            throw new TestErrorException("Failed to resolve PK's in OneToOneMapping: " + ex.toString());
        }
    }
}
