/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

package org.eclipse.persistence.testing.tests.jpa.relationships;

import java.util.Vector;

import javax.persistence.EntityManager;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

import org.eclipse.persistence.testing.models.jpa.relationships.*;

public class ExpressionJUnitTestSuite extends JUnitTestCase {
        
    public ExpressionJUnitTestSuite() {
    }
    
    public ExpressionJUnitTestSuite(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("ExpressionJUnitTestSuite");
        
        suite.addTest(new ExpressionJUnitTestSuite("testSetup"));
        suite.addTest(new ExpressionJUnitTestSuite("testLeftTrimWithTrimChar"));
        suite.addTest(new ExpressionJUnitTestSuite("testLeftTrimWithoutTrimChar"));
        suite.addTest(new ExpressionJUnitTestSuite("testRightTrimWithTrimChar"));
        suite.addTest(new ExpressionJUnitTestSuite("testRightTrimWithoutTrimChar"));
        suite.addTest(new ExpressionJUnitTestSuite("testTrimWithTrimChar"));
        suite.addTest(new ExpressionJUnitTestSuite("testTrimWithoutTrimChar"));
        suite.addTest(new ExpressionJUnitTestSuite("testLocateWithSingleArgument"));
        suite.addTest(new ExpressionJUnitTestSuite("testLocateWithDoubleArgument"));
        suite.addTest(new ExpressionJUnitTestSuite("testLocateWithDoubleArgument_Neg"));
        
        return suite;
    }
    
    public void testSetup() {
        clearCache();
        new RelationshipsTableManager().replaceTables(JUnitTestCase.getServerSession());        
    }

    
    /*
     * leftTrim(string) feature test
     *   tests that leftTrim(trim_char) works.
     */
    public void testLeftTrimWithTrimChar() throws Exception {
        Platform dbPlatform = getDbPlatform();
        if (!(dbPlatform.isOracle() || dbPlatform.isMySQL() || dbPlatform.isPostgreSQL() || dbPlatform.isSymfoware())) {
            getServerSession().logMessage("Test testLeftTrimWithTrimChar skipped for this platform");
            return;
        }

        Customer c = RelationshipsExamples.customerExample4();
        //customer4.setCity("Manotick");
        EntityManager em = createEntityManager();
        try{
            
            beginTransaction(em);
            em.persist(c);
            commitTransaction(em);
        }catch (Exception e){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        }
        closeEntityManager(em);
        try{
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("city").leftTrim("M").equal("anotick");

            ReadAllQuery r = new ReadAllQuery();
            r.setReferenceClass(Customer.class);
            r.setSelectionCriteria(expression);
            Vector v = (Vector)getServerSession().executeQuery(r);
            assertTrue("Test error: No Customers found",v.size()!=0 );
            Customer returned = (Customer)v.firstElement();
            assertTrue("Test error: No Customers found","Manotick".equals(returned.getCity()) );

        }catch(Exception e){
            em = createEntityManager();
            beginTransaction(em);
            c = em.find(Customer.class, c.getCustomerId());
            em.remove(c);
            try{
                commitTransaction(em);
            }catch (Throwable t){
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /*
     * leftTrim() feature test
     *   tests that leftTrim() works.
     */
    public void testLeftTrimWithoutTrimChar() throws Exception {
        // All platforms seem to support this

        Customer c = RelationshipsExamples.customerExample4();
        c.setCity(" anotick");
        EntityManager em = createEntityManager();
        try{
            beginTransaction(em);
            em.persist(c);
            commitTransaction(em);
        }catch (Exception e){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        }
        closeEntityManager(em);
        try{
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("city").leftTrim().equal("anotick");

            ReadAllQuery r = new ReadAllQuery();
            r.setReferenceClass(Customer.class);
            r.setSelectionCriteria(expression);
            Vector v = (Vector)getServerSession().executeQuery(r);
            assertTrue("Test error: No Customers found", v.size()!=0 );
            Customer returned = (Customer)v.firstElement();
            assertTrue("Test error: No Customers found", " anotick".equals(returned.getCity()) );

        }catch(Exception e){
            em = createEntityManager();
            beginTransaction(em);
            c = em.find(Customer.class, c.getCustomerId());
            em.remove(c);
            try{
                commitTransaction(em);
            }catch (Throwable t){
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
            }
            closeEntityManager(em);
            throw e;
        }
    }
    

    /*
     * rightTrim(string) feature test
     *   tests that rightTrim(trim_char) works.
     */
    public void testRightTrimWithTrimChar() throws Exception {
        Platform dbPlatform = getDbPlatform();
        if (!(dbPlatform.isOracle() || dbPlatform.isMySQL() || dbPlatform.isPostgreSQL() || dbPlatform.isSymfoware())) {
            getServerSession().logMessage("Test testRightTrimWithTrimChar skipped for this platform");
            return;
        }

        Customer c = RelationshipsExamples.customerExample4();
        c.setCity("ManotickM");
        EntityManager em = createEntityManager();
        try{
            beginTransaction(em);
            em.persist(c);
            commitTransaction(em);
        }catch (Exception e){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        }
        closeEntityManager(em);
        try{
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("city").rightTrim("M").equal("Manotick");

            ReadAllQuery r = new ReadAllQuery();
            r.setReferenceClass(Customer.class);
            r.setSelectionCriteria(expression);
            Vector v = (Vector)getServerSession().executeQuery(r);
            assertTrue("Test error: No Customers found", v.size()!=0 );
            Customer returned = (Customer)v.firstElement();
            assertTrue("Test error: No Customers found", "ManotickM".equals(returned.getCity()) || "Manotick".equals(returned.getCity()));

        }catch(Exception e){
            em = createEntityManager();
            beginTransaction(em);
            c = em.find(Customer.class, c.getCustomerId());
            em.remove(c);
            try{
                commitTransaction(em);
            }catch (Throwable t){
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /*
     * rightTrim() feature test
     *   tests that rightTrim() works.
     */
    public void testRightTrimWithoutTrimChar() throws Exception {
        // All platforms seem to support this

        Customer c = RelationshipsExamples.customerExample4();
        c.setCity("Manotic ");
        EntityManager em = createEntityManager();
        try{
            
            beginTransaction(em);
            em.persist(c);
            commitTransaction(em);
        }catch (Exception e){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        }
        closeEntityManager(em);
        try{
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("city").rightTrim().equal("Manotic");

            ReadAllQuery r = new ReadAllQuery();
            r.setReferenceClass(Customer.class);
            r.setSelectionCriteria(expression);
            Vector v = (Vector)getServerSession().executeQuery(r);
            assertTrue("Test error: No Customers found", v.size()!=0 );
            Customer returned = (Customer)v.firstElement();
            assertTrue("Test error: No Customers found", "Manotic ".equals(returned.getCity()) );

        }catch(Exception e){
            em = createEntityManager();
            beginTransaction(em);
            c = em.find(Customer.class, c.getCustomerId());
            em.remove(c);
            try{
                commitTransaction(em);
            }catch (Throwable t){
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /*
     * trim(string) feature test
     *   tests that trim(trim_char) works.
     */
    public void testTrimWithTrimChar() throws Exception {
        Platform dbPlatform = getDbPlatform();
        if (!(dbPlatform.isOracle() || dbPlatform.isMySQL() || dbPlatform.isPostgreSQL() || dbPlatform.isSymfoware())) {
            getServerSession().logMessage("Test testTrimWithTrimChar skipped for this platform");
            return;
        }
        Item i = new Item();
        i.setName("itemi");
        i.setDescription("itemi description");
        EntityManager em = createEntityManager();
        try{
            beginTransaction(em);
            em.persist(i);
            commitTransaction(em);
        }catch (Exception e){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        }
        closeEntityManager(em);
        try{
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("name").trim("i").equal("tem");
            
            ReadAllQuery r = new ReadAllQuery();
            r.setReferenceClass(Item.class);
            r.setSelectionCriteria(expression);
            Vector v = (Vector)getServerSession().executeQuery(r);
            assertTrue("Test error: No Items found",v.size()!=0 );
            Item returned = (Item)v.firstElement();
            assertTrue("Test error: No Items found","itemi".equals(returned.getName()) );
            
        }catch(Exception e){
            em = createEntityManager();
            try{
                beginTransaction(em);
                i = em.find(Item.class, i.getItemId());
                em.remove(i);
                commitTransaction(em);
            }catch (Throwable t){
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /*
     * trim(string) feature test
     *   tests that trim() works.
     */
    public void testTrimWithoutTrimChar() throws Exception {
        Platform dbPlatform = getDbPlatform();
        if (!(dbPlatform.isOracle() || dbPlatform.isMySQL() || dbPlatform.isPostgreSQL() 
                || dbPlatform.isInformix() || dbPlatform.isSQLAnywhere() || dbPlatform.isHSQL() || dbPlatform.isSymfoware())) {
            getServerSession().logMessage("Test testTrimWithoutTrimChar skipped for this platform");
            return;
        }
        Item i = new Item();
        i.setName(" tem ");
        i.setDescription("itemi description");
        EntityManager em = createEntityManager();
        try{
            beginTransaction(em);
            em.persist(i);
            commitTransaction(em);
        }catch (Exception e){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        }
        closeEntityManager(em);
        try{
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("name").trim().equal("tem");

            ReadAllQuery r = new ReadAllQuery();
            r.setReferenceClass(Item.class);
            r.setSelectionCriteria(expression);
            Vector v = (Vector)getServerSession().executeQuery(r);
            assertTrue("Test error: No Items found",v.size()!=0 );
            Item returned = (Item)v.firstElement();
            assertTrue("Test error: No Items found"," tem ".equals(returned.getName()) );
        }catch(Exception e){
            em = createEntityManager();
            try{
                beginTransaction(em);
                i = em.find(Item.class, i.getItemId());
                em.remove(i);
                commitTransaction(em);
            }catch (Throwable t){
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /*
     * locate(string) feature test
     *   tests that locate(string) works.
     */
    public void testLocateWithSingleArgument() throws Exception {
        Item i = new Item();
        i.setName("itemi");
        i.setDescription("itemi description");
        EntityManager em = createEntityManager();
        try{
            beginTransaction(em);
            em.persist(i);
            commitTransaction(em);
        }catch (Exception e){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        }
        closeEntityManager(em);
        try{
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("name").locate("t").equal(new Integer(2));
            
            ReadAllQuery r = new ReadAllQuery();
            r.setReferenceClass(Item.class);
            r.setSelectionCriteria(expression);
            Vector v = (Vector)getServerSession().executeQuery(r);
            assertTrue("Test error: No Items found", v.size()!=0 );
            Item returned = (Item)v.firstElement();
            assertTrue("Test error: IncorrectItem found","itemi".equals(returned.getName()) );
            
        }catch(Exception e){
            em = createEntityManager();
            try{
                beginTransaction(em);
                i = em.find(Item.class, i.getItemId());
                em.remove(i);
                commitTransaction(em);
            }catch (Throwable t){
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /*
     * locate(string, int) feature test
     *   tests that locate(string, int) works.
     */
    public void testLocateWithDoubleArgument() throws Exception {
        Item i = new Item();
        i.setName("itemi");
        i.setDescription("itemi description");
        EntityManager em = createEntityManager();
        try{
            beginTransaction(em);
            em.persist(i);
            commitTransaction(em);
        }catch (Exception e){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        }
        closeEntityManager(em);
        try{
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("name").locate("i", 2).equal(new Integer(5));
            
            ReadAllQuery r = new ReadAllQuery();
            r.setReferenceClass(Item.class);
            r.setSelectionCriteria(expression);
            Vector v = (Vector)getServerSession().executeQuery(r);
            assertTrue("Test error: No Items found", v.size()!=0 );
            Item returned = (Item)v.firstElement();
            assertTrue("Test error: IncorrectItem found","itemi".equals(returned.getName()) );
            
        }catch(Exception e){
            em = createEntityManager();
            try{
                beginTransaction(em);
                i = em.find(Item.class, i.getItemId());
                em.remove(i);
                commitTransaction(em);
            }catch (Throwable t){
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /*
     * locate(string, int) feature test
     *   Negative case: tests that locate(string, int) works when the string is not included  (bug 299334)
     * This test will fail on PostGresql until bug 299334 is fixed
     */
    public void testLocateWithDoubleArgument_Neg() throws Exception {
        Item i = new Item();
        i.setName("itemi");
        i.setDescription("itemi description");
        EntityManager em = createEntityManager();
        try{
            beginTransaction(em);
            em.persist(i);
            commitTransaction(em);
        }catch (Exception e){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        }
        closeEntityManager(em);
        try{
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("name").locate("t", 4).equal(new Integer(0));
            
            ReadAllQuery r = new ReadAllQuery();
            r.setReferenceClass(Item.class);
            r.setSelectionCriteria(expression);
            Vector v = (Vector)getServerSession().executeQuery(r);
            assertTrue("Test error: No Items found", v.size()!=0 );
            Item returned = (Item)v.firstElement();
            assertTrue("Test error: IncorrectItem found","itemi".equals(returned.getName()) );
            
        }catch(Exception e){
            em = createEntityManager();
            try{
                beginTransaction(em);
                i = em.find(Item.class, i.getItemId());
                em.remove(i);
                commitTransaction(em);
            }catch (Throwable t){
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
            }
            closeEntityManager(em);
            throw e;
        }
    }

}
