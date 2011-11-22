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

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.TemporalType;

import org.eclipse.persistence.testing.framework.wdf.Bugzilla;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.junit.Test;

public class TestConditionalExpressions extends QueryTest {

    @Test
    public void testNumericComparisons() {
        assertValidQuery("SELECT c FROM City c WHERE 5 > 6");
        assertValidQuery("SELECT c FROM City c WHERE 5 < 6");
        assertValidQuery("SELECT c FROM City c WHERE 5 >= 6");
        assertValidQuery("SELECT c FROM City c WHERE 5 <= 6");
        assertValidQuery("SELECT c FROM City c WHERE 5 = 6");
        assertValidQuery("SELECT c FROM City c WHERE 5 <> 6");
    }

    @Test
    @ToBeInvestigated
    public void testEnumComparison0() {
        assertInvalidQuery("SELECT c FROM City c WHERE c.type < org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.RUSH_PIGSTY");
    }

    @Test
    @ToBeInvestigated
    public void testEnumComparison1() {
        assertInvalidQuery("SELECT c FROM City c WHERE c.type >= org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.RUSH_PIGSTY");
    }

    @Test
    @ToBeInvestigated
    public void testEnumComparison2() {
        assertInvalidQuery("SELECT c FROM City c WHERE c.type <= org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.RUSH_PIGSTY");
    }

    @Test
    public void testEnumComparison3() {
        assertValidQuery("SELECT c FROM City c WHERE c.type = org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.RUSH_PIGSTY");
    }

    @Test
    public void testEnumComparison4() {
        assertValidQuery("SELECT c FROM City c WHERE c.type <> org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.RUSH_PIGSTY");
    }

    @Test
    public void testEnumComparison5() {
        assertValidQuery("SELECT c FROM City c WHERE c.id IN (SELECT min(c2.id) FROM City c2 WHERE c2.cityEnum = org.eclipse.persistence.testing.models.wdf.jpa1.jpql.City.CityEnum.BLI)");
    }

    @Test
    @ToBeInvestigated
    public void testBooleanComparison0() {
        assertInvalidQuery("SELECT c FROM City c WHERE c.cool > true");
    }

    @Test
    @ToBeInvestigated
    public void testBooleanComparison1() {
        assertInvalidQuery("SELECT c FROM City c WHERE c.cool < true");
    }

    @Test
    @ToBeInvestigated
    public void testBooleanComparison2() {
        assertInvalidQuery("SELECT c FROM City c WHERE c.cool >= true");
    }

    @Test
    @ToBeInvestigated
    public void testBooleanComparison3() {
        assertInvalidQuery("SELECT c FROM City c WHERE c.cool <= true");
    }

    @Test
    public void testBooleanComparison4() {
        assertValidQuery("SELECT c FROM City c WHERE c.cool = true");
    }

    @Test
    public void testBooleanComparison5() {
        assertValidQuery("SELECT c FROM City c WHERE c.cool <> true");
    }

    @Test
    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.OPEN_SQL,
    // DatabaseVendor.ORACLE })
    public void testCurrentDateTimeComparison() {
        assertValidQuery("SELECT c FROM City c WHERE current_time > current_time");
        assertValidQuery("SELECT c FROM City c WHERE current_time < current_time");
        assertValidQuery("SELECT c FROM City c WHERE current_date >= current_date");
        assertValidQuery("SELECT c FROM City c WHERE current_timestamp <= current_timestamp");
        assertValidQuery("SELECT c FROM City c WHERE current_time = current_time");
        assertValidQuery("SELECT c FROM City c WHERE current_time <> current_time");
    }

    @Test
    public void testPersistentObjectComparison0() {
        assertInvalidQuery("SELECT c FROM City c WHERE c > c");
    }

    @Test
    public void testPersistentObjectComparison1() {
        assertInvalidQuery("SELECT c FROM City c WHERE c < c");
    }

    @Test
    public void testPersistentObjectComparison2() {
        assertInvalidQuery("SELECT c FROM City c WHERE c >= c");
    }

    @Test
    public void testPersistentObjectComparison3() {
        assertInvalidQuery("SELECT c FROM City c WHERE c <= c");
    }

    @Test
    public void testPersistentObjectComparison4() {
        assertValidQuery("SELECT c FROM City c WHERE c = c");
    }

    @Test
    public void testPersistentObjectComparison5() {
        assertValidQuery("SELECT c FROM City c WHERE c <> c");
    }

    @Test
    public void testSimpleTypeComparisonHandling() {
        Set<InputParameterHolder> par1 = new HashSet<InputParameterHolder>();
        par1.add(new InputParameterHolder("parameter1", Calendar.getInstance(), TemporalType.TIMESTAMP));
        assertValidQueryWithParameters("SELECT btfa FROM BasicTypesFieldAccess btfa WHERE btfa.utilCalendar = :parameter1",
                par1);

        assertValidQuery("SELECT c FROM City c WHERE (1 + (2 + 3) / 1 * 2) - 5 > -1");
        assertValidQuery("SELECT c FROM City c WHERE 'bla' = (SELECT p.string FROM Person p)");
        assertValidQuery("SELECT c FROM City c WHERE 'bla' = ALL(SELECT p.string FROM Person p)");
        assertValidQuery("SELECT p FROM Person p WHERE 'bla' = p.string");
        assertInvalidQuery("SELECT p FROM Person p WHERE 'bla' = all.com.sap.ejbqlparser.test.Scale.STILL_ROCK");
        assertInvalidQuery("SELECT p FROM Person p WHERE 'bla' = p.integer.p");
        assertValidQuery("SELECT p FROM Person p WHERE 'bla' = ?1");
        assertValidQuery("SELECT p FROM Person p WHERE 'bla' = :one");
        assertInvalidQuery("SELECT p FROM Person p WHERE 'bla' = org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.STILL_ROCK");
        assertInvalidQuery("SELECT p FROM Person p WHERE 'bla' = org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.NO_FIELD");
        assertInvalidQuery("SELECT p FROM Person p WHERE 'bla' = MAX(p._float)");
        assertInvalidQuery("SELECT p FROM Person p WHERE 'bla' = AVG(p.integer)");
        assertInvalidQuery("SELECT p FROM Person p WHERE 'bla' = SUM(p.bigInteger)");
        assertInvalidQuery("SELECT p FROM Person p WHERE 'bla' = SUM(p.integer)");
        assertInvalidQuery("SELECT p FROM Person p WHERE 'bla' = SUM(p._float)");
        assertInvalidQuery("SELECT p FROM Person p WHERE 'bla' = COUNT(p._float)");
        assertValidQuery("SELECT c FROM City c WHERE (SELECT p.string FROM Person p) = 'bla'");
        assertValidQuery("SELECT p FROM Person p HAVING 'bla' = MIN(p.string)");
        assertValidQuery("SELECT p FROM Person p WHERE p.string = 'bla'");
        assertInvalidQuery("SELECT p FROM Person p WHERE all.com.sap.ejbqlparser.test.Scale.STILL_ROCK = 'bla'");
        assertInvalidQuery("SELECT p FROM Person p WHERE p.integer.p = 'bla'");
        assertValidQuery("SELECT p FROM Person p WHERE ?1 = 'bla'");
        assertValidQuery("SELECT p FROM Person p WHERE :one = 'bla'");
        assertInvalidQuery("SELECT p FROM Person p WHERE org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.STILL_ROCK = 'bla'");
        assertInvalidQuery("SELECT p FROM Person p WHERE org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.NO_FIELD = 'bla'");
        assertInvalidQuery("SELECT p FROM Person p WHERE MAX(p._float) = 'bla'");
        assertInvalidQuery("SELECT p FROM Person p WHERE AVG(p.integer) = 'bla'");
        assertInvalidQuery("SELECT p FROM Person p WHERE SUM(p.bigInteger) = 'bla'");
        assertInvalidQuery("SELECT p FROM Person p WHERE SUM(p.integer) = 'bla'");
        assertInvalidQuery("SELECT p FROM Person p WHERE SUM(p._float) = 'bla'");
        assertInvalidQuery("SELECT p FROM Person p WHERE COUNT(p._float) = 'bla'");

        // TODO mapper: boolean literal in comparison with subquery
        // assertValidQuery("SELECT c FROM City c WHERE (SELECT p._boolean FROM Person p) = true");
        // assertValidQuery("SELECT c FROM City c WHERE (SELECT p._Boolean FROM Person p) = true");
        // assertValidQuery("SELECT c FROM City c WHERE ALL(SELECT p._Boolean FROM Person p) = true");
        // assertValidQuery("SELECT c FROM City c WHERE SOME(SELECT p._boolean FROM Person p) = true");

        // TODO: mapper/parser: boolean comparison with input parameter
        // assertValidQuery("SELECT p FROM Person p WHERE true = ?1");
        // assertValidQuery("SELECT p FROM Person p WHERE true = :one");
        assertValidQuery("SELECT p FROM Person p WHERE true = p._boolean");
        assertValidQuery("SELECT p FROM Person p WHERE true = p._Boolean");
        // assertValidQuery("SELECT p FROM Person p WHERE ?1 = false");
        // assertValidQuery("SELECT p FROM Person p WHERE :one = false");

        assertValidQuery("SELECT p FROM Person p WHERE p._boolean = false");
        assertValidQuery("SELECT p FROM Person p WHERE p._Boolean = false");

        assertInvalidQuery("SELECT p FROM Person p WHERE (SELECT p1.integer FROM Person p1) + 1 = 2");
        assertInvalidQuery("SELECT p FROM Person p WHERE 1 + (SELECT p1.integer FROM Person p1) = 2");
        assertInvalidQuery("SELECT p FROM Person p WHERE -(SELECT p1.integer FROM Person p1) > 2");

        // TODO fix enum-handling in mapper
        // assertValidQuery("SELECT c FROM City c WHERE org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.RUSH_PIGSTY <> ALL(SELECT c1.type FROM City c1)");
        // assertValidQuery("SELECT c FROM City c WHERE org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.RUSH_PIGSTY <> (SELECT c1.type FROM City c1)");
        // assertValidQuery("SELECT c FROM City c WHERE org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.RUSH_PIGSTY <> c.type");
        // assertValidQuery("SELECT c FROM City c WHERE org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.RUSH_PIGSTY <> ?1");
        // assertValidQuery("SELECT c FROM City c WHERE org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.RUSH_PIGSTY <> :one");
        // assertValidQuery("SELECT c FROM City c WHERE (SELECT c1.type FROM City c1) <> org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.RUSH_PIGSTY");

        assertValidQuery("SELECT c FROM City c WHERE c.type <> org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.RUSH_PIGSTY");

        // TODO check input-parameter and enum-handling of mapper
        // assertValidQuery("SELECT c FROM City c WHERE  org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.RUSH_PIGSTY <> ?1");
        // assertValidQuery("SELECT c FROM City c WHERE :one <> org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.RUSH_PIGSTY");
        // assertValidQuery("SELECT c FROM City c WHERE  org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.RUSH_PIGSTY <> :one");
        // assertValidQuery("SELECT c FROM City c WHERE ?1 <> org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.RUSH_PIGSTY");

        assertValidQuery("SELECT p FROM Person p WHERE p.sqlTime >= (SELECT p1.sqlTime FROM Person p1)");

    }

    @Test
    @ToBeInvestigated
    public void testSimpleTypeComparisonHandling1() {
        assertInvalidQuery("SELECT c FROM City c WHERE 'bla' = (SELECT p.integer FROM Person p)");
    }

    @Test
    @ToBeInvestigated
    public void testSimpleTypeComparisonHandling2() {
        assertInvalidQuery("SELECT c FROM City c WHERE 'bla' = ANY(SELECT p.integer FROM Person p)");
    }

    @Test
    @ToBeInvestigated
    public void testSimpleTypeComparisonHandling3() {
        assertInvalidQuery("SELECT p FROM Person p WHERE 'bla' = p.integer");
    }

    @Test
    @ToBeInvestigated
    public void testSimpleTypeComparisonHandling4() {
        assertInvalidQuery("SELECT p FROM Person p WHERE 'bla' = -(2 + 3)");
    }

    @Test
    @ToBeInvestigated
    public void testSimpleTypeComparisonHandling5() {
        assertInvalidQuery("SELECT c FROM City c WHERE (SELECT p.integer FROM Person p) = 'bla'");
    }

    @Test
    @ToBeInvestigated
    public void testSimpleTypeComparisonHandling6() {
        assertInvalidQuery("SELECT p FROM Person p WHERE p.integer = 'bla'");
    }

    @Test
    @ToBeInvestigated
    public void testSimpleTypeComparisonHandling7() {
        assertInvalidQuery("SELECT p FROM Person p WHERE  -(2 + 3) = 'bla'");
    }

    @Test
    @ToBeInvestigated
    public void testSimpleTypeComparisonHandling8() {
        assertValidQuery("SELECT p FROM Person p WHERE MIN(p.string) = 'bla'");
    }

    @Test
    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.OPEN_SQL })
    public void testSimpleComparisonHandlingExcludingOpenSQL() {
        assertValidQuery("SELECT p FROM Person p WHERE CURRENT_TIMESTAMP >= ALL(SELECT p1.sqlTimestamp FROM Person p1)");
        assertValidQuery("SELECT p FROM Person p WHERE CURRENT_DATE >= SOME(SELECT p1.sqlDate FROM Person p1)");
        assertValidQuery("SELECT p FROM Person p WHERE p.sqlTimestamp <= CURRENT_TIMESTAMP");
        assertValidQuery("SELECT p FROM Person p WHERE 'bla' = LOWER('BLA')");
        assertValidQuery("SELECT p FROM Person p WHERE CURRENT_TIMESTAMP >= p.sqlTimestamp");
        assertValidQuery("SELECT p FROM Person p WHERE CURRENT_TIMESTAMP >= ?1");
        assertValidQuery("SELECT p FROM Person p WHERE CURRENT_TIMESTAMP >= :one");
        assertValidQuery("SELECT p FROM Person p WHERE LOWER('BLA') = 'bla'");

    }

    @Test
    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.OPEN_SQL })
    @ToBeInvestigated
    public void testSimpleComparisonHandlingExcludingOpenSQL1() {
        assertValidQuery("SELECT p FROM Person p WHERE +p.integer + -5 * -(p.integer + 5) / -LENGTH(p.string) * -MAX(p._float) + +?1 = +p.integer + -5 * -(p.integer + 5) / -LOCATE(p.string, 'bla') * -AVG(p._float) + +?1");
    }

    @Test
    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.OPEN_SQL })
    @ToBeInvestigated
    public void testSimpleComparisonHandlingExcludingOpenSQL2() {
        assertValidQuery("SELECT p FROM Person p WHERE MAX(p.sqlDate) <= CURRENT_DATE");
    }

    @Test
    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.OPEN_SQL })
    @ToBeInvestigated
    public void testSimpleComparisonHandlingExcludingOpenSQL3() {
        assertInvalidQuery("SELECT p FROM Person p WHERE 'bla' = CURRENT_DATE");
    }

    @Test
    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.OPEN_SQL })
    @ToBeInvestigated
    public void testSimpleComparisonHandlingExcludingOpenSQL4() {
        assertValidQuery("SELECT p FROM Person p WHERE CURRENT_TIMESTAMP >= MAX(p.sqlTimestamp)");
    }

    @Test
    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.OPEN_SQL })
    @ToBeInvestigated
    public void testSimpleComparisonHandlingExcludingOpenSQL5() {
        assertInvalidQuery("SELECT p FROM Person p WHERE CURRENT_DATE = 'bla'");
    }

    @Test
    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.OPEN_SQL,
    // DatabaseVendor.ORACLE })
    public void testSimpleComparisonHandlingExcludingOpenSQLAndOracle0() {
        assertValidQuery("SELECT p FROM Person p WHERE ?1 <= CURRENT_TIME");
    }

    @Test
    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.OPEN_SQL,
    // DatabaseVendor.ORACLE })
    public void testSimpleComparisonHandlingExcludingOpenSQLAndOracle1() {
        assertValidQuery("SELECT p FROM Person p WHERE :one <= CURRENT_TIME");
    }

    @Test
    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.OPEN_SQL,
    // DatabaseVendor.ORACLE })
    @ToBeInvestigated
    public void testSimpleComparisonHandlingExcludingOpenSQLAndOracle2() {
        assertInvalidQuery("SELECT p FROM Person p WHERE CURRENT_TIME = false");
    }

    @Test
    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.OPEN_SQL,
    // DatabaseVendor.ORACLE })
    @ToBeInvestigated
    public void testSimpleComparisonHandlingExcludingOpenSQLAndOracle3() {
        assertInvalidQuery("SELECT p FROM Person p WHERE true = CURRENT_TIME");
    }

    @Test
    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.OPEN_SQL,
    // DatabaseVendor.ORACLE })
    public void testSimpleComparisonHandlingExcludingOpenSQLAndOracle4() {
        assertValidQuery("SELECT p FROM Person p WHERE (SELECT p1.sqlTime FROM Person p1) <= CURRENT_TIME");
    }

    @Test
    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.OPEN_SQL,
    // DatabaseVendor.ORACLE })
    public void testSimpleComparisonHandlingExcludingOpenSQLAndOracle5() {
        assertValidQuery("SELECT p FROM Person p WHERE CURRENT_TIME >= ANY(SELECT p1.sqlTime FROM Person p1)");
    }

    @Test
    public void testBeanComparisonHandling() {
        assertValidQuery("SELECT p FROM Person p WHERE p.city = ALL(Select c FROM City c)");
        assertValidQuery("SELECT p FROM Person p WHERE p.city = (Select c FROM City c)");
        assertValidQuery("SELECT p FROM Person p, City c WHERE p.city = c");
        assertValidQuery("SELECT p FROM Person p WHERE p.city = ?1");
        assertValidQuery("SELECT p FROM Person p WHERE ?1 = p.city");
        assertValidQuery("SELECT p FROM Person p WHERE p.city = :one");
        assertValidQuery("SELECT p FROM Person p WHERE :one = p.city");
        assertValidQuery("SELECT p FROM Person p, City c WHERE c = p.city");
        assertValidQuery("SELECT p FROM Person p WHERE (Select c FROM City c) = p.city");
        assertValidQuery("SELECT p FROM Person p, City c WHERE p.city = (c) and true = true or (false = true and 5 = 6)");

    }

    @Test
    @ToBeInvestigated
    public void testBetweenHandling0() {
        assertValidQuery("SELECT p FROM Person p WHERE p.id BETWEEN (SELECT p1.integer FROM Person p1) AND AVG(p._float)");
    }

    @Test
    public void testBetweenHandling1() {
        assertValidQuery("SELECT p FROM Person p WHERE 5 BETWEEN 'bla' AND 'bla'");
    }

    @Test
    public void testBetweenHandling2() {
        assertInvalidQuery("SELECT p FROM Person p WHERE p BETWEEN 'bla' AND 'bla'");
    }

    @Test
    public void testBetweenHandling3() {
        assertInvalidQuery("SELECT p FROM Person p WHERE p.city.type BETWEEN 'bla' AND 'bla'");
    }

    @Test
    public void testBetweenHandling4() {
        assertValidQuery("SELECT p FROM Person p WHERE org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.RUSH_PIGSTY BETWEEN 'bla' AND 'bla'");
    }

    @Test
    @ToBeInvestigated
    public void testBetweenHandling5() {
        assertValidQuery("SELECT p FROM Person p WHERE 5 BETWEEN 2 AND MIN(p.integer)");
    }

    @Test
    @ToBeInvestigated
    public void testBetweenHandling6() {
        assertValidQuery("SELECT p FROM Person p WHERE MAX(p.string) BETWEEN ?1 AND (SELECT p1.string FROM Person p1)");
    }

    @Test
    @ToBeInvestigated
    public void testBetweenHandling7() {
        assertValidQuery("SELECT p FROM Person p WHERE p.id NOT BETWEEN (SELECT p1.integer FROM Person p1) AND AVG(p._float)");
    }

    @Test
    public void testBetweenHandling8() {
        assertValidQuery("SELECT p FROM Person p WHERE 5 NOT BETWEEN 'bla' AND 'bla'");
    }

    @Test
    public void testBetweenHandling9() {
        assertInvalidQuery("SELECT p FROM Person p WHERE p NOT BETWEEN 'bla' AND 'bla'");
    }

    @Test
    public void testBetweenHandling10() {
        assertInvalidQuery("SELECT p FROM Person p WHERE p.city.type NOT BETWEEN 'bla' AND 'bla'");
    }

    @Test
    public void testBetweenHandling11() {
        assertValidQuery("SELECT p FROM Person p WHERE org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.RUSH_PIGSTY NOT BETWEEN 'bla' AND 'bla'");
    }

    @Test
    @ToBeInvestigated
    public void testBetweenHandling12() {
        assertValidQuery("SELECT p FROM Person p WHERE 5 NOT BETWEEN 2 AND MIN(p.integer)");
    }

    @Test
    @ToBeInvestigated
    public void testBetweenHandling13() {
        assertValidQuery("SELECT p FROM Person p WHERE MAX(p.string) NOT BETWEEN ?1 AND (SELECT p1.string FROM Person p1)");
    }

    @Test
    public void testPlatformDependendBetweenHandling0() {
        assertValidQuery("SELECT p FROM Person p WHERE 2 + 3 BETWEEN LENGTH('bla') AND ?1");
    }

    @Test
    @ToBeInvestigated
    public void testPlatformDependendBetweenHandling1() {
        assertValidQuery("SELECT p FROM Person p WHERE p.sqlTimestamp BETWEEN CURRENT_TIMESTAMP AND p.sqlTimestamp");
    }

    @Test
    @ToBeInvestigated
    public void testPlatformDependendBetweenHandling2() {
        assertValidQuery("SELECT p FROM Person p WHERE (SELECT p1.string FROM Person p1) BETWEEN MIN(p.string) AND LOWER('bla')");
    }

    @Test
    public void testPlatformDependendBetweenHandling3() {
        assertValidQuery("SELECT p FROM Person p WHERE p.sqlTimestamp NOT BETWEEN CURRENT_TIMESTAMP AND p.sqlTimestamp");
    }

    @Test
    @ToBeInvestigated
    public void testPlatformDependendBetweenHandling4() {
        assertValidQuery("SELECT p FROM Person p WHERE (SELECT p1.string FROM Person p1) NOT BETWEEN MIN(p.string) AND LOWER('bla')");
    }

    @Test
    @ToBeInvestigated
    public void testLikeHandling00() {
        assertInvalidQuery("SELECT p FROM Person p WHERE 5 LIKE 'bla' ESCAPE 'c'");
    }

    @Test
    @ToBeInvestigated
    public void testLikeHandling01() {
        assertInvalidQuery("SELECT p FROM Person p WHERE p.city.type LIKE 'bla' ESCAPE 'c'");
    }

    @Test
    public void testLikeHandling02() {
        assertValidQuery("SELECT p FROM Person p WHERE p.string LIKE 'bla' ESCAPE 'c'");
    }

    @Test
    public void testLikeHandling03() {
        assertValidQuery("SELECT p FROM Person p WHERE (SELECT p1.string FROM Person p1) LIKE 'bla' ESCAPE 'c'");
    }

    @Test
    @ToBeInvestigated
    public void testLikeHandling04() {
        assertValidQuery("SELECT p FROM Person p WHERE MAX(p.string) LIKE 'bla'");
    }

    @Test
    public void testLikeHandling05() {
        assertValidQuery("SELECT p FROM Person p WHERE 'bla' LIKE ?1");
    }

    @Test
    public void testLikeHandling06() {
        assertValidQuery("SELECT p FROM Person p WHERE 'bla' LIKE ?1");
    }

    @Test
    public void testLikeHandling07() {
        assertInvalidQuery("SELECT p FROM Person p WHERE 'bla' LIKE 'bla' ESCAPE 'bla'");
    }

    @Test
    public void testLikeHandling08() {
        assertValidQuery("SELECT p FROM Person p WHERE 'bla' LIKE 'bla' ESCAPE ?1");
    }

    @Test
    @ToBeInvestigated
    public void testLikeHandling09() {
        assertInvalidQuery("SELECT p FROM Person p WHERE 5 NOT LIKE 'bla' ESCAPE 'c'");
    }

    @Test
    @ToBeInvestigated
    public void testLikeHandling10() {
        assertInvalidQuery("SELECT p FROM Person p WHERE p.city.type NOT LIKE 'bla' ESCAPE 'c'");
    }

    @Test
    public void testLikeHandling11() {
        assertValidQuery("SELECT p FROM Person p WHERE p.string NOT LIKE 'bla' ESCAPE 'c'");
    }

    @Test
    public void testLikeHandling12() {
        assertValidQuery("SELECT p FROM Person p WHERE (SELECT p1.string FROM Person p1) NOT LIKE 'bla' ESCAPE 'c'");
    }

    @Test
    @ToBeInvestigated
    public void testLikeHandling13() {
        assertValidQuery("SELECT p FROM Person p WHERE MAX(p.string) NOT LIKE 'bla'");
    }

    @Test
    public void testLikeHandling14() {
        assertValidQuery("SELECT p FROM Person p WHERE 'bla' NOT LIKE ?1");
    }

    @Test
    public void testLikeHandling15() {
        assertInvalidQuery("SELECT p FROM Person p WHERE 'bla' NOT LIKE 'bla' ESCAPE 'bla'");
    }

    @Test
    public void testLikeHandling16() {
        assertValidQuery("SELECT p FROM Person p WHERE 'bla' NOT LIKE 'bla' ESCAPE ?1");
    }

    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.OPEN_SQL,
    // DatabaseVendor.SAPDB })
    public void testLikeHandlingExcludingOpenSQL() {
        assertInvalidQuery("SELECT p FROM Person p WHERE LOWER(p.string) LIKE UPPER(p.string)");
        assertValidQuery("SELECT p FROM Person p WHERE LOWER(p.string) LIKE '%foobar%'");
    }

    @Test
    public void testInHandling() {
        assertInvalidQuery("select p from Person as p where p.city in (2, 3, 4)");
        assertInvalidQuery("select p from Person as p where CURRENT_DATE in (2, 3, 4)");
        assertValidQuery("select p from Person as p where p._float in (2, 3, 4.4, :one)");
        assertInvalidQuery("select p from Person as p where p._float in (2, 'bla', 4.4)");
        assertInvalidQuery("select p from Person as p where p.city.type in (org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Metal.DEATH)");
        assertValidQuery("select p from Person as p where p.city.type in (select c.type from City as c)");
        assertValidQuery("select p from Person as p where p.string in ('2', '3', '4.4', :one)");
        assertValidQuery("select p from Person as p where p.string in ('2', '3', '4.4', :one)");
        assertInvalidQuery("select p from Person as p where p.city NOT in (2, 3, 4)");
        assertInvalidQuery("select p from Person as p where CURRENT_DATE Not in (2, 3, 4)");
        assertValidQuery("select p from Person as p where p._float nOt in (2, 3, 4.4, :one)");
        assertInvalidQuery("select p from Person as p where p._float noT in (2, 'bla', 4.4)");
        assertInvalidQuery("select p from Person as p where p.city.type not in (org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Metal.DEATH)");
        assertValidQueryExecution("select p from Person as p where p.city.type not in (select c.type from City as c)");
        assertValidQuery("select p from Person as p where p.string not in ('2', '3', '4.4', :one)");

    }
    
    @Test
    public void testInBinary() {
        byte[] byteArr1 = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
        byte[] byteArr2 = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
        assertValidQueryExecutionWithArgs("SELECT b FROM BasicTypesFieldAccess b WHERE b.primitiveByteArray2Binary IN(?1,?2)",
                new Object[] { byteArr1, byteArr2 });
    	
    }

    

    @Test
    @ToBeInvestigated
    public void testInHandling0() {
        assertInvalidQuery("select p from Person as p where 2 in (2, 3, 4)");
    }

    @Test
    public void testInHandling1() {
        assertValidQuery("select p from Person as p where p.city.type in (org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.RUSH_PIGSTY, org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.STUCK_TURTLE, org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.RUNNING_ELEPHAN, :one)");
    }

    @Test
    @ToBeInvestigated
    public void testInHandling2() {
        assertInvalidQuery("select p from Person as p where 2 not in (2, 3, 4)");
    }

    @Test
    public void testInHandling3() {
        assertValidQuery("select p from Person as p where p.city.type not in (org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.RUSH_PIGSTY, org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.STUCK_TURTLE, org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Scale.RUNNING_ELEPHAN, :one)");
    }

    @Test
    @ToBeInvestigated
    public void testInHandling4() {
        byte[] byteArr1 = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8 };
        byte[] byteArr2 = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8 };
        String queryString = "SELECT p FROM PrincipalEntity p WHERE p.taskInstance.taskInstanceId IN (?1,?2)";
        assertValidQueryExecutionWithArgs(queryString, new Object[] { byteArr1, byteArr2 });
    }

    @Test
    public void testIsNullHandling0() {
        assertValidQuery("select p from Person as p where p.city is null");
    }

    @Test
    public void testIsNullHandling1() {
        assertValidQuery("select p from Person as p where :one is null");
    }

    @Test
    public void testIsNullHandling2() {
        assertInvalidQuery("select p from Person as p where p is null");
    }

    @Test
    public void testIsNullHandling3() {
        assertInvalidQuery("select p from Person as p where p.city.cops is null");
    }

    @Test
    @ToBeInvestigated
    public void testIsNullHandling4() {
        assertInvalidQuery("select p from Person as p where 2 + 3 is null");
    }

    @Test
    public void testIsNullHandling5() {
        assertValidQuery("select p from Person as p where p.city is not null");
    }

    @Test
    public void testIsNullHandling6() {
        assertValidQuery("select p from Person as p where :one is not null");
    }

    @Test
    public void testIsNullHandling7() {
        assertInvalidQuery("select p from Person as p where p is not null");
    }

    @Test
    public void testIsNullHandling8() {
        assertInvalidQuery("select p from Person as p where p.city.cops is NOT null");
    }

    @Test
    @ToBeInvestigated
    public void testIsNullHandling9() {
        assertInvalidQuery("select p from Person as p where 2 + 3 is not null");
    }

    @Test
    public void testIsEmptyHandling0() {
        assertInvalidQuery("select p from Person p where p.city is empty");
    }

    @Test
    @ToBeInvestigated
    public void testIsEmptyHandling1() {
        assertInvalidQuery("select p from Person p where p is empty");
    }

    @Test
    public void testIsEmptyHandling2() {
        assertValidQuery("select c from Criminal c where c.attachedCop.partner.partner.partner.attachedCriminals is empty");
    }

    @Test
    @ToBeInvestigated
    public void testIsEmptyHandling3() {
        assertInvalidQuery("select p from Person p where (2 * 4) is empty");
    }

    @Test
    public void testIsEmptyHandling4() {
        assertInvalidQuery("select p from Person p where p.city.rivers is empty");
    }

    @Test
    public void testIsEmptyHandling5() {
        assertInvalidQuery("select p from Person p where p.city is not empty");
    }

    @Test
    @ToBeInvestigated
    public void testIsEmptyHandling6() {
        assertInvalidQuery("select p from Person p where p is NOT empty");
    }

    @Test
    public void testIsEmptyHandling7() {
        assertValidQuery("select c from Criminal c where c.attachedCop.partner.partner.partner.informers is not empty");
    }

    @Test
    @ToBeInvestigated
    public void testIsEmptyHandling8() {
        assertInvalidQuery("select p from Person p where (2 * 4) is not empty");
    }

    @Test
    public void testIsEmptyHandling9() {
        assertInvalidQuery("select p from Person p where p.city.rivers is not empty");
    }

    @Test
    public void testMemberOfHandling() {
        assertInvalidQuery("select cri from Criminal cri, City c where cri.attachedCop member c.criminals");
        assertInvalidQuery("select cri from Criminal cri, City c where cri member c.rivers");
        assertInvalidQuery("select cri from Criminal cri, City c where 5 member c.rivers");
        assertInvalidQuery("select p from City c, Person p where p.integer member c.criminals");
        assertValidQuery("select c from City c where :one member OF c.criminals");
        assertInvalidQuery("select c from City c where (select p from Person p) member OF c.criminals");
        assertInvalidQuery("select c from City c where avg(c.id) member OF c.criminals");
        assertValidQuery("select cri from Criminal cri, City c where cri.attachedCop not member of c.cops");
        assertInvalidQuery("select cri from Criminal cri, City c where cri.attachedCop not member c.criminals");
        assertValidQuery("select cri from Criminal cri, City c where cri not member c.criminals");
        assertInvalidQuery("select cri from Criminal cri, City c where cri not member c.rivers");
        assertInvalidQuery("select cri from Criminal cri, City c where 5 NOT member c.rivers");
        assertInvalidQuery("select p from City c, Person p where p.integer not member c.criminals");
        assertValidQuery("select c from City c where :one not member OF c.criminals");
        assertInvalidQuery("select c from City c where (select p from Person p) not member OF c.criminals");
        assertInvalidQuery("select c from City c where avg(c.id) not member OF c.criminals");
    }
    
    @Test
    public void testMemberOfHandling0() {
      assertValidQuery("select cri from Criminal cri, City c where cri.attachedCop member of c.cops");
    }
    
    @Test
    public void testMemberOfHandling4() {
        assertValidQuery("select cri from Criminal cri, City c where cri member c.criminals");
    }

    @Test
    @ToBeInvestigated
    public void testMemberOfHandling1() {
        assertInvalidQuery("select p from City c, Person p where p.city.criminals member OF c.criminals");
    }

    @Test
    public void testMemberOfHandling2() {
        assertInvalidQuery("select p from City c, Person p where p.city.criminals not member OF c.criminals");
    }

    @Test
    public void testExistsHandling() {
        assertValidQuery("select c from City c where exists (select p from Person p)");
        assertValidQuery("select c from City c where not exists (select p from Person p)");
    }

}
