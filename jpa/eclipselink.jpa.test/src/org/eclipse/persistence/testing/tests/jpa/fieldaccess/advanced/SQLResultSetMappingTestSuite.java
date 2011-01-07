/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  


package org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced;


import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.persistence.EntityManager;

import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.queries.ResultSetMappingQuery;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Project;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.SmallProject;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Buyer;
import org.eclipse.persistence.queries.ColumnResult;
import org.eclipse.persistence.queries.EntityResult;
import org.eclipse.persistence.queries.FieldResult;
import org.eclipse.persistence.queries.SQLResultSetMapping;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

public class SQLResultSetMappingTestSuite extends JUnitTestCase {
    protected boolean m_reset = false;    // reset gets called twice on error

        
    public SQLResultSetMappingTestSuite() {
    }
    
    public SQLResultSetMappingTestSuite(String name) {
        super(name);
    }
    
    public void setUp () {
        m_reset = true;
        super.setUp();
        clearCache("fieldaccess");

        // Force uppercase for Postgres.
        if (getServerSession("fieldaccess").getPlatform().isPostgreSQL()) {
            getServerSession("fieldaccess").getLogin().setShouldForceFieldNamesToUpperCase(true);
        }
    }

    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("SQLResultSetMappingTestSuite");
        
        suite.addTest(new SQLResultSetMappingTestSuite("testSetup"));
        suite.addTest(new SQLResultSetMappingTestSuite("testInheritanceNoDiscriminatorColumn"));
        suite.addTest(new SQLResultSetMappingTestSuite("testComplicateResultWithInheritance"));
        suite.addTest(new SQLResultSetMappingTestSuite("testRefresh"));
        suite.addTest(new SQLResultSetMappingTestSuite("testBindParameters"));
        suite.addTest(new SQLResultSetMappingTestSuite("testBindParametersWithPostitional"));
        suite.addTest(new SQLResultSetMappingTestSuite("testSimpleInheritance"));
        suite.addTest(new SQLResultSetMappingTestSuite("testPessimisticLocking"));
        suite.addTest(new SQLResultSetMappingTestSuite("testComplicateResults"));
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession("fieldaccess"));
        EmployeePopulator employeePopulator = new EmployeePopulator();
        employeePopulator.buildExamples();
        //Persist the examples in the database
        employeePopulator.persistExample(getServerSession("fieldaccess"));
        clearCache("fieldaccess");
    }

    public void testInheritanceNoDiscriminatorColumn() throws Exception {
        SQLResultSetMapping resultSetMapping = new SQLResultSetMapping("testInheritanceNoDiscriminatorColumn");
        EntityResult entityResult = new EntityResult(Buyer.class);
        resultSetMapping.addResult(entityResult);
        entityResult.setDiscriminatorColumn("DTYPE_DESCRIM");
        
        SQLCall call = new SQLCall("SELECT t0.BUYER_ID, t0.DTYPE AS DTYPE_DESCRIM, t0.BUYER_NAME, t0.DESCRIP, t0.VERSION, t1.PURCHASES FROM CMP3_FA_BUYER t0, CMP3_FA_PBUYER t1 WHERE t1.BUYER_ID = t0.BUYER_ID");
        ResultSetMappingQuery query = new ResultSetMappingQuery(call);
        query.setSQLResultSetMapping(resultSetMapping);
        query.setShouldRefreshIdentityMapResult(true);
        EntityManager em = createEntityManager("fieldaccess");
        beginTransaction(em);
        try{
            List results = (List)((JpaEntityManager)em.getDelegate()).getActiveSession().executeQuery(query);
            assertNotNull("No result returned", results);
            
            Buyer buyer = (Buyer)results.get(0);
            buyer.setDescription("To A new changed description");
            results = (List)((JpaEntityManager)em.getDelegate()).getActiveSession().executeQuery(query);
            assertNotNull("No result returned", results);
            assertFalse("Object was not refreshed", buyer.getDescription().equals("To A new changed description"));
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }
    }

    public void testComplicateResultWithInheritance() throws Exception {
        SQLResultSetMapping resultSetMapping = new SQLResultSetMapping("ComplicatedInheritance");
        resultSetMapping.addResult(new ColumnResult("BUDGET_SUM"));
        EntityResult entityResult = new EntityResult(Project.class);
        resultSetMapping.addResult(entityResult);
        entityResult = new EntityResult(SmallProject.class);
        entityResult.addFieldResult(new FieldResult("id", "SMALL_ID"));
        entityResult.addFieldResult(new FieldResult("name", "SMALL_NAME"));
        entityResult.addFieldResult(new FieldResult("description", "SMALL_DESCRIPTION"));
        entityResult.addFieldResult(new FieldResult("teamLeader", "SMALL_TEAMLEAD"));
        entityResult.addFieldResult(new FieldResult("version", "SMALL_VERSION"));
        entityResult.setDiscriminatorColumn("SMALL_DESCRIM");
        resultSetMapping.addResult(entityResult);
        
        SQLCall call = new SQLCall("SELECT (t1.BUDGET/t0.PROJ_ID) AS BUDGET_SUM, t0.PROJ_ID, t0.PROJ_TYPE, t0.PROJ_NAME, t0.DESCRIP, t0.LEADER_ID, t0.VERSION, t1.BUDGET, t2.PROJ_ID AS SMALL_ID, t2.PROJ_TYPE AS SMALL_DESCRIM, t2.PROJ_NAME AS SMALL_NAME, t2.DESCRIP AS SMALL_DESCRIPTION, t2.LEADER_ID AS SMALL_TEAMLEAD, t2.VERSION AS SMALL_VERSION FROM CMP3_FA_PROJECT t0, CMP3_FA_PROJECT t2, CMP3_FA_LPROJECT t1 WHERE t1.PROJ_ID = t0.PROJ_ID AND t2.PROJ_TYPE='S'");
        ResultSetMappingQuery query = new ResultSetMappingQuery(call);
        query.setSQLResultSetMapping(resultSetMapping);
        List results = (List)getServerSession("fieldaccess").executeQuery(query);
        assertNotNull("No result returned", results);
        assertTrue("Empty list returned", (results.size()!=0));
        
        for (Iterator iterator = results.iterator(); iterator.hasNext(); ){
            Object[] resultElement = (Object[])iterator.next();
            assertTrue("Failed to Return 3 items", (resultElement.length == 3));
            // Using Number as Different db/drivers  can return different types
            // e.g. Oracle with ijdbc14.jar returns BigDecimal where as
            // Derby with derbyclient.jar returns Double
            assertTrue("Failed to return column",(resultElement[0] instanceof Number) );
            assertTrue("Failed to return LargeProject", (resultElement[1] instanceof LargeProject) );
            assertTrue("Failed To Return SmallProject", (resultElement[2] instanceof SmallProject) );            
            assertFalse("Returned same data in both result elements",((SmallProject)resultElement[2]).getName().equals(((LargeProject)resultElement[1]).getName()));
        }
    }
    
    public void testRefresh() throws Exception {
        SQLResultSetMapping resultSetMapping = new SQLResultSetMapping("ComplicatedInheritance");
        EntityResult entityResult = new EntityResult(Project.class);
        resultSetMapping.addResult(entityResult);
        entityResult.setDiscriminatorColumn("SMALL_DESCRIM");
        
        SQLCall call = new SQLCall("SELECT t0.PROJ_ID, t0.PROJ_TYPE AS SMALL_DESCRIM, t0.PROJ_NAME, t0.DESCRIP, t0.LEADER_ID, t0.VERSION, t1.BUDGET FROM CMP3_FA_PROJECT t0, CMP3_FA_PROJECT t2, CMP3_FA_LPROJECT t1 WHERE t1.PROJ_ID = t0.PROJ_ID");
        ResultSetMappingQuery query = new ResultSetMappingQuery(call);
        query.setSQLResultSetMapping(resultSetMapping);
        query.setShouldRefreshIdentityMapResult(true);
        EntityManager em = createEntityManager("fieldaccess");
        beginTransaction(em);
        try{
            List results = (List)getServerSession("fieldaccess").executeQuery(query);
            assertNotNull("No result returned", results);
            Project project = (Project)results.get(0);
            project.setDescription("To A new changed description");
            results = (List)getServerSession("fieldaccess").executeQuery(query);
            assertNotNull("No result returned", results);
            assertTrue("Empty list returned", (results.size()!=0));
            assertFalse("Object was not refreshed", project.getDescription().equals("To A new changed description"));
        }finally{
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);

        }
    }

    public void testBindParameters() throws Exception {
        SQLResultSetMapping resultSetMapping = new SQLResultSetMapping("BindParameters");
        EntityResult entityResult = new EntityResult(Project.class);
        resultSetMapping.addResult(entityResult);
        entityResult.setDiscriminatorColumn("SMALL_DESCRIM");
        
        SQLCall call = new SQLCall("SELECT t0.PROJ_ID, t0.PROJ_TYPE AS SMALL_DESCRIM, t0.PROJ_NAME, t0.DESCRIP, t0.LEADER_ID, t0.VERSION, t1.BUDGET FROM CMP3_FA_PROJECT t0, CMP3_FA_LPROJECT t1 WHERE t1.PROJ_ID = t0.PROJ_ID AND t1.BUDGET > ? AND t1.BUDGET < 30000000");
        ResultSetMappingQuery query = new ResultSetMappingQuery(call);
        query.setSQLResultSetMapping(resultSetMapping);
        query.setShouldRefreshIdentityMapResult(false);
        query.setShouldBindAllParameters(true);
        query.addArgument("1");
        Vector params = new Vector();
        //4000 is a more reasonable budget given test data if results are expected
        params.add(new Integer(4000));
        List results = (List)getServerSession("fieldaccess").executeQuery(query, params);
        assertNotNull("No result returned", results);
        assertTrue("Empty list returned", (results.size()!=0));
    }

    public void testBindParametersWithPostitional() throws Exception {
        SQLResultSetMapping resultSetMapping = new SQLResultSetMapping("BindParameters");
        EntityResult entityResult = new EntityResult(Project.class);
        resultSetMapping.addResult(entityResult);
        entityResult.setDiscriminatorColumn("SMALL_DESCRIM");
        
        SQLCall call = new SQLCall("SELECT t0.PROJ_ID, t0.PROJ_TYPE AS SMALL_DESCRIM, t0.PROJ_NAME, t0.DESCRIP, t0.LEADER_ID, t0.VERSION, t1.BUDGET FROM CMP3_FA_PROJECT t0, CMP3_FA_LPROJECT t1 WHERE t1.PROJ_ID = t0.PROJ_ID AND t1.BUDGET > ? AND t1.BUDGET < 30000000");
        ResultSetMappingQuery query = new ResultSetMappingQuery(call);
        query.setSQLResultSetMapping(resultSetMapping);
        query.setShouldRefreshIdentityMapResult(false);
        query.setShouldBindAllParameters(true);
        query.addArgument("1");
        Vector params = new Vector();
        params.add(new Integer(4000));
        List results = (List)getServerSession("fieldaccess").executeQuery(query, params);
        assertNotNull("No result returned", results);
        assertTrue("Empty list returned", (results.size()!=0));
    }
    public void testSimpleInheritance() throws Exception {
        SQLResultSetMapping resultSetMapping = new SQLResultSetMapping("SimpleInheritance");
        EntityResult entityResult = new EntityResult(Project.class);
        entityResult.setDiscriminatorColumn("SMALL_DESCRIM");
        resultSetMapping.addResult(entityResult);

        //Use ANSI outer join sytax so that the query works on most of the databases.
        //SQLCall call = new SQLCall("SELECT t0.PROJ_ID, t0.PROJ_TYPE AS SMALL_DESCRIM,  t0.PROJ_NAME, t0.DESCRIP, t0.LEADER_ID, t0.VERSION, t1.BUDGET FROM CMP3_FA_PROJECT t0, CMP3_FA_LPROJECT t1 WHERE t1.PROJ_ID (+)= t0.PROJ_ID");
        SQLCall call = new SQLCall("SELECT t0.PROJ_ID, t0.PROJ_TYPE AS SMALL_DESCRIM,  t0.PROJ_NAME, t0.DESCRIP, t0.LEADER_ID, t0.VERSION, t1.BUDGET FROM CMP3_FA_PROJECT t0 left outer join CMP3_FA_LPROJECT t1 on t1.PROJ_ID = t0.PROJ_ID");
        ResultSetMappingQuery query = new ResultSetMappingQuery(call);
        query.setSQLResultSetMapping(resultSetMapping);
        List results = (List)getServerSession("fieldaccess").executeQuery(query);
        assertNotNull("No result returned", results);
        assertTrue("Empty list returned", (results.size()!=0));
        for (Iterator iterator = results.iterator(); iterator.hasNext(); ){
            Object project = iterator.next();
            assertTrue("Failed to return a project", (project instanceof Project) );
        }
    }

    public void testPessimisticLocking() throws Exception {
        if (!isSelectForUpateSupported("fieldaccess")) {
            return;
        }
        EntityManager em = createEntityManager("fieldaccess");
        SmallProject smallProject = (SmallProject)getServerSession("fieldaccess").readObject(SmallProject.class);
        SQLResultSetMapping resultSetMapping = new SQLResultSetMapping("PessimisticLocking");
        EntityResult entityResult = new EntityResult(SmallProject.class);
        resultSetMapping.addResult(entityResult);
        
        SQLCall call = new SQLCall("SELECT t0.PROJ_ID, t0.PROJ_TYPE, t0.PROJ_NAME, t0.DESCRIP, t0.LEADER_ID, t0.VERSION FROM CMP3_FA_PROJECT t0 WHERE t0.PROJ_ID = " + smallProject.getId() + " FOR UPDATE");
        ResultSetMappingQuery query = new ResultSetMappingQuery(call);
        query.setSQLResultSetMapping(resultSetMapping);
        query.setLockMode(query.LOCK);
        beginTransaction(em);
        try{
            List results = (List)((JpaEntityManager)em.getDelegate()).getActiveSession().executeQuery(query);
            assertNotNull("No result returned", results);
            assertTrue("Empty list returned", (results.size()!=0));
            smallProject = (SmallProject)(results.get(0));
            smallProject.setDescription("A relatively new Description");
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
                
        smallProject = em.find(SmallProject.class, smallProject.getId());
        closeEntityManager(em);
        assertTrue("Failed to update the new description", smallProject.getDescription().equals("A relatively new Description"));
       
    }
    
    /** tests that embeddable and dot notation for fieldresults work */
     public void testComplicateResults() throws Exception {
        SQLResultSetMapping resultSetMapping = new SQLResultSetMapping("ComplicatedInheritance");
        EntityResult entityResult;
        
        entityResult = new EntityResult(Employee.class);
        entityResult.addFieldResult(new FieldResult("period.startDate", "STARTDATE"));
        entityResult.addFieldResult(new FieldResult("address.id", "EMP_ADDR"));

        resultSetMapping.addResult(entityResult);
        SQLCall call = new SQLCall("SELECT t0.EMP_ID, t1.EMP_ID, t0.F_NAME, t0.L_NAME, t0.VERSION, t1.SALARY, t0.START_DATE AS STARTDATE, t0.END_DATE, t0.ADDR_ID AS EMP_ADDR, t0.manager_EMP_ID FROM CMP3_FA_EMPLOYEE t0, CMP3_FA_SALARY t1 WHERE ((t1.EMP_ID = t0.EMP_ID) AND ( t0.L_NAME = 'Smith' ))");
        ResultSetMappingQuery query = new ResultSetMappingQuery(call);
        query.setSQLResultSetMapping(resultSetMapping);
        List results = (List)getServerSession("fieldaccess").executeQuery(query);
        assertNotNull("No result returned", results);
        assertTrue("Incorrect number of results returned, expected 2 got "+results.size(), (results.size()==2));
        
        for (Iterator iterator = results.iterator(); iterator.hasNext(); ){
            Object resultElement = iterator.next();
            assertTrue("Failed to return Employee", (resultElement instanceof Employee) );
            Employee emp = (Employee)resultElement;
            assertNotNull("Failed to get an address for Employee "+emp.getFirstName(), emp.getAddress() );
        }
    }


    public void tearDown () {
        if (m_reset) {
            m_reset = false;
        }
        super.tearDown();
    }
}
