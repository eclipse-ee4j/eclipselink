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
package org.eclipse.persistence.testing.tests.nls.japanese;

import org.eclipse.persistence.testing.framework.*;
//import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;

public class NLSRefreshIdentityMapResultsTest extends AutoVerifyTestCase {
    protected NLSEmployee originalObject;
    protected String firstName;
    protected ReadObjectQuery query;

    public NLSRefreshIdentityMapResultsTest() {
        setDescription("[NLS_Japanese] This test verifies if the refresh identity map feature works properly");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    protected void setup() {
        query = (ReadObjectQuery)getSession().getDescriptor(org.eclipse.persistence.testing.tests.nls.japanese.NLSEmployee.class).getQueryManager().getQuery("refreshIdentityMapResultsQuery");
        originalObject = (NLSEmployee)getSession().executeQuery(query);
    }

    public void test() {
        firstName = originalObject.getFirstName();
        //originalObject.setFirstName("Godzilla"); //need to convert to japanese
        originalObject.setFirstName("\u304d\u30db\u30a8\u30cf\u30b1\u30b7\u30b7\u30a2");

        getSession().executeQuery(query);
    }

    protected void verify() {
        if (!(originalObject.getFirstName().equals(firstName))) {
            throw new TestErrorException("The refresh identity map results test failed.");
        }
    }
}
