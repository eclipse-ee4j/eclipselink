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
package org.eclipse.persistence.testing.tests.queries.inmemory;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.ReadObjectTest;

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
 * @version        19 August 1999
 */
public class CacheHitOnPKWithInheritanceTest extends ReadObjectTest {
    public CacheHitOnPKWithInheritanceTest() {
        super();
        setName("CacheHitOnPKWithInheritanceTest");
        setDescription("Test whether querying on PK (erroneously) results in a cache hit when using inheritance.");
    }

    public CacheHitOnPKWithInheritanceTest(Object originalObject) {
        super(originalObject);
        setName("CacheHitOnPKWithInheritanceTest");
        setDescription("Test whether querying on PK (erroneously) results in a cache hit when using inheritance.");
    }

    protected void setup() {
    }

    protected void test() {
        Expression exp1;
        ExpressionBuilder builder1;
        ReadObjectQuery query1;

        // Read in a SmallProject
        SmallProject sProject = (SmallProject)getSession().readObject(SmallProject.class);

        // Try to read in a LargeProject with an ID == sProject's ID
        builder1 = new ExpressionBuilder();
        exp1 = builder1.get("id").equal(sProject.getId());
        query1 = new ReadObjectQuery(LargeProject.class, exp1);

        // This should be null
        objectFromDatabase = getSession().executeQuery(query1);
    }

    protected void verify() {
        if (originalObject != null) {
            throw new TestErrorException("A LargeProject was read in using a SmallProject's ID (cache hit).");
        }
    }
}
