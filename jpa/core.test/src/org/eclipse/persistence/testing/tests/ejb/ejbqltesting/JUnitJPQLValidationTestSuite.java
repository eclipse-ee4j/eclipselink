/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.ejb.ejbqltesting;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;
import javax.persistence.TransactionRequiredException;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import junit.extensions.TestSetup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.jpa.base.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;

import org.eclipse.persistence.jpa.config.EclipseLinkQueryHints;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.sessions.server.ServerSession;

import org.antlr.runtime.RecognitionException;

/**
 * <p>
 * <b>Purpose</b>: Test EJBQL exceptions.
 * <p>
 * <b>Description</b>: This class creates a test suite, initializes the database
 * and adds tests to the suite.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Run tests for expected EJBQL exceptions thrown
 * </ul>
 * @see org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator
 * @see JUnitDomainObjectComparer
 */
 
public class JUnitJPQLValidationTestSuite extends JUnitTestCase
{
    static JUnitDomainObjectComparer comparer;        //the global comparer object used in all tests
    
    public JUnitJPQLValidationTestSuite()
    {
        super();
    }
  
    public JUnitJPQLValidationTestSuite(String name)
    {
        super(name);
    }
  
    //This method is run at the end of EVERY test case method
    public void tearDown()
    {
        clearCache();
    }
    //This suite contains all tests contained in this class
    public static Test suite() 
    {
        TestSuite suite = new TestSuite();
        suite.setName("JUnitJPQLValidationTestSuite");
        suite.addTest(new JUnitJPQLValidationTestSuite("generalExceptionTest"));
        suite.addTest(new JUnitJPQLValidationTestSuite("recognitionExceptionTest"));
        suite.addTest(new JUnitJPQLValidationTestSuite("missingSelectExceptionTest"));
        suite.addTest(new JUnitJPQLValidationTestSuite("malformedJPQLExceptionTest1"));
        suite.addTest(new JUnitJPQLValidationTestSuite("malformedJPQLExceptionTest2"));
        suite.addTest(new JUnitJPQLValidationTestSuite("malformedJPQLExceptionTest3"));
        suite.addTest(new JUnitJPQLValidationTestSuite("malformedJPQLExceptionTest4"));
        suite.addTest(new JUnitJPQLValidationTestSuite("malformedJPQLExceptionTest5"));
        suite.addTest(new JUnitJPQLValidationTestSuite("malformedJPQLExceptionTest6"));
        suite.addTest(new JUnitJPQLValidationTestSuite("malformedJPQLExceptionTest7"));
        //gf1166
        suite.addTest(new JUnitJPQLValidationTestSuite("malformedJPQLExceptionTest8"));
        suite.addTest(new JUnitJPQLValidationTestSuite("noAliasWithWHEREAndParameterExceptionTest"));
        //suite.addTest(new JUnitJPQLValidationTestSuite("unknownAbstractSchemaTypeTest"));
        suite.addTest(new JUnitJPQLValidationTestSuite("multipleDeclarationOfIdentificationVariable"));
        suite.addTest(new JUnitJPQLValidationTestSuite("aliasResolutionException"));  
        suite.addTest(new JUnitJPQLValidationTestSuite("illegalArgumentExceptionTest"));
        suite.addTest(new JUnitJPQLValidationTestSuite("createNamedQueryThrowsIllegalArgumentExceptionTest"));
        suite.addTest(new JUnitJPQLValidationTestSuite("flushTxExceptionTest"));
        suite.addTest(new JUnitJPQLValidationTestSuite("noResultExceptionTest"));
        suite.addTest(new JUnitJPQLValidationTestSuite("testGetSingleResultOnUpdate"));
        suite.addTest(new JUnitJPQLValidationTestSuite("testGetSingleResultOnDelete"));
        suite.addTest(new JUnitJPQLValidationTestSuite("testExecuteUpdateOnSelect"));
        suite.addTest(new JUnitJPQLValidationTestSuite("flushOptimisticLockExceptionTest"));
        suite.addTest(new JUnitJPQLValidationTestSuite("commitOptimisticLockExceptionTest"));
        suite.addTest(new JUnitJPQLValidationTestSuite("JTAOptimisticLockExceptionTest"));
        suite.addTest(new JUnitJPQLValidationTestSuite("testParameterNameValidation"));
        suite.addTest(new JUnitJPQLValidationTestSuite("testModArgumentValidation"));
        suite.addTest(new JUnitJPQLValidationTestSuite("testInExpressionValidation"));
        suite.addTest(new JUnitJPQLValidationTestSuite("testOrderableTypeInOrderByItem"));
        suite.addTest(new JUnitJPQLValidationTestSuite("testInvalidNavigation"));
        suite.addTest(new JUnitJPQLValidationTestSuite("testInvalidCollectionNavigation"));
        suite.addTest(new JUnitJPQLValidationTestSuite("testUnknownAttribute"));
        suite.addTest(new JUnitJPQLValidationTestSuite("testUnknownEnumConstant"));
        suite.addTest(new JUnitJPQLValidationTestSuite("testCommitRollbackException"));                
        suite.addTest(new JUnitJPQLValidationTestSuite("testParameterPositionValidation"));
        suite.addTest(new JUnitJPQLValidationTestSuite("testParameterPositionValidation2"));
        suite.addTest(new JUnitJPQLValidationTestSuite("testParameterTypeValidation"));
        suite.addTest(new JUnitJPQLValidationTestSuite("testEjbqlCaseSensitivity"));
        suite.addTest(new JUnitJPQLValidationTestSuite("testEjbqlUnsupportJoinArgument"));
        suite.addTest(new JUnitJPQLValidationTestSuite("testInvalidSetClause"));
        suite.addTest(new JUnitJPQLValidationTestSuite("testUnsupportedCountDistinctOnOuterJoinedCompositePK"));
        suite.addTest(new JUnitJPQLValidationTestSuite("testInvalidHint"));
        return new TestSetup(suite) {
     
            //This method is run at the end of the SUITE only
            protected void tearDown() {
                clearCache();
            }
            
            //This method is run at the start of the SUITE only
            protected void setUp() {
                
                //get session to start setup
                DatabaseSession session = JUnitTestCase.getServerSession();
                
                //create a new EmployeePopulator
                EmployeePopulator employeePopulator = new EmployeePopulator();
                
                new AdvancedTableCreator().replaceTables(session);
                
                //initialize the global comparer object
                comparer = new JUnitDomainObjectComparer();
                
                //set the session for the comparer to use
                comparer.setSession((AbstractSession)session.getActiveSession());              
                
                //Populate the tables
                employeePopulator.buildExamples();
                
                //Persist the examples in the database
                employeePopulator.persistExample(session);     
            }            
        };    
  }
  
    public void illegalArgumentExceptionTest() 
    {
        
        String ejbqlString = "SELECT FROM EMPLOYEE emp";
        try 
        {
            List result = createEntityManager().createQuery(ejbqlString).getResultList();                
            fail("Illegal Argument Exception must be thrown");
        }        
        
        catch(IllegalArgumentException ex)
        {     
            Assert.assertTrue(ex.getCause() instanceof JPQLException);            
        }                   
    }
    
    
    public void generalExceptionTest()
    {
        
        String ejbqlString = "SELECT FROM EMPLOYEE emp";
        try 
        {
            List result = createEntityManager().createQuery(ejbqlString).getResultList();                
            fail("Syntax error exception must be thrown");
        }        
        
        catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(JPQLException.unexpectedToken, ((JPQLException) ex.getCause()).getErrorCode());
        }              
    }
    
    public void recognitionExceptionTest()
    {
        
        String ejbqlString =  "SELECT OBJECT(emp) FROW Employee emp";
        try 
        {
            List result = createEntityManager().createQuery(ejbqlString).getResultList();                
            fail("Recognition Exception must be thrown");
        }        
        
        catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(JPQLException.syntaxErrorAt, ((JPQLException) ex.getCause()).getErrorCode());
        }              
    }
    
   public void missingSelectExceptionTest()
   {
        
        String ejbqlString =  "OBJECT(emp) FROM Employee emp";
        
        try 
        {
            List result = createEntityManager().createQuery(ejbqlString).getResultList();                
            fail("Recognition Exception must be thrown");
        }        
        
        catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(JPQLException.unexpectedToken, ((JPQLException) ex.getCause()).getErrorCode());
        }    
   }
   
   
   public void malformedJPQLExceptionTest1()
   {
        
        String ejbqlString =  "SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName == \"F";
        
        try 
        {
            List result = createEntityManager().createQuery(ejbqlString).getResultList();                
            fail("Recognition Exception must be thrown");
        }        
        
        catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(JPQLException.unexpectedToken, ((JPQLException) ex.getCause()).getErrorCode());
        }    
   }
   
   public void malformedJPQLExceptionTest2()
   {
        
        String ejbqlString =  "SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName = \"Fred\" AND 1";
        
        try 
        {
            List result = createEntityManager().createQuery(ejbqlString).getResultList();                
            fail("Recognition Exception must be thrown");
        }        
        
        catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(JPQLException.unexpectedToken, ((JPQLException) ex.getCause()).getErrorCode());
        }    
   }
    
    public void malformedJPQLExceptionTest3()
    {
        
        String ejbqlString =  "SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName = \"Fred\" OR \"Freda\"";
        
        try 
        {
            List result = createEntityManager().createQuery(ejbqlString).getResultList();                
            fail("Recognition Exception must be thrown");    
        }        
        
        catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(JPQLException.unexpectedToken, ((JPQLException) ex.getCause()).getErrorCode());
        }         
    }
    
    public void malformedJPQLExceptionTest4()
    {
        
        String ejbqlString =  "SLEECT OBJECT(emp) FROM Employee emp WHERE emp.firstName = \"Fred\" OR \"Freda\"";
        
        try 
        {
            List result = createEntityManager().createQuery(ejbqlString).getResultList();                
            fail("Recognition Exception must be thrown");       
        }        
        
        catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(JPQLException.unexpectedToken, ((JPQLException) ex.getCause()).getErrorCode());
        }         
    
    }

    public void malformedJPQLExceptionTest5()
    {
        
        String ejbqlString =  "SELECT c FORM Customer c";
        
        try 
        {
            List result = createEntityManager().createQuery(ejbqlString).getResultList();                
            fail("Failed to throw expected IllegalArgumentException for a query using invalid keyword FORM");       
        }        
        
        catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(JPQLException.syntaxErrorAt, ((JPQLException) ex.getCause()).getErrorCode());
            assertTrue("Failed to throw expected IllegalArgumentException for a query having an unexpected keyword FORM.", ex.getCause().getMessage().contains("at [FORM]"));
        }         

        ejbqlString =  "SELECT COUNT(c FROM Customer c";
        
        try 
        {
            List result = createEntityManager().createQuery(ejbqlString).getResultList();                
            fail("Failed to throw expected IllegalArgumentException for a query using invalid keyword FROM");       
        }        
        
        catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(JPQLException.syntaxErrorAt, ((JPQLException) ex.getCause()).getErrorCode());
            assertTrue("Failed to throw expected IllegalArgumentException for a query having an unexpected keyword FROM.", ex.getCause().getMessage().contains("at [FROM]"));
        }         
    
        ejbqlString =  "SELECT c* FROM Customer c";
        
        try 
        {
            List result = createEntityManager().createQuery(ejbqlString).getResultList();                
            fail("Failed to throw expected IllegalArgumentException for a query using invalid keyword *");       
        }        
        
        catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(JPQLException.syntaxErrorAt, ((JPQLException) ex.getCause()).getErrorCode());
            assertTrue("Failed to throw expected IllegalArgumentException for a query having an unexpected keyword *.", ex.getCause().getMessage().contains("at [*]"));
        }         
    }
    
    public void malformedJPQLExceptionTest6()
    {
        
        String ejbqlString =  "SELECT c FROM Customer c WHERE c.name LIKE 1";
        
        try 
        {
            List result = createEntityManager().createQuery(ejbqlString).getResultList();                
            fail("Failed to throw expected IllegalArgumentException for a query using invalid keyword 1");       
        }        
        
        catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(JPQLException.unexpectedToken, ((JPQLException) ex.getCause()).getErrorCode());
            assertTrue("Failed to throw expected IllegalArgumentException for a query having an unexpected keyword 1.", ex.getCause().getMessage().contains("unexpected token [1]"));
        }         

        ejbqlString =  "SELECT c FROM Customer c WHERE c.name is not nall";
        
        try 
        {
            List result = createEntityManager().createQuery(ejbqlString).getResultList();                
            fail("Failed to throw expected IllegalArgumentException for a query using invalid keyword nall");       
        }        
        
        catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(JPQLException.unexpectedToken, ((JPQLException) ex.getCause()).getErrorCode());
            assertTrue("Failed to throw expected IllegalArgumentException for a query having an unexpected keyword nall.", ex.getCause().getMessage().contains("unexpected token [nall]"));
        }         

        ejbqlString =  "SELECT c FROM Customer c WHERE c.name is net null";
        
        try 
        {
            List result = createEntityManager().createQuery(ejbqlString).getResultList();                
            fail("Failed to throw expected IllegalArgumentException for a query using invalid keyword net");       
        }        
        
        catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(JPQLException.unexpectedToken, ((JPQLException) ex.getCause()).getErrorCode());
            assertTrue("Failed to throw expected IllegalArgumentException for a query having an unexpected keyword net.", ex.getCause().getMessage().contains("unexpected token [net]"));
        }         

        ejbqlString =  "SELECT c FROM Customer c WHERE c.name is EMPYT";
        
        try 
        {
            List result = createEntityManager().createQuery(ejbqlString).getResultList();                
            fail("Failed to throw expected IllegalArgumentException for a query using invalid keyword EMPYT");       
        }        
        
        catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(JPQLException.unexpectedToken, ((JPQLException) ex.getCause()).getErrorCode());
            assertTrue("Failed to throw expected IllegalArgumentException for a query having an unexpected keyword EMPYT.", ex.getCause().getMessage().contains("unexpected token [EMPYT]"));
        }         

        ejbqlString =  "SELECT c FROM Customer c WHERE c.name in 3.5";
        
        try 
        {
            List result = createEntityManager().createQuery(ejbqlString).getResultList();                
            fail("Failed to throw expected IllegalArgumentException for a query using invalid keyword 3.5");       
        }        
        
        catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(JPQLException.syntaxErrorAt, ((JPQLException) ex.getCause()).getErrorCode());
            assertTrue("Failed to throw expected IllegalArgumentException for a query having an unexpected keyword 3.5.", ex.getCause().getMessage().contains("at [3.5]"));
        }         

        ejbqlString =  "SELECT c FROM Customer c WHERE c.name MEMBER 6";
        
        try 
        {
            List result = createEntityManager().createQuery(ejbqlString).getResultList();                
            fail("Failed to throw expected IllegalArgumentException for a query using invalid keyword 6");       
        }        
        
        catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(JPQLException.unexpectedToken, ((JPQLException) ex.getCause()).getErrorCode());
            assertTrue("Failed to throw expected IllegalArgumentException for a query having an unexpected keyword 6.", ex.getCause().getMessage().contains("unexpected token [6]"));
        }         

        ejbqlString =  "SELECT c FROM Customer c WHERE c.name NOT BETEEN 6 and 7";
        
        try 
        {
            List result = createEntityManager().createQuery(ejbqlString).getResultList();                
            fail("Failed to throw expected IllegalArgumentException for a query using invalid keyword BETEEN");       
        }        
        
        catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(JPQLException.unexpectedToken, ((JPQLException) ex.getCause()).getErrorCode());
            assertTrue("Failed to throw expected IllegalArgumentException for a query having an unexpected keyword BETEEN.", ex.getCause().getMessage().contains("unexpected token [BETEEN]"));
        }         
    
    }

    public void malformedJPQLExceptionTest7()
    {
        
        String ejbqlString =  "SELECT e FROM";
        
        try 
        {
            List result = createEntityManager().createQuery(ejbqlString).getResultList();                
            fail("Expected unexpected end of query exception.");       
        }        
        
        catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(JPQLException.unexpectedEOF, ((JPQLException) ex.getCause()).getErrorCode());
        }         
    
    }

    //gf1166  Wrap ANTLRException inside JPQLException
    public void malformedJPQLExceptionTest8()
    {
        
        String ejbqlString =  "SELECT e FROM";
        
        try 
        {
            List result = createEntityManager().createQuery(ejbqlString).getResultList();                
            fail("Expected unexpected end of query exception.");       
        }        
        
        catch(IllegalArgumentException ex)
        {
            assertFalse("Failed to wrap the exception", ((JPQLException) ex.getCause()).getInternalException() == null);
            assertTrue("Failed to wrap the ANTLRException", ((JPQLException) ex.getCause()).getInternalException() instanceof RecognitionException);
        }         
    
    }

    public void noAliasWithWHEREAndParameterExceptionTest()
    {
        
        String ejbqlString =  "FROM Employee WHERE firstName = ?1";
        
        try 
        {
            List result = createEntityManager().createQuery(ejbqlString).getResultList();                
            fail("Recognition Exception must be thrown");
        }        
        
        catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(JPQLException.unexpectedToken, ((JPQLException) ex.getCause()).getErrorCode());
        }         
    }

    public void aliasResolutionException()
    {
        String ejbqlString = null;
        
        try {
            // invalid identification variable in WHERE clause
            ejbqlString = "SELECT employee FROM Employee employee WHERE emp.firstName = 'Fred'";
            createEntityManager().createQuery(ejbqlString).getResultList();
            fail("Failed to throw expected IllegalArgumentException for a query " +
                 "using an invalid identification variable in the WHERE clause.");
        } catch(IllegalArgumentException ex) {
            JPQLException ejbqlEx = (JPQLException)ex.getCause();
            Assert.assertEquals(
                "Caught JPQLException with unexpected error code " + 
                "(expected aliasResolutionException)",
                JPQLException.aliasResolutionException, ejbqlEx.getErrorCode());
        }

        try {
            // invalid identification variable in SELECT clause
            ejbqlString = "SELECT OBJECT(nullRoot) FROM Employee emp";
            createEntityManager().createQuery(ejbqlString).getResultList();
            fail("Failed to throw expected IllegalArgumentException for a query " +
                 "selecting an invalid identification variable.");
        } catch(IllegalArgumentException ex) {
            JPQLException ejbqlEx = (JPQLException)ex.getCause();
            Assert.assertEquals(
                "Caught JPQLException with unexpected error code " + 
                "(expected aliasResolutionException)",
                JPQLException.aliasResolutionException, ejbqlEx.getErrorCode());
        }

        try {
            // invalid identification variable in JOIN clause
            ejbqlString = "SELECT emp FROM Employee emp JOIN e.projects p WHERE p.name = 'Enterprise'";
            createEntityManager().createQuery(ejbqlString).getResultList();
            fail("Failed to throw expected IllegalArgumentException for a query " +
                 "using an invalid identification variable in a JOIN clause.");
        } catch(IllegalArgumentException ex) {
            JPQLException ejbqlEx = (JPQLException)ex.getCause();
            Assert.assertEquals(
                "Caught JPQLException with unexpected error code " + 
                "(expected aliasResolutionException)",
                JPQLException.aliasResolutionException, ejbqlEx.getErrorCode());
        }
    }

	public void unknownAbstractSchemaTypeTest()
    {
        String ejbqlString =  " SELECT OBJECT(i) FROM Integer i WHERE i.city = \"Ottawa\"";
        
        try 
        {
            List result = createEntityManager().createQuery(ejbqlString).getResultList();                
            fail("Missing exception for query using unknown abstract schema type");
        }        
        
        catch(IllegalArgumentException ex)
        {
            JPQLException ejbqlEx = (JPQLException)ex.getCause();
            Assert.assertEquals("Caught JPQLException with unexpected error code " + 
                                "(expected unknownAbstractSchemaType)", 
                                JPQLException.unknownAbstractSchemaType, ejbqlEx.getErrorCode());
        }   
    }

    public void multipleDeclarationOfIdentificationVariable()
    {
        String ejbqlString;
        List result;
        
        try 
        {
            ejbqlString = "SELECT o FROM Order o, Customer o";
            result = createEntityManager().createQuery(ejbqlString).getResultList();
            fail("Multiple declaration of identification variable must be thrown");
        }        
        catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(((JPQLException) ex.getCause()).getErrorCode(),JPQLException.multipleVariableDeclaration);
        }   

        try 
        {
            ejbqlString = "SELECT c FROM Customer c Join c.orders o WHERE NOT EXISTS (SELECT o FROM c.orders o)";
            result = createEntityManager().createQuery(ejbqlString).getResultList();
            fail("Multiple declaration of identification variable must be thrown");
        }        
        catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(((JPQLException) ex.getCause()).getErrorCode(),JPQLException.multipleVariableDeclaration);
        }   
    }

    public void testParameterNameValidation(){
        EntityManager em = this.createEntityManager();
        Query query = em.createQuery("Select e from Employee e where e.lastName like :name ");
        try{
            query.setParameter("l", "%ay");
        }catch (IllegalArgumentException ex){
            assertTrue("Failed to throw expected IllegalArgumentException, when incorrect parameter name is used", ex.getMessage().contains("using a name"));
            return;
        }
        fail("Failed to throw expected IllegalArgumentException, when incorrect parameter name is used");
    }
    
   
    public void testParameterPositionValidation(){
        EntityManager em = this.createEntityManager();
        Query query = em.createQuery("Select e from Employee e where e.firstName like ?1 ");
        try{
            query.setParameter(2, "%ay");
        }catch (IllegalArgumentException ex){
            assertTrue("Failed to throw expected IllegalArgumentException, when incorrect parameter name is used", ex.getMessage().contains("parameter at position"));
            return;
        }
        fail("Failed to throw expected IllegalArgumentException, when incorrect parameter position is used");
    }

    public void testParameterPositionValidation2() {

        EntityManager em = this.createEntityManager();
        Query query = em.createQuery("Select e from Employee e where e.firstName = ?1 AND e.lastName = ?3 ");
        try {
            query.setParameter(1, "foo");
            query.setParameter(2, "");
            query.setParameter(3, "bar");
        } catch (IllegalArgumentException ex) {
            assertTrue("Failed to throw expected IllegalArgumentException, when incorrect parameter name is used", ex.getMessage().contains("parameter at position"));
            return;
        }
        fail("Failed to throw expected IllegalArgumentException, when incorrect parameter position is used");
    }

    public void testParameterTypeValidation() {
        EntityManager em = this.createEntityManager();
        Query query = em.createQuery("Select e from Employee e where e.firstName = :fname AND e.lastName = :lname ");
        try {
            query.setParameter("fname", "foo");
            query.setParameter("lname", new Integer(1));
        } catch (IllegalArgumentException ex) {
            assertTrue("Failed to throw expected IllegalArgumentException, when parameter with incorrect type is used", ex.getMessage().contains("attempted to set a value of type"));
            return;
        }
        fail("Failed to throw expected IllegalArgumentException, when parameter with incorrect type is used");
    }

    public void testModArgumentValidation()
    {
        Assert.assertFalse("Warning SQL/Sybase doesnot support MOD function",  JUnitTestCase.getServerSession().getPlatform().isSQLServer() || JUnitTestCase.getServerSession().getPlatform().isSybase());

        String ejbqlString;
        List result;

        try
        {
            ejbqlString = "SELECT p FROM LargeProject p WHERE MOD(p.budget, 10) = 5";
            result = createEntityManager().createQuery(ejbqlString).getResultList();
            fail("wrong data type for MOD function must be thrown");
        }
        catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(((JPQLException) ex.getCause()).getErrorCode(),JPQLException.invalidFunctionArgument);
        }

        try
        {
            ejbqlString = "SELECT p FROM LargeProject p WHERE MOD(10, p.budget) = 5";
            result = createEntityManager().createQuery(ejbqlString).getResultList();
            fail("wrong data type for MOD function must be thrown");
        }
        catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(((JPQLException) ex.getCause()).getErrorCode(),JPQLException.invalidFunctionArgument);
        }
    }

    public void testInExpressionValidation()
    {
        String ejbqlString;
        List result;

        try {
            ejbqlString = "SELECT e FROM Employee e WHERE e.firstName IN (1, 2)";
            result = createEntityManager().createQuery(ejbqlString).getResultList();
            fail("wrong type for IN expression exception must be thrown");
        }
        catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(((JPQLException) ex.getCause()).getErrorCode(),JPQLException.invalidExpressionArgument);
        }
    }

    public void testOrderableTypeInOrderByItem() {
        EntityManager em = this.createEntityManager();
        try {
            Query query = em.createQuery("SELECT e FROM Employee e ORDER BY e.address");
            query.getResultList();
            fail("Failed to throw expected IllegalArgumentException for a query having an ORDER BY item with a non-orderable type");
        } catch (IllegalArgumentException ex) {
            JPQLException cause = (JPQLException)ex.getCause();
            Assert.assertEquals(cause.getErrorCode(), JPQLException.expectedOrderableOrderByItem);
        }
    }

    public void testInvalidNavigation() {
        EntityManager em = this.createEntityManager();
        try {
            em.createQuery("SELECT e.firstName.invalid FROM Employee e").getResultList();
            fail("Failed to throw expected IllegalArgumentException for a query " +
                 "navigating a state field of type String in the SELECT clause.");
        } catch (IllegalArgumentException ex) {
            JPQLException cause = (JPQLException)ex.getCause();
            Assert.assertEquals("Unexpected error code", 
                cause.getErrorCode(), JPQLException.invalidNavigation);
        }
        try {
            em.createQuery("SELECT e FROM Employee e WHERE e.firstName.invalid = 1").getResultList();
            fail("Failed to throw expected IllegalArgumentException for a query " +
                 "navigating a state field of type String in the WHERE clause.");
        } catch (IllegalArgumentException ex) {
            JPQLException cause = (JPQLException)ex.getCause();
            Assert.assertEquals("Unexpected error code", 
                cause.getErrorCode(), JPQLException.invalidNavigation);
        }
    }

    public void testInvalidCollectionNavigation() {
        EntityManager em = this.createEntityManager();
        try {
            String jpql = "SELECT e.phoneNumbers.type FROM Employee e";
            em.createQuery(jpql).getResultList();
            fail("Failed to throw expected IllegalArgumentException for a query " +
                 "navigating a collection valued association field in the SELECT clause.");
        } catch (IllegalArgumentException ex) {
            JPQLException cause = (JPQLException)ex.getCause();
            Assert.assertEquals("Unexpected error code", 
                cause.getErrorCode(), JPQLException.invalidCollectionNavigation);
        }
        try {
            String jpql = 
                "SELECT e FROM Employee e WHERE e.phoneNumbers.type = 'Work'";
            em.createQuery(jpql).getResultList();
            fail("Failed to throw expected IllegalArgumentException for a query " +
                 "navigating a collection valued association field in the WHERE clause.");
        } catch (IllegalArgumentException ex) {
            JPQLException cause = (JPQLException)ex.getCause();
            Assert.assertEquals("Unexpected error code", 
                cause.getErrorCode(), JPQLException.invalidCollectionNavigation);
        }
    }

    public void testInvalidHint() {
        EntityManager em = createEntityManager();
        try {
            String jpql = "SELECT e.phoneNumbers.type FROM Employee e";
            Query query = em.createQuery(jpql);
            query.setHint(EclipseLinkQueryHints.BATCH, "e.phoneNumbers");
            query.getResultList();
            fail("Failed to throw expected IllegalArgumentException for invalid query hint.");
        } catch (IllegalArgumentException ex) {
            // Expected.
        }
        try {
            String jpql = "SELECT e FROM Employee e";
            Query query = em.createQuery(jpql);
            query.setHint(EclipseLinkQueryHints.BATCH, "e.phoneNumbers.areaCode");
            query.getResultList();
            fail("Failed to throw expected IllegalArgumentException for invalid query hint.");
        } catch (QueryException ex) {
            // Expected.
        }
        try {
            String jpql = "SELECT e FROM Employee e";
            Query query = em.createQuery(jpql);
            query.setHint(EclipseLinkQueryHints.CACHE_USAGE, "foobar");
            query.getResultList();
            fail("Failed to throw expected IllegalArgumentException for invalid query hint.");
        } catch (IllegalArgumentException ex) {
            // Expected.
        }
        closeEntityManager(em);
    }
    
    public void testUnknownAttribute() {
        EntityManager em = this.createEntityManager();
        try {
            em.createQuery("SELECT e.unknown FROM Employee e").getResultList();
            fail("Failed to throw expected IllegalArgumentException for a query " +
                 "selecting an unknown state or association field.");
        } catch (IllegalArgumentException ex) {
            JPQLException cause = (JPQLException)ex.getCause();
            Assert.assertEquals("Unexpected error code: ", 
                cause.getErrorCode(), JPQLException.unknownAttribute);
        }
        try {
            em.createQuery("SELECT e FROM Employee e WHERE e.unknown = 1").getResultList();
            fail("Failed to throw expected IllegalArgumentException for a query " +
                 "using an unknown state or association field in the WHERE clause.");
        } catch (IllegalArgumentException ex) {
            JPQLException cause = (JPQLException)ex.getCause();
            Assert.assertEquals("Unexpected error code", 
                cause.getErrorCode(), JPQLException.unknownAttribute);
        }
    }

    public void testUnknownEnumConstant() {
        EntityManager em = this.createEntityManager();
        try {
            em.createQuery("SELECT e FROM Employee e WHERE e.status = EmployeeStatus.FULL_TIME");
            fail("Failed to throw expected IllegalArgumentException for a query"+
                "unknown enumerated class constant.");
        } catch (IllegalArgumentException ex) {
            JPQLException cause = (JPQLException)ex.getCause();
            Assert.assertEquals("Unexpected error code: ",
                cause.getErrorCode(), JPQLException.aliasResolutionException);
        }
    }




  /**
   * For this test you need to add a persistence unit named default1 in the persistence.xml file
   * in essentials_testmodels.jar.
   */
  public void flushOptimisticLockExceptionTest()
    {
        EntityManager firstEm = createEntityManager();        
        EntityManager secondEm = createAlternateEntityManager();
        
        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName='Bob' ";   
       
        secondEm.getTransaction().begin();
        try{
            firstEm.getTransaction().begin();        
            try{
                Employee firstEmployee = (Employee) firstEm.createQuery(ejbqlString).getSingleResult();                
                firstEmployee.setLastName("test");      
                     
                Employee secondEmployee = (Employee) secondEm.createQuery(ejbqlString).getSingleResult();       
                secondEmployee.setLastName("test");
                
                firstEm.flush();
                firstEm.getTransaction().commit();
            }catch (RuntimeException ex){
                if (firstEm.getTransaction().isActive()){
                    firstEm.getTransaction().rollback();
                }
                firstEm.close();
                throw ex;
            }
            secondEm.flush();
            fail("javax.persistence.OptimisticLockException must be thrown during flush");
        } catch (RuntimeException e) {            
            if (secondEm.getTransaction().isActive()){
                secondEm.getTransaction().rollback();
            }
            secondEm.close();
            undoEmployeeChanges();
            Assert.assertTrue(e instanceof javax.persistence.OptimisticLockException);            
        }        
    }

    
     /* For this test you need to add a persistence unit named default1 in the persistence.xml file 
       in essentials_testmodels.jar */   
  

  public void commitOptimisticLockExceptionTest()
    {
        EntityManager firstEm = (EntityManager) createEntityManager();        
        EntityManager secondEm = (EntityManager) createAlternateEntityManager();
        
        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName='Bob' ";   
       
        secondEm.getTransaction().begin();
        try{
            firstEm.getTransaction().begin();        
            try{
        
                Employee firstEmployee = (Employee) firstEm.createQuery(ejbqlString).getSingleResult();                
                firstEmployee.setLastName("test");      
                     
                Employee secondEmployee = (Employee) secondEm.createQuery(ejbqlString).getSingleResult();       
                secondEmployee.setLastName("test");
                
                firstEm.getTransaction().commit();
            }catch (RuntimeException ex){
                if (firstEm.getTransaction().isActive()){
                    firstEm.getTransaction().rollback();
                }
                firstEm.close();
                throw ex;
            }
            secondEm.getTransaction().commit();  
        } catch (Exception e){          
            if (secondEm.getTransaction().isActive()){
                secondEm.getTransaction().rollback();
            }
            secondEm.close();
            undoEmployeeChanges();
            Assert.assertTrue("Exception not instance of opt Lock exception", e.getCause() instanceof javax.persistence.OptimisticLockException);            
            return;
        }    
        fail("javax.persistence.OptimisticLockException must be thrown during commit");
    }
    
    //this test fakes a JTA transaction
    public void JTAOptimisticLockExceptionTest() 
    {
        EntityManager firstEm = (EntityManager) createEntityManager();        
        EntityManager secondEm = (EntityManager) createAlternateEntityManager();
        
        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName='Bob' ";   
       
        secondEm.getTransaction().begin();
        try{
            firstEm.getTransaction().begin();        
            try{
        
                Employee firstEmployee = (Employee) firstEm.createQuery(ejbqlString).getSingleResult();                
                firstEmployee.setLastName("test");      
                     
                Employee secondEmployee = (Employee) secondEm.createQuery(ejbqlString).getSingleResult();       
                secondEmployee.setLastName("test");
        
        
                firstEm.getTransaction().commit();
            }catch (RuntimeException ex){
                if (firstEm.getTransaction().isActive()){
                    firstEm.getTransaction().rollback();
                }
                firstEm.close();
                throw ex;
            }
        
            ((RepeatableWriteUnitOfWork)((EntityManagerImpl)secondEm).getActivePersistenceContext(null)).issueSQLbeforeCompletion();
            fail("javax.persistence.OptimisticLockException must be thrown during commit");
        } catch (Exception e){          
            if (secondEm.getTransaction().isActive()){
                secondEm.getTransaction().rollback();
            }
            secondEm.close();
            Assert.assertTrue(e instanceof javax.persistence.OptimisticLockException);            
        }finally {
            undoEmployeeChanges();
            if (secondEm.getTransaction().isActive()){
                secondEm.getTransaction().rollback();
            }
        }
    }
    
    public void flushTxExceptionTest()
    {
        try 
        {
            createEntityManager().flush();    
        }
        catch (TransactionRequiredException e)
        {            
            Assert.assertTrue(e instanceof TransactionRequiredException);
        }
    }
    
    public void createNamedQueryThrowsIllegalArgumentExceptionTest() 
    {
        try 
        {
            List result = createEntityManager().createNamedQuery("test").getResultList();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(e instanceof IllegalArgumentException);
        }       
    } 
    
    public void noResultExceptionTest()
    {
        String ejbqlString = "SELECT OBJECT (emp) FROM Employee emp WHERE emp.lastName=\"doestNotExist\" ";
        
        try 
        {
            Object result = createEntityManager().createQuery(ejbqlString).getSingleResult();
        }
        catch (Exception e)
        {               
            Assert.assertTrue(e instanceof NoResultException);
        }
    }   
    
    public void testGetSingleResultOnUpdate()
    {
        boolean testPass=false;
        String ejbqlString = "UPDATE Employee e SET e.salary = (e.salary + 1000) WHERE e.lastName='Chanley' ";
        
        try 
        {
            Object result = createEntityManager().createQuery(ejbqlString).getSingleResult(); 
        }
        catch (IllegalStateException e)
        {                   
            testPass = true;
        }        
        Assert.assertTrue(testPass);
    }
    
    
    public void testGetSingleResultOnDelete()
    {
        boolean testPass=false;
        String ejbqlString = "DELETE FROM Employee e WHERE e.lastName='Chanley' ";
        
        try 
        {
            Object result = createEntityManager().createQuery(ejbqlString).getSingleResult(); 
        }
        catch (IllegalStateException e)
        {            
            testPass = true;
        }
        Assert.assertTrue(testPass);
    }
    
    public void testExecuteUpdateOnSelect()
    {
        boolean testPass=false;
        String ejbqlString = "SELECT emp FROM Employee emp  WHERE emp.lastName='Smith' ";
        
        try 
        {
            int result = createEntityManager().createQuery(ejbqlString).executeUpdate();
        }
        catch (IllegalStateException e)
        {            
            testPass = true; 
        }
        Assert.assertTrue(testPass);
    }


    public void testCommitRollbackException() 
    {
        EntityManager em = createEntityManager();
        String ejbqlString = "SELECT OBJECT (emp) FROM Employee emp WHERE emp.firstName='Bob'";
        DirectToFieldMapping idMapping = null;
        String defaultFieldName = "";
        em.getTransaction().begin();
        try{
            Employee emp = (Employee) em.createQuery(ejbqlString).getSingleResult();        
            idMapping = (DirectToFieldMapping) ((ServerSession)((EntityManagerImpl)em.getDelegate()).getServerSession()).getClassDescriptor(Employee.class).getMappingForAttributeName("id");
            defaultFieldName = idMapping.getFieldName();
            idMapping.setFieldName("fake_id");
            emp.setId(323);
            em.getTransaction().commit();
        } catch (Exception e) {          
            if(em.getTransaction().isActive()){
                em.getTransaction().rollback();
                em.close();
            }
            Assert.assertTrue(e instanceof RollbackException);
        } finally {
            em = createEntityManager();
            em.getTransaction().begin();
            try{
                idMapping.setFieldName(defaultFieldName);
                em.getTransaction().commit();
            } catch (Exception e) {
                if(em.getTransaction().isActive()){
                    em.getTransaction().rollback();
                    em.close();
                }
            }
        }
    }
    
    //fix for bugID 4670705
    public void testEjbqlCaseSensitivity() 
    {
        boolean testPass = true;
        EntityManager em = createEntityManager();        
        String ejbqlString = "SELECT OBJECT (E) FROM Employee e";        
        
        try {
            List result = em.createQuery(ejbqlString).getResultList();
        } catch (Exception e) {     
            testPass = false; 
        }              
        Assert.assertTrue(testPass);        
    }
    
    //this test resets the last name of the employee Bob that is changed in some tests
    public void undoEmployeeChanges() 
    {
        EntityManager em = createEntityManager();       
           
        em.getTransaction().begin();
        try{
            String ejbqlString = "SELECT OBJECT (emp) FROM Employee emp WHERE emp.firstName ='Bob' ";
        
            Employee emp = (Employee) em.createQuery(ejbqlString).getSingleResult();
            emp.setLastName("Smith");
        
            em.flush();
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if(em.getTransaction().isActive()){
                em.getTransaction().rollback();
                em.close();
            }
        }
    }

    public void testEjbqlUnsupportJoinArgument() {
        String ejbqlString;
        List result;
        
        try 
        {
            ejbqlString = "SELECT e.firstName FROM Employee e JOIN e.period ep";
            result = createEntityManager().createQuery(ejbqlString).getResultList();
            fail ("JOINing of embedded entities is not allowed must be thrown");
        } catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(((JPQLException) ex.getCause()).getErrorCode(),JPQLException.unsupportJoinArgument);
        }   
        
        try 
        {
            ejbqlString = "SELECT e.firstName FROM Employee e JOIN FETCH e.period";
            result = createEntityManager().createQuery(ejbqlString).getResultList();
            fail ("JOINing of embedded entities is not allowed must be thrown");
        } catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(((JPQLException) ex.getCause()).getErrorCode(),JPQLException.unsupportJoinArgument);
        }   
    }

    public void testInvalidSetClause()
    {
        String ejbqlString;
        List result;
        
        try 
        {
            ejbqlString = "UPDATE Employee e SET e.projects = NULL";
            result = createEntityManager().createQuery(ejbqlString).getResultList();
            fail ("Failed to throw expected IllegalArgumentException for query " + 
                  " updating a collection valued relationship.");
        } catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(((JPQLException) ex.getCause()).getErrorCode(),
                                JPQLException.invalidSetClauseTarget);
        }   

        try 
        {
            ejbqlString = "UPDATE Employee e SET e.department.name = 'CHANGED'";
            result = createEntityManager().createQuery(ejbqlString).getResultList();
            fail ("Failed to throw expected IllegalArgumentException for query " + 
                  " updating a sate field of a related instance.");
        } catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(((JPQLException) ex.getCause()).getErrorCode(),
                                JPQLException.invalidSetClauseNavigation);
        }   
    }
        

    public void testUnsupportedCountDistinctOnOuterJoinedCompositePK()
    {
        try 
        {
            String jpql = "SELECT COUNT(DISTINCT p) FROM Employee e LEFT JOIN e.phoneNumbers p GROUP BY e.lastName";
            List result = createEntityManager().createQuery(jpql).getResultList();
            fail ("Failed to throw expected IllegalArgumentException for query " + 
                  " having a COUNT DISTINCT on a joined variable with a composite primary key.");
        } catch(IllegalArgumentException ex)
        {
            Assert.assertEquals(((QueryException) ex.getCause()).getErrorCode(),
                                QueryException.DISTINCT_COUNT_ON_OUTER_JOINED_COMPOSITE_PK);
        } catch(QueryException ex)
        {
            Assert.assertEquals(ex.getErrorCode(),
                                QueryException.DISTINCT_COUNT_ON_OUTER_JOINED_COMPOSITE_PK);
        }   
    }
    
    public static EntityManager createAlternateEntityManager() {
        return Persistence.createEntityManagerFactory("default1", JUnitTestCaseHelper.getDatabaseProperties()).createEntityManager();
    }   
       
    public static void main(String[] args)
    {
        junit.swingui.TestRunner.main(args);
    }
}
