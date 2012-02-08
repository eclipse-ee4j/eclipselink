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

import org.eclipse.persistence.testing.framework.wdf.Skip;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.junit.Test;

public class TestGroupByOrderByHaving extends QueryTest {

    @Test
    @ToBeInvestigated
    public void testOrderBy0() {
        /* 0 */assertInvalidQuery("SELECT c FROM City c order by c.type");
    }

    @Test
    public void testOrderBy1() {
        /* 1 */assertValidQuery("SELECT c FROM City c order by c.id");
    }

    @Test
    public void testOrderBy2() {
        /* 2 */assertInvalidQuery("SELECT c FROM City c order by c.cops");
    }

    @Test
    public void testOrderBy3() {
        /* 3 */assertInvalidQuery("SELECT c, max(c.id) FROM City c order by c.id");
    }

    @Test
    @ToBeInvestigated
    public void testOrderBy4() {
        /* 4 */assertInvalidQuery("SELECT c, new  org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Holder(c.id) FROM City c order by c.id");
    }

    @Test
    @ToBeInvestigated
    public void testOrderBy5() {
        /* 5 */assertInvalidQuery("select c, p.string from Person p, Cop c order by p.integer");
    }

    @Test
    public void testOrderBy6() {
        /* 6 */assertValidQuery("select c, p from Person p, Cop c order by p.integer");
    }

    @Test
    public void testOrderBy7() {
        /* 7 */assertValidQuery("select c.partner, p from Person p, Cop c order by c.partner.id");
    }

    @Test
    public void testOrderBy8() {
        assertValidQuery("select c, p from Person p, Cop c order by c.partner.id");
    }

    @Test
    public void testOrderBy9() {
        assertValidQueryExecution("select t from Task t order by t.projectId");
    }

    @Test
    public void testGroupBy0() {
        /* 9 */assertValidQuery("select c, p.id from Person p, Cop c group by c, p.id");
    }

    @Test
    public void testGroupBy1() {
        /* 10 */assertValidQuery("select c, p.id from Person p, Cop c group by c");
    }

    @Test
    public void testGroupBy2() {
        /* 11 */assertValidQuery("select c, p.id from Person p, Cop c group by p.id");
    }

    @Test
    public void testGroupBy3() {
        /* 12 */assertValidQuery("select p.id from Person p, Cop c group by c, p.id");
    }

    @Test
    public void testGroupBy4() {
        assertValidQuery("select c from Person p, Cop c group by c, p.id");
    }

    @Test
    public void testGroupBy5() {
        /* 14 */assertValidQuery("select max(p.integer), c, min(p.string), p.id from Person p, Cop c group by c, p.id");
    }

    @Test
    public void testGroupBy6() {
        assertValidQueryExecution("SELECT p,stadt FROM Person p join p.city AS stadt ORDER BY p.id DESC, stadt.name ASC");
    }

    @Test
    public void testGroupBy7() {
        // TODO check if query is meaningful
        assertValidQuery("SELECT c, new  org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Holder(c.id) FROM City c group by c.id");
    }

    @Test
    @ToBeInvestigated
    public void testGroupBy8() {
        /* 16 */assertInvalidQuery("select c.partner.informers, p.id from Person p, Cop c group by c.partner.informers, p.id");
    }

    @Test
    public void testGroupBy9() {
        /* 17 */assertValidQuery("select c, p.id from Person p, Cop c group by c, p.id having p.id = 5 order by p.id");
    }

    @Test
    public void testSubQueryGroupBy0() {
        /* 18 */assertValidQuery("select _city from City _city where exists(select c from Cop c group by c, c.id, c.tesla)");
    }

    @Test
    public void testSubQueryGroupBy1() {
        /* 19 */assertValidQuery("select _city from City _city where exists(select c from Cop c group by c, c, c having c.tesla is not null)");
    }

    @Test
    public void testSubQueryGroupBy2() {
        /* 20 */assertValidQuery("select _city from City _city where exists(select c.id from Cop c group by c.id having c.partner.id = 5)");
    }

    @Test
    @Skip(databaseNames="org.eclipse.persistence.platform.database.MaxDBPlatform") 
	/*
	 * On MaxDB, the query maps to
	 * "SELECT t0.ID, t0.COOL, t0.NAME, t0.TYPE, t0.CITY_ENUM, t0.CITY_TESLA_INT, t0.CITY_TESLA_BLOB FROM TMP_CITY t0 WHERE EXISTS (SELECT 1 FROM TMP_COP t2, TMP_COP t1 WHERE (t2.ID = t1.PARTNER_ID) GROUP BY t1.ID HAVING (t2.ID = 5))"
	 * . The query is invalid (as expected) and should fail on the database as
	 * t2.ID is no grouping column and must not be used in HAVING.
	 */
    public void testSubQueryGroupBy3() {
        /* 21 */assertInvalidQuery("select _city from City _city where exists(select max(c.id) from Cop c group by c.id having c.partner.id = 5)");
    }

    @Test
    public void testSubQueryGroupBy4() {
        /* 22 */assertValidQuery("select _city from City _city where exists(select c from Cop c group by c.id)");
    }

    @Test
    @ToBeInvestigated
    public void testSubQueryGroupBy5() {
        /* 23 */assertInvalidQuery("select _city from City _city where exists(select c.tesla from Cop c group by c.id)");
    }

    @Test
    @ToBeInvestigated
    public void testConstructorGroupBy0() {
        assertValidQuery("SELECT new  org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Holder(count(p)) FROM City c, Person p group by c.id");
    }

    @Test
    public void testConstructorGroupBy1() {
        /* 25 */assertValidQuery("SELECT new  org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Holder(max(c.id)) FROM City c group by c.id");
    }
}
