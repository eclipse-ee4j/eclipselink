/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.inheritance;

import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.inheritance.Insect;

public class JoinWithSecondaryTableTest extends org.eclipse.persistence.testing.framework.TestCase {

    public JoinWithSecondaryTableTest() {
        setDescription("Performs a query on a joined inheritance superclass which has a joined attribute.  Bug6111278");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
    }

    public void test() {
    	try{
    		getSession().readAllObjects(Insect.class);
    	}catch (Exception exception){
    		throw new TestErrorException("Query on joined inheritance class with join failed");
    	}
    }

    public void verify() {
    }
}