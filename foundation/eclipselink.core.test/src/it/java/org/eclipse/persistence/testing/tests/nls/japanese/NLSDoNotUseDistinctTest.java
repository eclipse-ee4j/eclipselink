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
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import java.util.Vector;

public class NLSDoNotUseDistinctTest extends AutoVerifyTestCase {
    private Vector employees;
    private ReadAllQuery query;

    public NLSDoNotUseDistinctTest() {
        setDescription("[NLS_Japanese] Test use distinct option");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
    }

    public void test() {
        query = (ReadAllQuery)getSession().getDescriptor(org.eclipse.persistence.testing.tests.nls.japanese.NLSEmployee.class).getQueryManager().getQuery("doNotUseDistinctQuery");
        ReadAllQuery queryCopy = (ReadAllQuery)query.clone();
        queryCopy.setSelectionCriteria(new ExpressionBuilder().anyOf("phoneNumbers").get("areaCode").equal("613"));
        employees = (Vector)getSession().executeQuery(queryCopy);
    }

    public void verify() {
        if (employees.size() != 13) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("ReadAllQuery with doNotUseDistinct test failed. Mismatched objects returned");
        }
    }
}
