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
package org.eclipse.persistence.testing.tests.queries.inmemory;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.ReadObjectTest;
import org.eclipse.persistence.testing.models.insurance.HouseClaim;

/**
 * <P>
 * <B>Purpose</B>: <P>
 *
 * <B>Motivation</B>: <P>
 *
 * <B>Design</B>: <P>
 *
 * <B>Responsibilities</B>: <P>
 *
 * <B>Features Used</B>:
 * <UL>
 *     <LI>
 * </UL>
 *
 * <B>Paths Covered</B>:
 *
 * @author         Rick Barkhouse
 * @version        18 August 1999
 */
public class CacheHitWithNonPKCriteriaTest extends ReadObjectTest {
    public CacheHitWithNonPKCriteriaTest() {
        super();
        setName("CacheHitWithNonPKCriteriaTest");
        setDescription("Test whether querying on PK plus additional fields (erroneously) results in a cache hit.");
    }

    public CacheHitWithNonPKCriteriaTest(Object originalObject) {
        super(originalObject);
        setName("CacheHitWithNonPKCriteriaTest");
        setDescription("Test whether querying on PK plus additional fields (erroneously) results in a cache hit.");
    }

    protected void setup() {
    }

    protected void test() {
        Expression exp1;
        Expression exp2;
        ExpressionBuilder builder1;
        ExpressionBuilder builder2;
        ReadObjectQuery query1;
        ReadObjectQuery query2;

        // Read in a HouseClaim
        builder1 = new ExpressionBuilder();
        exp1 = builder1.get("id").equal(100);
        query1 = new ReadObjectQuery(org.eclipse.persistence.testing.models.insurance.HouseClaim.class, exp1);
        HouseClaim claim = (HouseClaim)getSession().executeQuery(query1);

        // Read in HouseClaim with same id, but different amount
        builder2 = new ExpressionBuilder();
        exp2 = builder2.get("id").equal(100);
        exp2 = exp2.and(builder2.get("amount").notEqual(claim.getAmount()));
        query2 = new ReadObjectQuery(org.eclipse.persistence.testing.models.insurance.HouseClaim.class, exp2);

        // This should be null
        objectFromDatabase = getSession().executeQuery(query2);
    }

    protected void verify() {
        if (originalObject != null) {
            throw new TestErrorException("Cache hit occurred when querying on PK plus additional field.");
        }
    }
}
