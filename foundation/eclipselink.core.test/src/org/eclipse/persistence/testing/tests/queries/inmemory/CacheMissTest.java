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

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Test for cache on 1-1 traversal.
 *
 */
public class CacheMissTest extends CacheHitTest {
    protected Employee employee;

    public CacheMissTest() {
        setDescription("Test cache miss for non-primary key query.");
        originalObject = PopulationManager.getDefaultManager().getObject(Employee.class, "0001");
    }

    /**
     * Query the object by primary key.
     */
    protected Object readObject() {
        return getSession().readObject(Employee.class, new ExpressionBuilder().get("id").notEqual(((Employee)objectToRead).getId()));
    }

    protected void verify() {
        if (objectRead == objectToRead) {
            throw new TestErrorException("Object read match but should not.");
        }

        if (tempStream.toString().length() == 0) {
            throw new TestErrorException("Cache hit occurred, but should not have.");
        }
    }
}
