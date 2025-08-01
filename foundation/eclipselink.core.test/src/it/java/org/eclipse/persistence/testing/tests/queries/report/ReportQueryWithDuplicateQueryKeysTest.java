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
package org.eclipse.persistence.testing.tests.queries.report;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

public class ReportQueryWithDuplicateQueryKeysTest extends AutoVerifyTestCase {
    public ReportQueryWithDuplicateQueryKeysTest() {
    }

    @Override
    public void setup() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Bar bar = new Bar();
        Person person = new Person();
        person.setKey("Key1");
        person.setName("Anthony");
        bar.setBrewer(person);
        bar.setName("Dugs Jugs o' Beer");
        bar.setKey("TheKey");
        Beer beer = new Beer();
        person.setFaviouriteBeer(beer);
        beer.setBrand("Dugs");
        beer.setKey("AnotherKey");
        beer.setBrewer(new Brewer("Molson"));
        beer.getBrewer().setKey("Key2");
        bar.addBeer(beer);
        uow.registerObject(bar);
        uow.commit();
    }

    @Override
    public void reset() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Bar bar = (Bar)uow.readObject(Bar.class);
        uow.deleteObject(bar);
        uow.deleteObject(bar.getBrewer());
        uow.deleteObject(bar.getBrewer().getFaviouriteBeer().getBrewer());
        uow.deleteObject(bar.getBrewer().getFaviouriteBeer());
        uow.commit();

    }

    @Override
    public void test() {
        ExpressionBuilder builder = new ExpressionBuilder();
        ReportQuery reportQuery = new ReportQuery(Bar.class, builder);
        Expression expression = builder.get("brewer").get("faviouriteBeer").get("brewer").get("name");
        reportQuery.addAttribute("Names", expression);
        try {
            Object result = getSession().executeQuery(reportQuery);
        } catch (QueryException ex) {
            throw new TestErrorException("Test failed as the toplink failed to find the correct descriptor." + ex);
        }
    }
}
