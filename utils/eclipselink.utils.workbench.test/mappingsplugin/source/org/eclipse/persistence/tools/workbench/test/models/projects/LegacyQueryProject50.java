/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.models.projects;

import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalReadAllQuery;

/**
 * The default query timeout used to be zero, but is now -1.  The expected project that
 * is built in memory during legacy 5.0 unit testing has to reflect this new state.  This
 * class correctly sets the timeout value for 5.0 unit test.
 */
public class LegacyQueryProject50 extends QueryProject
{
    /**
     * 0 is the expected value for query timeout in legacy 50 unit tests.
     */
    @Override
    protected Integer getDefaultQueryTimeout()
    {
        return MWQuery.QUERY_TIMEOUT_NO_TIMEOUT;
    }

    @Override
    protected String getDefaultQueryLockMode()
    {
        return MWQuery.NO_LOCK;
    }

    @Override
    protected void createReportQuery() {
       //don't create, report queries didn't exist in 5.0 and before
    }

    @Override
    protected void createReportQuery2() {
        //don't create, report queries didn't exist in 5.0 and before
    }

    @Override
    protected void createReportQuery3() {
        //don't create, report queries didn't exist in 5.0 and before
    }

    @Override
    protected void createReportQuery4() {
        //don't create, report queries didn't exist in 5.0 and before
    }

    @Override
    protected void addAttributesToQuery4(MWRelationalReadAllQuery query) {
        //don't add any attributes(joined, batch read), they didn't exist in 5.0 and before
    }
}
