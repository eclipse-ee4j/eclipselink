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

public class TestAggregates extends QueryTest {

    @Test
    public void testAvgHandling1() {
        /*
         * 00 null, /* 01 null, /* 02 "line 1: Path 'c.informers' is not a CMP path\n" +
         * "SELECT avg(distinct c.informers) FROM Cop c where avg(distinct c.partner) > 3.5 and exists(select avg(c1.partner) from Cop c1)\n"
         * + "                    ^\n" + "line 1: Path 'c.informers' is not of numeric type (Informer)\n" +
         * "SELECT avg(distinct c.informers) FROM Cop c where avg(distinct c.partner) > 3.5 and exists(select avg(c1.partner) from Cop c1)\n"
         * + "                    ^\n" + "line 1: Path 'c.partner' is not a CMP path\n" +
         * "SELECT avg(distinct c.informers) FROM Cop c where avg(distinct c.partner) > 3.5 and exists(select avg(c1.partner) from Cop c1)\n"
         * + "                                                               ^\n" +
         * "line 1: Path 'c.partner' is not of numeric type (Cop)\n" +
         * "SELECT avg(distinct c.informers) FROM Cop c where avg(distinct c.partner) > 3.5 and exists(select avg(c1.partner) from Cop c1)\n"
         * + "                                                               ^\n" +
         * "line 1: Path 'c1.partner' is not a CMP path\n" +
         * "SELECT avg(distinct c.informers) FROM Cop c where avg(distinct c.partner) > 3.5 and exists(select avg(c1.partner) from Cop c1)\n"
         * + "                                                                                                      ^\n" +
         * "line 1: Path 'c1.partner' is not of numeric type (Cop)\n" +
         * "SELECT avg(distinct c.informers) FROM Cop c where avg(distinct c.partner) > 3.5 and exists(select avg(c1.partner) from Cop c1)\n"
         * + "                                                                                                      ^\n",
         */
        // TODO parser/mapper have to be fixed
        /* 0 assertValidQuery("SELECT avg(c.id) FROM Cop c where avg(c.id) > 3.5 and exists(select avg(c1.id) from Cop c1)"); */

        // TODO parser/mapper have to be fixed
        /*
         * 1assertValidQuery(
         * "SELECT avg(distinct c.id) FROM Cop c where avg(distinct c.id) > 3.5 and exists(select avg(distinct c1.id) from Cop c1)"
         * );
         */

        assertValidQueryExecution("SELECT e.firstname, avg(e.salary) FROM Employee e GROUP BY e.firstname HAVING avg(e.salary) > 2000");
    }

    @Test
    public void testAvgHandling2() {
        /* 2 */assertInvalidQuery("SELECT avg(distinct c.informers) FROM Cop c where avg(distinct c.partner) > 3.5 and exists(select avg(c1.partner) from Cop c1)");
    }

    @Test
    public void testMaxHandling() {
        /*
         * 03 null, /* 04 null, /* 05 "line 1: Path 'c.informers' is not a CMP path\n" +
         * "SELECT max(distinct c.informers) FROM Cop c where max(distinct c.partner) > 3.5 and exists(select max(c1.partner) from Cop c1)\n"
         * + "                    ^\n" + "line 1: No order defined on type (Informer) of path 'c.informers'\n" +
         * "SELECT max(distinct c.informers) FROM Cop c where max(distinct c.partner) > 3.5 and exists(select max(c1.partner) from Cop c1)\n"
         * + "                    ^\n" + "line 1: Path 'c.partner' is not a CMP path\n" +
         * "SELECT max(distinct c.informers) FROM Cop c where max(distinct c.partner) > 3.5 and exists(select max(c1.partner) from Cop c1)\n"
         * + "                                                               ^\n" +
         * "line 1: No order defined on type (Cop) of path 'c.partner'\n" +
         * "SELECT max(distinct c.informers) FROM Cop c where max(distinct c.partner) > 3.5 and exists(select max(c1.partner) from Cop c1)\n"
         * + "                                                               ^\n" +
         * "line 1: Comparison '>' not defined for entity beans\n" +
         * "SELECT max(distinct c.informers) FROM Cop c where max(distinct c.partner) > 3.5 and exists(select max(c1.partner) from Cop c1)\n"
         * + "                                                                          ^\n" +
         * "line 1: Comparison of incompatible types: (Cop) '>' (java.math.BigDecimal)\n" +
         * "SELECT max(distinct c.informers) FROM Cop c where max(distinct c.partner) > 3.5 and exists(select max(c1.partner) from Cop c1)\n"
         * + "                                                                          ^\n" +
         * "line 1: Path 'c1.partner' is not a CMP path\n" +
         * "SELECT max(distinct c.informers) FROM Cop c where max(distinct c.partner) > 3.5 and exists(select max(c1.partner) from Cop c1)\n"
         * + "                                                                                                      ^\n" +
         * "line 1: No order defined on type (Cop) of path 'c1.partner'\n" +
         * "SELECT max(distinct c.informers) FROM Cop c where max(distinct c.partner) > 3.5 and exists(select max(c1.partner) from Cop c1)\n"
         * + "                                                                                                      ^\n",
         */
        // TODO parser/mapper have to be fixed
        /* 3 assertValidQuery("SELECT max(c.id) FROM Cop c where max(c.id) > 3.5 and exists(select max(c1.id) from Cop c1)"); */

        // TODO parser/mapper have to be fixed
        /*
         * 4assertValidQuery(
         * "SELECT max(distinct c.id) FROM Cop c where max(distinct c.id) > 3.5 and exists(select max(distinct c1.id) from Cop c1)"
         * );
         */

        /* 5 */assertInvalidQuery("SELECT max(distinct c.informers) FROM Cop c where max(distinct c.partner) > 3.5 and exists(select max(c1.partner) from Cop c1)");
    }

    @Test
    public void testMinHandling() {
        /*
         * 06 null, /* 07 null, /* 08 "line 1: Path 'c.informers' is not a CMP path\n" +
         * "SELECT min(distinct c.informers) FROM Cop c where min(distinct c.partner) > 3.5 and exists(select min(c1.partner) from Cop c1)\n"
         * + "                    ^\n" + "line 1: No order defined on type (Informer) of path 'c.informers'\n" +
         * "SELECT min(distinct c.informers) FROM Cop c where min(distinct c.partner) > 3.5 and exists(select min(c1.partner) from Cop c1)\n"
         * + "                    ^\n" + "line 1: Path 'c.partner' is not a CMP path\n" +
         * "SELECT min(distinct c.informers) FROM Cop c where min(distinct c.partner) > 3.5 and exists(select min(c1.partner) from Cop c1)\n"
         * + "                                                               ^\n" +
         * "line 1: No order defined on type (Cop) of path 'c.partner'\n" +
         * "SELECT min(distinct c.informers) FROM Cop c where min(distinct c.partner) > 3.5 and exists(select min(c1.partner) from Cop c1)\n"
         * + "                                                               ^\n" +
         * "line 1: Comparison '>' not defined for entity beans\n" +
         * "SELECT min(distinct c.informers) FROM Cop c where min(distinct c.partner) > 3.5 and exists(select min(c1.partner) from Cop c1)\n"
         * + "                                                                          ^\n" +
         * "line 1: Comparison of incompatible types: (Cop) '>' (java.math.BigDecimal)\n" +
         * "SELECT min(distinct c.informers) FROM Cop c where min(distinct c.partner) > 3.5 and exists(select min(c1.partner) from Cop c1)\n"
         * + "                                                                          ^\n" +
         * "line 1: Path 'c1.partner' is not a CMP path\n" +
         * "SELECT min(distinct c.informers) FROM Cop c where min(distinct c.partner) > 3.5 and exists(select min(c1.partner) from Cop c1)\n"
         * + "                                                                                                      ^\n" +
         * "line 1: No order defined on type (Cop) of path 'c1.partner'\n" +
         * "SELECT min(distinct c.informers) FROM Cop c where min(distinct c.partner) > 3.5 and exists(select min(c1.partner) from Cop c1)\n"
         * + "                                                                                                      ^\n",
         */

        // TODO parser/mapper have to be fixed
        /* 6 assertValidQuery("SELECT min(c.id) FROM Cop c where min(c.id) > 3.5 and exists(select min(c1.id) from Cop c1)"); */

        // TODO parser/mapper have to be fixed
        /*
         * 7assertValidQuery(
         * "SELECT min(distinct c.id) FROM Cop c where min(distinct c.id) > 3.5 and exists(select min(distinct c1.id) from Cop c1)"
         * );
         */
        /* 8 */assertInvalidQuery("SELECT min(distinct c.informers) FROM Cop c where min(distinct c.partner) > 3.5 and exists(select min(c1.partner) from Cop c1)");
    }

    @Test
    public void testSumHandling() {
        /*
         * 09 null, /* 10 null, /* 11 "line 1: Path 'c.informers' is not a CMP path\n" +
         * "SELECT sum(distinct c.informers) FROM Cop c where sum(distinct c.partner) > 3.5 and exists(select sum(c1.partner) from Cop c1)\n"
         * + "                    ^\n" + "line 1: Path 'c.informers' is not of numeric type (Informer)\n" +
         * "SELECT sum(distinct c.informers) FROM Cop c where sum(distinct c.partner) > 3.5 and exists(select sum(c1.partner) from Cop c1)\n"
         * + "                    ^\n" + "line 1: Path 'c.partner' is not a CMP path\n" +
         * "SELECT sum(distinct c.informers) FROM Cop c where sum(distinct c.partner) > 3.5 and exists(select sum(c1.partner) from Cop c1)\n"
         * + "                                                               ^\n" +
         * "line 1: Path 'c.partner' is not of numeric type (Cop)\n" +
         * "SELECT sum(distinct c.informers) FROM Cop c where sum(distinct c.partner) > 3.5 and exists(select sum(c1.partner) from Cop c1)\n"
         * + "                                                               ^\n" +
         * "line 1: Path 'c1.partner' is not a CMP path\n" +
         * "SELECT sum(distinct c.informers) FROM Cop c where sum(distinct c.partner) > 3.5 and exists(select sum(c1.partner) from Cop c1)\n"
         * + "                                                                                                      ^\n" +
         * "line 1: Path 'c1.partner' is not of numeric type (Cop)\n" +
         * "SELECT sum(distinct c.informers) FROM Cop c where sum(distinct c.partner) > 3.5 and exists(select sum(c1.partner) from Cop c1)\n"
         * + "                                                                                                      ^\n",
         */
        // TODO parser/mapper have to be fixed
        /* 9 assertValidQuery("SELECT sum(c.id) FROM Cop c where sum(c.id) > 3.5 and exists(select sum(c1.id) from Cop c1)"); */
        /*
         * 10assertValidQuery(
         * "SELECT sum(distinct c.id) FROM Cop c where sum(distinct c.id) > 3.5 and exists(select sum(distinct c1.id) from Cop c1)"
         * );
         */

        /*
         * 11assertInvalidQuery(
         * "SELECT sum(distinct c.informers) FROM Cop c where sum(distinct c.partner) > 3.5 and exists(select sum(c1.partner) from Cop c1)"
         * );
         */
    }

    @Test
    @ToBeInvestigated
    public void testCountHandling() {
        /*
         * 12 null, /* 13 null, /* 14 "line 1: Path 'c.informers' is neither a CMP path nor a single valued CMR path\n" +
         * "SELECT count(distinct c.informers) FROM Cop c where count(distinct c.informers) > 3.5 and exists(select count(c1.informers) from Cop c1)\n"
         * + "                      ^\n" + "line 1: Path 'c.informers' is neither a CMP path nor a single valued CMR path\n" +
         * "SELECT count(distinct c.informers) FROM Cop c where count(distinct c.informers) > 3.5 and exists(select count(c1.informers) from Cop c1)\n"
         * + "                                                                   ^\n" +
         * "line 1: Path 'c1.informers' is neither a CMP path nor a single valued CMR path\n" +
         * "SELECT count(distinct c.informers) FROM Cop c where count(distinct c.informers) > 3.5 and exists(select count(c1.informers) from Cop c1)\n"
         * +
         * "                                                                                                              ^\n",
         * /* 15 null,
         */

        // TODO parser/mapper handling have to be fixed
        /* 12 */assertValidQuery("SELECT count(c.id) FROM Cop c where count(c.id) > 3.5 and exists(select count(c1.id) from Cop c1)"); /**/
        /*
         * 13assertValidQuery(
         * "SELECT count(distinct c.partner) FROM Cop c where count(distinct c.partner) > 3.5 and exists(select count(distinct c1.partner) from Cop c1)"
         * );
         */
        /*
         * 14assertInvalidQuery(
         * "SELECT count(distinct c.informers) FROM Cop c where count(distinct c.informers) > 3.5 and exists(select count(c1.informers) from Cop c1)"
         * );
         */
        /* 15 assertValidQuery("SELECT count(c) FROM Cop c where count(c) > 3.5 and exists(select count(c1) from Cop c1)"); */
    }
}
