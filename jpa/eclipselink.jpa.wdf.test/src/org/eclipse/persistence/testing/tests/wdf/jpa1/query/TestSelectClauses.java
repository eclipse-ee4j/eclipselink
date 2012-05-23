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

import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.junit.Test;

public class TestSelectClauses extends QueryTest {

    @Test
    public void testSimpleSelect() {
        /*
         * 00 null, /* 01 null, /* 02 null, /* 03 null, /* 04 "line 1: Path 'c.informers' is not single-valued\n" + "SELECT
         * distinct c.informers FROM Cop c\n" + " ^\n", /* 05 "line 1: Path 'c.informers' is not single-valued\n" + "SELECT
         * c.informers FROM Cop c\n" + " ^\n", /* 06 null, /* 07 null,
         */
        /* 0 */assertValidQuery("SELECT c FROM City c");
        /* 1 */assertValidQuery("SELECT distinct c FROM City c");
        /* 2 */assertValidQuery("SELECT c.partner FROM Cop c");
        /* 3 */assertValidQuery("SELECT distinct c.partner FROM Cop c");
        /* 6 */assertValidQuery("select distinct max(c.id) FROM Cop c");
        /* 7 */assertValidQuery("select distinct Object(c) FROM Cop c");
        /* -- */assertValidQuery("SELECT c FROM Criminal c WHERE c.cType IN (  org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Criminal.CriminalType.NICE)");
        // TODO assure that query is executed on underlying database
        assertValidQueryExecution("SELECT e.department, e.cubicle FROM Employee e");
    }

    @Test
    @ToBeInvestigated
    public void testSimpleSelect4() {
        /* 4 */assertInvalidQuery("SELECT distinct c.informers FROM Cop c");
    }

    @Test
    @ToBeInvestigated
    public void testSimpleSelect5() {
        /* 5 */assertInvalidQuery("SELECT c.informers FROM Cop c");
    }

    @Test
    public void testConstructorExpression08() {
        /* 8 */assertValidQuery("select distinct new  org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Holder(c.id) FROM Cop c");
    }

    @Test
    public void testConstructorExpression09() {
        /* 9 */assertValidQuery("select new  org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Holder(c.id), c1, c2.id, max(c3.id) from Cop c join c.informers c1 join c1.informingCops c2, in (c.attachedCriminals) c3");
    }

    @Test
    public void testConstructorExpression10() {
        /* 10 */assertInvalidQuery("select new  org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Holder_(c.id_), cn, c2._id, max(c3._id) from Cop c join c.informers c1 join c1.informingCops c2, in (c.attachedCriminals) c3");
    }

    @Test
    public void testSubQueries11() {
        /* 11 */assertInvalidQuery("select cit from City cit where exists (select c1 from Cop c, in (c.attachedCriminals) c3)");
    }

    @Test
    public void testSubQueries12() {
        /* 12 */assertInvalidQuery("select cit from City cit where exists (select c2.id from Cop c, in (c.attachedCriminals) c3)");
    }

    @Test
    public void testSubQueries13() {
        /* 13 */assertInvalidQuery("select cit from City cit where exists (select min(c2.id) from Cop c, in (c.attachedCriminals) c3)");
    }

    @Test
    public void testSubQueries14() {
        /* 14 */assertInvalidQuery("select cit from City cit where exists (select sum(c.id_) from Cop c, in (c.attachedCriminals) c3)");
    }

    @Test
    @ToBeInvestigated
    public void testSubQueries15() {
        /* 15 */assertValidQuery("select c.id from Cop c where exists ( select c.id from Cop c where c.id IN (0,1,2,3))");
    }
}
