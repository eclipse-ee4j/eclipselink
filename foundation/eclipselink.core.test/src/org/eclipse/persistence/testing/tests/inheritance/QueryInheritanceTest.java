/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.inheritance;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.inheritance.*;

public class QueryInheritanceTest extends TestCase {


    public QueryInheritanceTest() {
        setDescription("Verifies that Named queries are inheritedd correctly");
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getAbstractSession().beginTransaction();
    }

    public void test() {
        Vector vector = (Vector)getSession().executeQuery("InheritanceReadAll", Dog.class);
        if (vector.isEmpty()){
            throw new TestProblemException("No Dogs found at all");
        }
        for (Iterator iterator = vector.iterator(); iterator.hasNext(); ){
            if ( ! (iterator.next() instanceof Dog) ){
                throw new TestErrorException("Failed to inherit query correctly");
            }
        }
    }

}
