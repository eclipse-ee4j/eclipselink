/*
 * Copyright (c) 2018, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.tests.collections.map;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.testing.models.collections.map.EntityEntity1MMapHolder;
import org.eclipse.persistence.testing.tests.queries.report.ReportQueryTestCase;

import java.util.Enumeration;
import java.util.Vector;

public class MapKeyEntityEntity1MReportQueryTestCase extends ReportQueryTestCase{

    @Override
    protected void buildExpectedResults() {
        Vector holders = getSession().readAllObjects(EntityEntity1MMapHolder.class);

        for (Enumeration e = holders.elements(); e.hasMoreElements(); ) {
            EntityEntity1MMapHolder holder = (EntityEntity1MMapHolder)e.nextElement();
            for (Object o : holder.getEntityToEntityMap().keySet()) {
                Object[] result = new Object[1];
                result[0] = o;
                addResult(result, null);
            }
        }
    }

    @Override
    protected void setup() throws Exception {
        super.setup();
        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(EntityEntity1MMapHolder.class);
        reportQuery.addAttribute("key", reportQuery.getExpressionBuilder().anyOf("entityToEntityMap").mapKey());
    }
}
