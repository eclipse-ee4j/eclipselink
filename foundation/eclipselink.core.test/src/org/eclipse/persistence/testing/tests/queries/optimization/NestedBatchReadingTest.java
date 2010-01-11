/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.queries.optimization;

import java.util.*;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.insurance.*;

public class NestedBatchReadingTest extends TestCase {
    public Vector result;

    public NestedBatchReadingTest() {
        setDescription("Tests batch reading nesting across two 1-m mappings, polcies and claims");
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(PolicyHolder.class);
        query.addBatchReadAttribute("policies");
        query.addBatchReadAttribute(query.getExpressionBuilder().get("policies").get("claims"));
        result = (Vector)getSession().executeQuery(query);
    }

    public void verify() {
        boolean foundClaims = false;
        for (Enumeration holdersEnum = result.elements(); holdersEnum.hasMoreElements(); ) {
            for (Enumeration policiesEnum = ((PolicyHolder)holdersEnum.nextElement()).getPolicies().elements(); 
                 policiesEnum.hasMoreElements(); ) {
                if (!((Policy)policiesEnum.nextElement()).getClaims().isEmpty()) {
                    foundClaims = true;
                }
            }
        }

        if (!foundClaims) {
            throw new TestErrorException("claims were not bacthed correctly.");
        }
    }
}
