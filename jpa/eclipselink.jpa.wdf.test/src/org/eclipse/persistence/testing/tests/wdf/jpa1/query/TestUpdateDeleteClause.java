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

public class TestUpdateDeleteClause extends QueryTest {

    @Test
    public void testDelete() {
        // TODO parser/mapper have to be fixed
        /* 0 assertValidQuery("delete FROM Cop c where avg(c.id) > 3.5 and exists(select avg(c1.id) from Cop c1)"); */
        // TODO parser/mapper have to be fixed
        /* 1 assertValidQuery("delete FROM Cop as c where avg(c.id) > 3.5 and exists(select avg(c1.id) from Cop c1)"); */
        /* 2 */assertValidQuery("delete FROM Cop c where exists(select avg(c1.id) from Cop c1)");
        assertValidQuery("DELETE FROM Cop");
        // TODO parser/mapper have to be fixed
        /* 3 assertValidQuery("delete FROM Cop c where avg(c.id) > 3.5 and exists(select avg(c1.id) from Cop c1)"); */
    }

    @Test
    @ToBeInvestigated
    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.OPEN_SQL })
    public void testUpdate() {

        /* 4 */assertInvalidUpdateExecution("update Cop c set p.ianko = 4 where avg(c.id) > 3.5 and exists(select avg(c1.id) from Cop c1)");
        /* 5 */assertInvalidUpdateExecution("update Cop c set C.p.ianko = 4 where avg(c.id) > 3.5 and exists(select avg(c1.id) from Cop c1)");
        assertValidUpdateExecution("UPDATE Cop SET id=1");
        // TODO parser/mapper have to be fixed
        /* 6 assertInvalidQuery("update Cop set C.p.ianko = 4 where avg(c.id) > 3.5 and exists(select avg(c1.id) from Cop c1)"); */
        /* 7 */assertInvalidUpdateExecution("update Cop set partner.id = 4 where avg(c.id) > 3.5 and exists(select avg(c1.id) from Cop c1)");
        // TODO parser/mapper have to be fixed
        /* 8 assertValidQuery("update Cop set id = null where exists(select avg(c1.id) from Cop c1)"); */
        assertValidUpdateExecution("update Cop set id = 1 where exists(select avg(c.id) from Cop c)");

        /* 9 */assertInvalidUpdateExecution("update Cop set id = 'null' where exists(select avg(c1.id) from Cop c1)");

        // TODO parser/mapper have to be fixed
        /* 10 assertValidQuery("update Cop set partner = null"); */
        /* 11 assertValidQuery("update Cop set tesla.integer = 5"); */
        /* 12 assertInvalidQuery("update Cop set partner.partner = null"); */
        /* 13 assertInvalidQuery("update Cop set attachedCriminals = null"); */
    }

    @Test
    public void testUpdateWithArguments() {

        Set<InputParameterHolder> par1 = new HashSet<InputParameterHolder>();
        par1.add(new InputParameterHolder("var2", "string"));
        par1.add(new InputParameterHolder("var1", 2));
        assertValidQueryWithParameters("UPDATE Department d SET d.name=:var2 WHERE d.id=:var1 ", par1);

        par1.clear();
        par1.add(new InputParameterHolder("var1", 1));
        par1.add(new InputParameterHolder("var2", 2));
        assertValidQueryWithParameters("UPDATE Cop c SET c.id=:var1, c.tesla.integer=:var2", par1);

        par1.clear();
        par1.add(new InputParameterHolder("var1", 1));
        par1.add(new InputParameterHolder("var2", 2));
        assertValidQueryWithParameters("UPDATE Cop c SET c.id = :var2 WHERE c.id=:var1", par1);
    }
}
