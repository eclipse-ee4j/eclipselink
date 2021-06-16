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
package org.eclipse.persistence.testing.tests.nls.japanese;

import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;

public class NLSQueryTimeOutTest extends AutoVerifyTestCase {
    private boolean limitExceed;

    public NLSQueryTimeOutTest() {
        setDescription("[NLS_Japanese] Test the query timeout setting");
        this.limitExceed = false;
    }

    public void test() {
        try {
            getSession().executeQuery("queryTimeOutQuery", org.eclipse.persistence.testing.tests.nls.japanese.NLSEmployee.class);
        } catch (Exception e) {
            if (e instanceof org.eclipse.persistence.exceptions.DatabaseException) {
                limitExceed = true;
            }
        }
    }

    public void verify() {
        if (!limitExceed) {
            throw new org.eclipse.persistence.testing.framework.TestProblemException("Query timeout test fails");
        }
    }
}
