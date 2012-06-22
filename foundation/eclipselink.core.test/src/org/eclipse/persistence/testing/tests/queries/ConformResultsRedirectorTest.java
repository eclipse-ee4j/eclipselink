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
package org.eclipse.persistence.testing.tests.queries;

import java.util.Vector;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.Session;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.TestCase;

/**
 *  Test for bug 3324757 REDIRECT QUERY RESULTS DO NOT CONFORM TO A UNITOFWORK
 *  <p>
 *  This was a bug with the old query design.  The problem was a UnitOfWork 
 *  would send the query to the parent, and the parent would execute the 
 *  redirected query.
 *  <p>
 *  If the query was a conforming query, then conforming would fail as outside 
 *  UnitOfWork.
 *  @author  smcritch
 *  @since   release specific (what release of product did this appear in)
 */
public class

ConformResultsRedirectorTest extends TestCase implements QueryRedirector {

    public ConformResultsRedirectorTest() {
    }

    public void test() {

        ReadAllQuery query = new ReadAllQuery(Address.class);

        UnitOfWork unitOfWork = getSession().acquireUnitOfWork();
        try {
            Vector addresses = (Vector)unitOfWork.executeQuery(query);
            Address address = (Address)addresses.elementAt(0);
            unitOfWork.deleteObject(address);

            query.setRedirector(this);

            Vector conformingAddresses = (Vector)unitOfWork.executeQuery(query);

            strongAssert(addresses.size() == conformingAddresses.size() + 1, 
                         "The redirected conforming query was not invoked.");
        } finally {
            unitOfWork.release();
        }
    }

    public Object invokeQuery(DatabaseQuery query, org.eclipse.persistence.sessions.Record arguments, Session session) {
        ReadAllQuery conformingQuery = new ReadAllQuery(Address.class);
        conformingQuery.conformResultsInUnitOfWork();

        return session.executeQuery(conformingQuery);
    }
}
