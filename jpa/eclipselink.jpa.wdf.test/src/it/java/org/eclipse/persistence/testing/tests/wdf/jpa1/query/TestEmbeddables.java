/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     SAP - initial API and implementation

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
