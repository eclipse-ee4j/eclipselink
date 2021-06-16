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
package org.eclipse.persistence.testing.tests.proxyindirection;

import java.util.List;

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.TestCase;

public class BatchReadTest extends TestCase {
    List employees;

    public BatchReadTest() {
        setDescription("Tests ReadAllObjects using Proxy Indirection.");
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.addBatchReadAttribute("address");

        employees = (List)getSession().executeQuery(query);
        ((Employee)employees.get(0)).getAddress();
    }
}
