/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  


package org.eclipse.persistence.testing.tests.jpa.inheritance;

import java.util.List;

import javax.persistence.EntityManager;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inheritance.PerformanceTireInfo;
import org.eclipse.persistence.testing.models.jpa.inheritance.TireInfo;

public class ReportQueryMultipleReturnInheritanceTestSuite extends JUnitTestCase {
    protected boolean m_reset = false;    // reset gets called twice on error
    protected PerformanceTireInfo tireInfo;
        
    public ReportQueryMultipleReturnInheritanceTestSuite() {
    }
    
    public ReportQueryMultipleReturnInheritanceTestSuite(String name) {
        super(name);
    }
    
    public void setUp () {
        super.setUp();
        m_reset = true;
        this.tireInfo = new PerformanceTireInfo();
        tireInfo.setPressure(32);
        tireInfo.setSpeedRating(220);
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
        try{
            em.persist(tireInfo);
            em.getTransaction().commit();
        }catch (RuntimeException ex){
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
            throw ex;
        }
    }
    
    public void tearDown () {
        if (m_reset) {
            EntityManager em = createEntityManager();
            em.getTransaction().begin();
            try{
                TireInfo localTire = (TireInfo)em.find(TireInfo.class, tireInfo.getId());
                em.remove(localTire);
                em.getTransaction().commit();
            }catch (RuntimeException ex){
                if (em.getTransaction().isActive()){
                    em.getTransaction().rollback();
                }
                em.close();
                throw ex;
            }
            m_reset = false;
            super.tearDown();
        }
    }
    
    public void testInheritanceMultiTableException(){
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.returnWithoutReportQueryResult();
        reportQuery.setReferenceClass(TireInfo.class);
        reportQuery.addAttribute("tireinfo",reportQuery.getExpressionBuilder());
        List result = (List)getServerSession().executeQuery(reportQuery);
        Object resultItem = result.get(0);
        assertTrue("Failed to return Employees correctly, Not A PerformanceTireInfo", PerformanceTireInfo.class.isAssignableFrom(resultItem.getClass()));
        assertTrue("Did not populate all fields.  Missing 'pressure'", ((PerformanceTireInfo)resultItem).getPressure() != null);
        assertTrue("Did not populate all fields.  Missing 'speedrating'", ((PerformanceTireInfo)resultItem).getSpeedRating() != null);
    }
    
    
    public static Test suite() {
        return new TestSuite(ReportQueryMultipleReturnInheritanceTestSuite.class) {
        
            protected void setUp(){               
            }

            protected void tearDown() {
                clearCache();
            }
        };
    }
    

}
