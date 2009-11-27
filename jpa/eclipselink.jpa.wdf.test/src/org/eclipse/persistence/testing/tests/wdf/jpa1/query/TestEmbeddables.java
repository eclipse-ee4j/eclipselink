/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.tests.wdf.jpa1.query;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.junit.Test;

public class TestEmbeddables extends QueryTest {

    @Test
    @ToBeInvestigated
    public void testEmbeddables0() {
        /* 0 */assertInvalidQuery("SELECT c FROM Cop c where c.tesla = c.tesla");
    }

    @Test
    public void testEmbeddables1() {
        /* 1 */assertValidQuery("SELECT c FROM Cop c where c.tesla.integer = c.tesla.integer");
    }

    @Test
    public void testEmbeddables2() {
        /* 2 */assertValidQuery("SELECT c FROM Cop c where c.tesla is null");
    }

    @Test
    @ToBeInvestigated
    public void testEmbeddables3() {
        /* 3 */assertInvalidQuery("SELECT c FROM City c where c.tesla = c.tesla");
    }

    @Test
    public void testEmbeddables4() {
        /* 4 */assertValidQuery("SELECT c FROM City c where c.tesla.integer = c.tesla.integer");
    }

    @Test
    public void testEmbeddables5() {
        /* 5 */assertValidQuery("SELECT c FROM City c where c.tesla is not null");
    }

    @Test
    public void testEmbeddables6() {
        /* 6 */assertValidQuery("SELECT c FROM Cop c where c.tesla.integer = 5");
    }

    @Test
    public void testEmbeddables7() {
        /* 7 */assertValidQuery("SELECT c FROM Cop c where 5 = c.tesla.integer");
    }

    @Test
    public void testBlobs() {
        Set<InputParameterHolder> par1 = new HashSet<InputParameterHolder>();
        par1.add(new InputParameterHolder("var1", new byte[10]));
        assertValidQueryWithParameters(
                "SELECT bfa.primitiveByteArray2Blob FROM BasicTypesFieldAccess bfa WHERE bfa.primitiveByteArray2Blob=:var1",
                par1);

        assertValidQuery("SELECT c.tesla.sound FROM City c WHERE c.id = 1");
    }
}
