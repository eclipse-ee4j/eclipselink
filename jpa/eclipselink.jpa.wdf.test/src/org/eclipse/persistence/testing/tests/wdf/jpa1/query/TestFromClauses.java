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

public class TestFromClauses extends QueryTest {

    /*
     * JPA 1.0 specification, 4.4.2: An identification variable must not be a reserved identifier or have the same name as any
     * entity in the same persistence unit: Identification variables are case insensitive.
     */
    @Test
    public void testSimpleFrom() {
        /* 0 */assertValidQuery("SELECT c FROM City c");
        /* 1 */assertValidQuery("SELECT c FROM City as C");
        /* 2 */assertInvalidQuery("SELECT c FROM NoExist as c");
        /* 3 */assertValidQuery("SELECT C FROM City as c");
        /* 4 assertInvalidQuery("SELECT city FROM City city"); */
        /* 6 */assertInvalidQuery("SELECT c FROM City c, Person C");
        assertValidQuery("SELECT bfa.enumOrdinal FROM BasicTypesFieldAccess bfa WHERE bfa.enumOrdinal = org.eclipse.persistence.testing.models.wdf.jpa1.types.UserDefinedEnum.EMIL");
    }

    @Test
    @ToBeInvestigated
    public void testSimpleFrom5() {
        /* 5 */assertInvalidQuery("SELECT city FROM City City");
    }

    @Test
    public void testJoinFrom() {
        /* 7 */assertValidQuery("SELECT c FROM City c join c.cops as c1 where c1.id = 3");
        /* 8 */assertValidQuery("SELECT c FROM City c join c.cops c1 join c1.partner c2 where c2.id = 3");
        /* 11 */assertValidQuery("SELECT c FROM City c join fetch c.cops, Informer i inner join fetch i.informingCops");
        /* 12 */assertValidQuery("SELECT c FROM City c join c.cops c1 inner join c1.partner as c2 left join c2.partner c3 left outer join c3.informers c4 left outer join fetch c4.informingCops");
        /* 13 */assertValidQuery("select c from City as c join c.cops c1, in (c1.partner.attachedCriminals) crim where crim.attachedCop = c1");
        /* 14 */assertInvalidQuery("select c from City as c join c.cops c1, in (c1.partner) crim where crim.attachedCop = c1");
    }

    @Test
    @ToBeInvestigated
    public void testJoinFrom9() {
        /* 9 */assertInvalidQuery("SELECT c FROM Cop c join c.partner.attachedCriminals c1 where c1.id = 3");
    }

    @Test
    @ToBeInvestigated
    public void testJoinFrom10() {
        /* 10 */assertInvalidQuery("SELECT c FROM City c join c.type c1");
    }

    @Test
    @ToBeInvestigated
    public void testExistsSubquery15() {
        /* 15 */assertValidQuery("select c from City as c where exists (select c from City c)");
    }

    @Test
    @ToBeInvestigated
    public void testExistsSubquery17() {

        /* 17 */assertValidQuery("select c from City as c where exists (select c1 from City c1 where exists (select c1 from City c1))");
    }

    @Test
    @ToBeInvestigated
    public void testExistsSubquery18() {
        /* 18 */assertValidQuery("select c from City as c where exists (select c1 from City c1 where exists (select c from City c))");
    }
}
