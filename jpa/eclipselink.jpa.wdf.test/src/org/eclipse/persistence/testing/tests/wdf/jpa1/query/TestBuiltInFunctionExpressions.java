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

public class TestBuiltInFunctionExpressions extends QueryTest {

    @Test
    public void testConcatHandling0() {
        /* 0 */assertValidQuery("SELECT c FROM City c where 'a' = concat('a', 'a')");
    }

    @Test
    public void testConcatHandling1() {
        /* 1 */assertValidQuery("SELECT c FROM City c where 'a' = concat(concat('a', 'a'), concat('a', 'a'))");
    }

    @Test
    public void testConcatHandling2() {
        /* 2 */assertValidQuery("SELECT p FROM Person p where 'a' = concat(p.string, p.string)");
    }

    @Test
    public void testConcatHandling3() {
        /* 3 */assertValidQuery("SELECT p FROM Person p where 'a' = concat(5, 5)");
    }

    @Test
    public void testConcatHandling4() {
        /* 4 */assertValidQuery("SELECT p FROM Person p where 'a' = concat(?1, ?1)");
    }

    @ToBeInvestigated
    @Test
    public void testConcatHandling5() {
        /* 5 */assertValidQuery("SELECT p FROM Person p where 'a' = concat(max(p.string), min(p.string))");
    }

    @Test
    public void testConcatHandling6() {
        /* 6 */assertInvalidQuery("SELECT p FROM Person p where 'a' = concat((select max(p1.string) from Person p1), (select max(p1.string) from Person p1))");
    }

    @Test
    public void testConcatHandling7() {
        /* 7 */assertInvalidQuery("SELECT p FROM Person p where 'a' = concat(p, 4 * 5)");
    }

    @Test
    public void testSubstringHandling() {
        /* -- */assertValidQuery("UPDATE Person p SET p.string = CONCAT('288', SUBSTRING(p.string, LOCATE(p.string, '-'), 4))");
    }

    @Test
    public void testSubstringHandling8() {
        /* 8 */assertValidQuery("SELECT p FROM Person p where 'a' = substring('a', 4, 5)");
    }

    @Test
    public void testSubstringHandling9() {
        /* 9 */assertValidQuery("SELECT p FROM Person p where 'a' = substring(p.string, p.integer, p.integer)");
    }

    @Test
    public void testSubstringHandling10() {
        /* 10 */assertValidQuery("SELECT p FROM Person p where 'a' = substring(p.string, 2 + 3, 3 * 2)");
    }

    @Test
    public void testSubstringHandling11() {
        /* 11 */assertValidQuery("SELECT p FROM Person p where 'a' = substring(?1, ?2, ?2)");
    }

    @Test
    public void testSubstringHandling12() {
        /* 12 */assertValidQuery("SELECT p FROM Person p where 'a' = substring(substring('a', 2, 3), abs(2), abs(-2))");
    }

    @ToBeInvestigated
    @Test
    public void testSubstringHandling13() {
        /* 13 */assertValidQuery("SELECT p FROM Person p where 'a' = substring(max(p.string), min(p.integer), max(p.integer))");
    }

    @Test
    public void testSubstringHandling14() {
        /* 14 */assertInvalidQuery("SELECT p FROM Person p where 'a' = substring((select max(p1.string) from Person p1), (select max(p1.integer) from Person p1), (select max(p1.integer) from Person p1))");
    }

    @Test
    public void testTrimHandling15() {
        /* 15 */assertValidQuery("SELECT p FROM Person p where 'a' = trim('a')");
    }

    @Test
    public void testTrimHandling16() {
        /* 16 */assertInvalidQuery("SELECT p FROM Person p where 'a' = trim(p)");
    }

    @Test
    public void testTrimHandling17() {
        /* 17 */assertValidQuery("SELECT p FROM Person p where 'a' = trim(p.string)");
    }

    @Test
    public void testTrimHandling18() {
        /* 18 */assertValidQuery("SELECT p FROM Person p where 'a' = trim(:one)");
    }

    @Test
    public void testTrimHandling19() {
        /* 19 */assertValidQuery("SELECT p FROM Person p where 'a' = trim(trim('a'))");
    }

    @ToBeInvestigated
    @Test
    public void testTrimHandling20() {
        /* 20 */assertValidQuery("SELECT p FROM Person p where 'a' = trim(max(p.string))");
    }

    @Test
    public void testTrimHandling21() {
        /* 21 */assertInvalidQuery("SELECT p FROM Person p where 'a' = trim((select max(p1.string) from Person p1))");
    }

    @Test
    public void testTrimHandling22() {
        /* 22 */assertInvalidQuery("SELECT p FROM Person p where 'a' = trim(1+1)");
    }

    @Test
    public void testTrimHandling28() {
        /* 28 */assertValidQuery("SELECT p FROM Person p where trim(trailing from 'a') = 'a'");
    }

    @Test
    public void testTrimHandling29() {
        /* 29 */assertValidQuery("SELECT p FROM Person p where trim(leading from 'a') = 'a'");
    }

    @Test
    public void testTrimHandling30() {
        /* 30 */assertInvalidQuery("SELECT p FROM Person p where 'a' = trim(1 from 'a')");
    }

    @ToBeInvestigated
    @Test
    public void testTrimHandling31() {
        /* 31 */assertInvalidQuery("SELECT p FROM Person p where 'a' = trim('12' from 'a')");
    }

    @Test
    public void testTrimWithTrimCharacter23() {
        /* 23 */assertValidQuery("SELECT p FROM Person p where 'a' = trim('a' from 'a')");
    }

    @Test
    public void testTrimWithTrimCharacter24() {
        /* 24 */assertValidQuery("SELECT p FROM Person p where trim(both 'a' from 'a') = 'a'");
    }

    @Test
    public void testTrimWithTrimCharacter25() {
        /* 25 */assertValidQuery("SELECT p FROM Person p where trim(trailing 'a' from 'a') = 'a'");
    }

    @Test
    public void testTrimWithTrimCharacter26() {
        /* 26 */assertValidQuery("SELECT p FROM Person p where trim(leading 'a' from 'a') = 'a'");
    }

    @Test
    public void testTrimWithTrimCharacter27() {
        /* 27 */assertValidQuery("SELECT p FROM Person p where trim(both from 'a') = 'a'");
    }

    @Test
    public void testTrimWithTrimCharacter33() {
        /* 33 */assertValidQuery("SELECT p FROM Person p where 'a' = trim(:one from 'a')");
        /* 34 */assertValidQuery("SELECT p FROM Person p where 'a' = trim(:one from 'a')");
    }

    @Test
    public void testTrimWithTrimCharacter34() {
        /* 34 */assertValidQuery("SELECT p FROM Person p where 'a' = trim(:one from 'a')");
    }

    @Test
    public void testUpperHandling35() {
        /* 35 */assertValidQuery("SELECT p FROM Person p where upper('a') = lower('a')");
    }

    @Test
    public void testUpperHandling36() {
        /* 36 */assertInvalidQuery("SELECT p FROM Person p where upper(p) = lower(p)");
    }

    @Test
    public void testUpperHandling37() {
        /* 37 */assertValidQuery("SELECT p FROM Person p where upper(p.string) = lower(p.string)");
    }

    @Test
    public void testUpperHandling38() {
        /* 38 */assertValidQuery("SELECT p FROM Person p where upper(:one) = lower(:one)");
    }

    @Test
    public void testUpperHandling39() {
        /* 39 */assertValidQuery("SELECT p FROM Person p where upper(trim('a')) = lower(trim('a'))");
    }

    @ToBeInvestigated
    @Test
    public void testUpperHandling40() {
        /* 40 */assertValidQuery("SELECT p FROM Person p where upper(min(p.string)) = lower(max(p.string))");
    }

    @Test
    public void testUpperHandling41() {
        /* 41 */assertInvalidQuery("SELECT p FROM Person p where upper((select max(p1.string) from Person p1)) = lower((select max(p1.string) from Person p1))");
    }

    @Test
    public void testUpperHandling42() {
        /* 42 */assertValidQuery("SELECT p FROM Person p where upper(2*2) = lower(1+1)");
    }

    @Test
    public void testLengthHandling43() {
        /* 43 */assertValidQuery("SELECT p FROM Person p where length('a') = 666");
    }

    @Test
    public void testLengthHandling44() {
        /* 44 */assertInvalidQuery("SELECT p FROM Person p where length(p) = 666");
    }

    @Test
    public void testLengthHandling45() {
        /* 45 */assertValidQuery("SELECT p FROM Person p where length(p.string) = 666");
    }

    @Test
    public void testLengthHandling46() {
        /* 46 */assertValidQuery("SELECT p FROM Person p where length(:one) = 666");
    }

    @Test
    public void testLengthHandling47() {
        /* 47 */assertValidQuery("SELECT p FROM Person p where length(trim('a')) = 666");
    }

    @ToBeInvestigated
    @Test
    public void testLengthHandling48() {
        /* 48 */assertValidQuery("SELECT p FROM Person p where length(min(p.string)) = 666");
    }

    @Test
    public void testLengthHandling49() {
        /* 49 */assertInvalidQuery("SELECT p FROM Person p where length((select max(p1.string) from Person p1)) = 666");
    }

    @Test
    public void testLengthHandling50() {
        /* 50 */assertValidQuery("SELECT p FROM Person p where length(2*2) = 666");
    }

    @Test
    public void testLocateHandling51() {
        /* 51 */assertValidQuery("SELECT p FROM Person p where 666 = locate('a', 'b', 5)");
    }

    @Test
    public void testLocateHandling52() {
        /* 52 */assertValidQuery("SELECT p FROM Person p where 666 = locate(p.string, p.string, p.integer)");
    }

    @Test
    public void testLocateHandling53() {
        /* 53 */assertValidQuery("SELECT p FROM Person p where 666 = locate(p.string, p.string, 3 * 2)");
    }

    @Test
    public void testLocateHandling54() {
        /* 54 */assertValidQuery("SELECT p FROM Person p where 666 = locate(?1, ?1, ?2)");
    }

    @Test
    public void testLocateHandling55() {
        /* 55 */assertValidQuery("SELECT p FROM Person p where 666 = locate(substring('a', 2, 3), upper('abs(2)'), abs(-2))");
    }

    @ToBeInvestigated
    @Test
    public void testLocateHandling56() {
        /* 56 */assertValidQuery("SELECT p FROM Person p where 666 = locate(max(p.string), min(p.string), max(p.integer))");
    }

    @Test
    public void testLocateHandling57() {
        /* 57 */assertInvalidQuery("SELECT p FROM Person p where 666 = locate((select max(p1.string) from Person p1), (select max(p1.string) from Person p1), (select max(p1.integer) from Person p1))");
    }

    @Test
    public void testLocateHandling58() {
        /* 58 */assertValidQuery("SELECT p FROM Person p where 666 = locate('a', 'b')");
    }

    @Test
    public void testAbsHandling59() {
        /* 59 */assertValidQuery("SELECT p FROM Person p where 666 = abs(5+10.3)");
    }

    @Test
    public void testAbsHandling60() {
        /* 60 */assertValidQuery("SELECT p FROM Person p where 666 = abs(666)");
    }

    @Test
    public void testAbsHandling61() {
        /* 61 */assertValidQuery("SELECT p FROM Person p where 666 = abs(p._float)");
    }

    @Test
    public void testAbsHandling62() {
        /* 62 */assertValidQuery("SELECT p FROM Person p where 666 = abs(p.integer)");
    }

    @ToBeInvestigated
    @Test
    public void testAbsHandling63() {
        /* 63 */assertInvalidQuery("SELECT p FROM Person p where 666 = abs(p.string)");
    }

    @Test
    public void testAbsHandling64() {
        /* 64 */assertInvalidQuery("SELECT p FROM Person p where abs((select max(p1.bigInteger) from Person p1)) = 666");
    }

    @ToBeInvestigated
    @Test
    public void testAbsHandling65() {
        /* 65 */assertValidQuery("SELECT p FROM Person p where abs(max(p._float)) = 666");
    }

    @Test
    public void testAbsHandling66() {
        /* 66 */assertValidQuery("SELECT p FROM Person p where abs(abs(p._float)) = 666");
    }

    @Test
    public void testAbsHandling67() {
        /* 67 */assertValidQuery("SELECT p FROM Person p where 666 = abs(?1)");
    }

    @ToBeInvestigated
    @Test
    public void testAbsHandling68() {
        /* 68 */assertInvalidQuery("SELECT p FROM Person p where 666 = abs(p)");
    }

    @Test
    public void testSqrtHandling69() {
        /* 69 */assertValidQuery("SELECT p FROM Person p where 666 = sqrt(5+10.3)");
    }

    @Test
    public void testSqrtHandling70() {
        /* 70 */assertValidQuery("SELECT p FROM Person p where 666 = sqrt(666)");
    }

    @Test
    public void testSqrtHandling71() {
        /* 71 */assertValidQuery("SELECT p FROM Person p where 666 = sqrt(p._float)");
    }

    @Test
    public void testSqrtHandling72() {
        /* 72 */assertValidQuery("SELECT p FROM Person p where 666 = sqrt(p.integer)");
    }

    @ToBeInvestigated
    @Test
    public void testSqrtHandling73() {
        /* 73 */assertInvalidQuery("SELECT p FROM Person p where 666 = sqrt(p.string)");
    }

    @Test
    public void testSqrtHandling74() {
        /* 74 */assertInvalidQuery("SELECT p FROM Person p where sqrt((select max(p1.bigInteger) from Person p1)) = 666");
    }

    @ToBeInvestigated
    @Test
    public void testSqrtHandling75() {
        /* 75 */assertValidQuery("SELECT p FROM Person p where sqrt(max(p._float)) = 666");
    }

    @Test
    public void testSqrtHandling76() {
        /* 76 */assertValidQuery("SELECT p FROM Person p where sqrt(sqrt(p._float)) = 666");
    }

    @Test
    public void testSqrtHandling77() {
        /* 77 */assertValidQuery("SELECT p FROM Person p where 666 = sqrt(?1)");
    }

    @Test
    public void testSqrtHandling78() {
        /* 78 */assertValidQuery("SELECT p FROM Person p where 666 = sqrt(?1)");
    }

    @Test
    public void testModHandling79() {
        /* 79 */assertValidQuery("SELECT p FROM Person p where 666 = mod(5+10,5+10)");
    }

    @Test
    public void testModHandling80() {
        /* 80 */assertValidQuery("SELECT p FROM Person p where 666 = mod(666, 666)");
    }

    @Test
    public void testModHandling81() {
        /* 81 */assertInvalidQuery("SELECT p FROM Person p where 666 = mod(p._float, p._float)");
    }

    @Test
    public void testModHandling82() {
        /* 82 */assertValidQuery("SELECT p FROM Person p where 666 = mod(p.integer, p.integer)");
    }

    @Test
    public void testModHandling83() {
        /* 83 */assertInvalidQuery("SELECT p FROM Person p where 666 = mod(p.string, p.string)");
    }

    @Test
    public void testModHandling84() {
        /* 84 */assertInvalidQuery("SELECT p FROM Person p where mod((select max(p1.bigInteger) from Person p1), (select max(p1.bigInteger) from Person p1)) = 666");
    }

    @ToBeInvestigated
    @Test
    public void testModHandling85() {
        /* 85 */assertValidQuery("SELECT p FROM Person p where mod(max(p.integer), max(p.integer)) = 666");
    }

    @Test
    public void testModHandling86() {
        /* 86 */assertValidQuery("SELECT p FROM Person p where mod(abs(p.integer), abs(p.integer)) = 666");
    }

    @Test
    public void testModHandling87() {
        /* 87 */assertValidQuery("SELECT p FROM Person p where 666 = mod(?1, ?1)");
    }

    @Test
    public void testModHandling88() {
        /* 88 */assertInvalidQuery("SELECT p FROM Person p where 666 = mod(p, p)");
    }

    @Test
    public void testSizeHandling89() {
        /* 89 */assertInvalidQuery("SELECT c FROM Cop c where 666 = size(c.partner)");
    }

    @Test
    public void testSizeHandling90() {
        /* 90 */assertValidQuery("SELECT c FROM Cop c where 666 = size(c.attachedCriminals)");
    }

    @Test
    public void testSizeHandling91() {
        /* 91 */assertValidQuery("SELECT c FROM Cop c where 666 = size(c.informers)");
    }

    @Test
    public void testSizeHandling92() {
        /* 92 */assertInvalidQuery("SELECT c FROM Cop c where 666 = size(c.id)");
    }

    @Test
    public void testSizeHandling93() {
        /* 93 */assertInvalidQuery("SELECT c FROM Cop c where 666 = size(c.tesla)");
    }

    @Test
    public void testSizeHandling94() {
        /* 94 */assertInvalidQuery("SELECT c FROM Cop c where 666 = size(c.tesla.integer)");
    }

}
