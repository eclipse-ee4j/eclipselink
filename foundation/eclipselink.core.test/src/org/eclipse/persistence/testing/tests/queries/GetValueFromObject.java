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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.testing.models.inheritance.Car;

/**
 * 2.5.0.6 - this tests if getValueFromObject() throws a nullPointer
 * with a direct query key and object is in the cache.
 */
public class GetValueFromObject extends TestCase {

    public GetValueFromObject() {
        setDescription("This tests if getValueFromObject() throws a nullPointer with a direct query key and object being in the cache.");

    }

    public void test() {
        Car car = (Car)getSession().readObject(Car.class);

        ExpressionBuilder eb = new ExpressionBuilder();
        Expression expr1 = eb.get("id").equal(car.id.intValue());
        Expression expr2 = eb.get("preferredFuel").equal("goff");
        Expression finalExpr = expr1.and(expr2);
        try {
            getSession().readObject(Car.class, finalExpr);
        } catch (EclipseLinkException tlException) {
            throw new TestException("caught exception was: " + tlException);
        }
    }
}
