/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     10/09/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 *     10/25/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 *     11/22/2012-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support (index metadata support)
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa21.advanced;

import javax.persistence.EntityManager;

import junit.framework.TestSuite;
import junit.framework.Test;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.converters.ConverterClass;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.converters.Converter;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.sessions.server.ServerSession;

import org.eclipse.persistence.testing.models.jpa21.advanced.Runner;
import org.eclipse.persistence.testing.models.jpa21.advanced.RunnerInfo;
import org.eclipse.persistence.testing.models.jpa21.advanced.RunnerStatus;
import org.eclipse.persistence.testing.models.jpa21.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa21.advanced.enums.Health;
import org.eclipse.persistence.testing.models.jpa21.advanced.enums.Level;
import org.eclipse.persistence.testing.models.jpa21.advanced.enums.RunningStatus;

public class ConverterTestSuite extends JUnitTestCase {
    public ConverterTestSuite() {}
    
    public ConverterTestSuite(String name) {
        super(name);
    }
    
    public void setUp () {
        super.setUp();
        clearCache();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("ConverterTestSuite");
        
        suite.addTest(new ConverterTestSuite("testAutoApplyConverter"));
        suite.addTest(new ConverterTestSuite("testAnnotationConverters"));
        
        suite.addTest(XMLConverterTestSuite.suite());
        
        return suite;
    }
    
    /**
     * Test that an attribute picks up an auto apply converter. 
     */
    public void testAutoApplyConverter() {
        ServerSession session = JUnitTestCase.getServerSession();
        
        ClassDescriptor employeeDescriptor = session.getDescriptor(Employee.class);
        DirectToFieldMapping salaryMapping = (DirectToFieldMapping) employeeDescriptor.getMappingForAttributeName("salary");
        
        assertNotNull("Salary mapping did not have the auto apply converter", salaryMapping.getConverter());
        
        DirectToFieldMapping previousSalaryMapping = (DirectToFieldMapping) employeeDescriptor.getMappingForAttributeName("previousSalary");
        
        assertNull("Salary mapping did not have the auto apply converter", previousSalaryMapping.getConverter());
    }  
    
    /**
     * Test that converters are set.
     */
    public void testAnnotationConverters() {
        EntityManager em = createEntityManager();
            
        try {
            beginTransaction(em);
            
            Runner runner = new Runner();
            runner.setAge(53);
            runner.setIsFemale();
            runner.setFirstName("Doris");
            runner.setLastName("Day");
            runner.addPersonalBest("10 KM", "47:34");
            runner.addPersonalBest("5", "26:41");
            RunnerInfo runnerInfo = new RunnerInfo();
            runnerInfo.setHealth(Health.H);
            runnerInfo.setLevel(Level.A);
            RunnerStatus runnerStatus = new RunnerStatus();
            runnerStatus.setRunningStatus(RunningStatus.D);
            runnerInfo.setStatus(runnerStatus);
            runner.setInfo(runnerInfo);
            
            em.persist(runner);
            commitTransaction(em);
                
            // Clear the cache
            em.clear();
            clearCache();
    
            Runner runnerRefreshed = em.find(Runner.class, runner.getId());
            assertTrue("The age conversion did not work.", runnerRefreshed.getAge() == 52);
            assertTrue("The embeddable health conversion did not work.", runnerRefreshed.getInfo().getHealth().equals(Health.HEALTHY));
            assertTrue("The embeddable level conversion did not work.", runnerRefreshed.getInfo().getLevel().equals(Level.AMATEUR));
            assertTrue("The nested embeddable running status conversion did not work.", runnerRefreshed.getInfo().getStatus().getRunningStatus().equals(RunningStatus.DOWN_TIME));
            assertTrue("The number of personal bests for this runner is incorrect.", runnerRefreshed.getPersonalBests().size() == 2);
            assertTrue("Distance (map key) conversion did not work.", runnerRefreshed.getPersonalBests().keySet().contains("10K"));
            assertTrue("Distance (map key) conversion did not work.", runnerRefreshed.getPersonalBests().keySet().contains("5K"));
            assertTrue("Time (map value) conversion did not work.", runnerRefreshed.getPersonalBests().values().contains("47:34.0"));
            assertTrue("Time (map value) conversion did not work.", runnerRefreshed.getPersonalBests().values().contains("26:41.0"));
            
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
                
            throw e;
        } finally {
            closeEntityManager(em);
        }
    }
}
