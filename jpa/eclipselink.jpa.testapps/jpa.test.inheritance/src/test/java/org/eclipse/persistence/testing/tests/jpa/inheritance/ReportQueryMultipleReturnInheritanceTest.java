/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink


package org.eclipse.persistence.testing.tests.jpa.inheritance;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inheritance.InheritanceTableCreator;
import org.eclipse.persistence.testing.models.jpa.inheritance.PerformanceTireInfo;
import org.eclipse.persistence.testing.models.jpa.inheritance.TireInfo;

import java.util.List;

public class ReportQueryMultipleReturnInheritanceTest extends JUnitTestCase {

    private PerformanceTireInfo tireInfo;

    public ReportQueryMultipleReturnInheritanceTest() {
    }

    public ReportQueryMultipleReturnInheritanceTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ReportQueryMultipleReturnInheritanceTest.class);
        return suite;
    }

    @Override
    public void setUp () {
        super.setUp();
        new InheritanceTableCreator().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
        this.tireInfo = new PerformanceTireInfo();
        tireInfo.setPressure(32);
        tireInfo.setSpeedRating(220);
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            em.persist(tireInfo);
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
    }

    @Override
    public void tearDown () {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            TireInfo localTire = em.find(TireInfo.class, tireInfo.getId());
            em.remove(localTire);
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        super.tearDown();
    }

    public void testInheritanceMultiTableException(){
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.returnWithoutReportQueryResult();
        reportQuery.setReferenceClass(TireInfo.class);
        reportQuery.addAttribute("tireinfo",reportQuery.getExpressionBuilder());
        List<?> result = (List<?>)getServerSession().executeQuery(reportQuery);
        Object resultItem = result.get(0);
        assertTrue("Failed to return Employees correctly, Not A PerformanceTireInfo", PerformanceTireInfo.class.isAssignableFrom(resultItem.getClass()));
        assertNotNull("Did not populate all fields.  Missing 'pressure'", ((PerformanceTireInfo) resultItem).getPressure());
        assertNotNull("Did not populate all fields.  Missing 'speedrating'", ((PerformanceTireInfo) resultItem).getSpeedRating());
    }

}
