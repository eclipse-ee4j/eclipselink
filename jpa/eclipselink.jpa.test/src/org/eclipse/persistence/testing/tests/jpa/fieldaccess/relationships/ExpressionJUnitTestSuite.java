/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships;

import java.util.List;

import javax.persistence.EntityManager;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.queries.ReadAllQuery;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.relationships.*;

public class ExpressionJUnitTestSuite extends JUnitTestCase {
        
    public ExpressionJUnitTestSuite() {
    }
    
    public ExpressionJUnitTestSuite(String name) {
        super(name);
    }
    
    public void setUp () {
        super.setUp();
        clearCache("fieldaccess");
        new RelationshipsTableManager().replaceTables(JUnitTestCase.getServerSession("fieldaccess"));        
    }

    
    /*
     * lefTrim(string) feature test
     *   tests that leftTrim(trim_char) works.
     */
    public void testLeftTrimWithTrimChar() throws Exception {
        Platform dbPlatform = getPlatform("fieldaccess");
        if (!(dbPlatform.isOracle() || dbPlatform.isMySQL() || dbPlatform.isPostgreSQL() || dbPlatform.isSymfoware())) {
            getServerSession("fieldaccess").logMessage("Test testLeftTrimWithTrimChar skipped for this platform");
            return;
        }

        Customer c = RelationshipsExamples.customerExample4();
        //customer4.setCity("Manotick");
        EntityManager em = createEntityManager("fieldaccess");
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
        em = createEntityManager("fieldaccess");
        beginTransaction(em);
        try {
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("city").leftTrim("M").equal("anotick");

            ReadAllQuery query = new ReadAllQuery();
            query.setReferenceClass(Customer.class);
            query.setSelectionCriteria(expression);
            List result = ((JpaEntityManager)em.getDelegate()).createQuery(query).getResultList();
            commitTransaction(em);
            closeEntityManager(em);
            assertTrue("Test error: No Customers found", result.size()!=0 );
            Customer returned = (Customer)result.get(0);
            assertTrue("Test error: No Customers found","Manotick".equals(returned.getCity()) );

        } catch(Exception e) {
            try {
                commitTransaction(em);
            } catch (Throwable t) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
            }
            closeEntityManager(em);
            em = createEntityManager("fieldaccess");
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
     * lefTrim() feature test
     *   tests that leftTrim() works.
     */
    public void testLeftTrimWithoutTrimChar() throws Exception {
        // All platforms seem to support this

        Customer c = RelationshipsExamples.customerExample4();
        c.setCity(" anotick");
        EntityManager em = createEntityManager("fieldaccess");
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
        em = createEntityManager("fieldaccess");
        beginTransaction(em);
        try {
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("city").leftTrim().equal("anotick");

            ReadAllQuery query = new ReadAllQuery();
            query.setReferenceClass(Customer.class);
            query.setSelectionCriteria(expression);
            List result = ((JpaEntityManager)em.getDelegate()).createQuery(query).getResultList();
            commitTransaction(em);
            closeEntityManager(em);
            assertTrue("Test error: No Customers found", result.size()!=0 );
            Customer returned = (Customer)result.get(0);
            assertTrue("Test error: No Customers found", " anotick".equals(returned.getCity()) );

        } catch(Exception e) {
            try {
                commitTransaction(em);
            } catch (Throwable t) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
            }
            closeEntityManager(em);
            em = createEntityManager("fieldaccess");
            beginTransaction(em);
            c = em.find(Customer.class, c.getCustomerId());
            em.remove(c);
            try {
                commitTransaction(em);
            } catch (Throwable t) {
                if (isTransactionActive(em)) {
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
        Platform dbPlatform = getPlatform("fieldaccess");
        if (!(dbPlatform.isOracle() || dbPlatform.isMySQL() || dbPlatform.isPostgreSQL() || dbPlatform.isSymfoware())) {
            getServerSession("fieldaccess").logMessage("Test testRightTrimWithTrimChar skipped for this platform");
            return;
        }

        Customer c = RelationshipsExamples.customerExample4();
        c.setCity("ManotickM");
        EntityManager em = createEntityManager("fieldaccess");
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
        em = createEntityManager("fieldaccess");
        beginTransaction(em);
        try {
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("city").rightTrim("M").equal("Manotick");

            ReadAllQuery query = new ReadAllQuery();
            query.setReferenceClass(Customer.class);
            query.setSelectionCriteria(expression);
            List result = ((JpaEntityManager)em.getDelegate()).createQuery(query).getResultList();
            commitTransaction(em);
            closeEntityManager(em);
            assertTrue("Test error: No Items found", result.size()!=0 );
            Customer returned = (Customer)result.get(0);
            assertTrue("Test error: No Customers found", "ManotickM".equals(returned.getCity()) );

        } catch(Exception e) {
            try {
                commitTransaction(em);
            } catch (Throwable t) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
            }
            em = createEntityManager("fieldaccess");
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
        EntityManager em = createEntityManager("fieldaccess");
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
        em = createEntityManager("fieldaccess");
        beginTransaction(em);
        try {
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("city").rightTrim().equal("Manotic");

            ReadAllQuery query = new ReadAllQuery();
            query.setReferenceClass(Customer.class);
            query.setSelectionCriteria(expression);
            List result = ((JpaEntityManager)em.getDelegate()).createQuery(query).getResultList();
            commitTransaction(em);
            closeEntityManager(em);
            assertTrue("Test error: No Items found", result.size()!=0 );
            Customer returned = (Customer)result.get(0);
            if (!getServerSession("fieldaccess").getPlatform().isMaxDB()) {
                // bug 327435: MaxDB trims trailing spaces of [VAR]CHAR fields
                assertTrue("Test error: No Customers found", "Manotic ".equals(returned.getCity()));
            }

        } catch(Exception e) {
            try {
                commitTransaction(em);
            } catch (Throwable t) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
            }
            em = createEntityManager("fieldaccess");
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
        Platform dbPlatform = getPlatform("fieldaccess");
        if (!(dbPlatform.isOracle() || dbPlatform.isMySQL() || dbPlatform.isPostgreSQL() || dbPlatform.isSymfoware() )) {
            getServerSession("fieldaccess").logMessage("Test testTrimWithTrimChar skipped for this platform");
            return;
        }
        Item i = new Item();
        i.setName("itemi");
        i.setDescription("itemi description");
        EntityManager em = createEntityManager("fieldaccess");
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
        em = createEntityManager("fieldaccess");
        beginTransaction(em);
        try {
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("name").trim("i").equal("tem");

            ReadAllQuery query = new ReadAllQuery();
            query.setReferenceClass(Item.class);
            query.setSelectionCriteria(expression);
            List result = ((JpaEntityManager)em.getDelegate()).createQuery(query).getResultList();
            commitTransaction(em);
            closeEntityManager(em);
            assertTrue("Test error: No Items found", result.size()!=0 );
            Item returned = (Item)result.get(0);
            assertTrue("Test error: No Items found","itemi".equals(returned.getName()) );

        } catch(Exception e) {
            try {
                commitTransaction(em);
            } catch (Throwable t) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
            }
            em = createEntityManager("fieldaccess");
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
        Platform dbPlatform = getPlatform("fieldaccess");
        if (!(dbPlatform.isOracle() || dbPlatform.isMySQL() || dbPlatform.isPostgreSQL() 
                || dbPlatform.isInformix() || dbPlatform.isSQLAnywhere() || dbPlatform.isHSQL() || dbPlatform.isSymfoware())) {
            getServerSession("fieldaccess").logMessage("Test testTrimWithoutTrimChar skipped for this platform");
            return;
        }
        Item i = new Item();
        i.setName(" tem ");
        i.setDescription("itemi description");
        EntityManager em = createEntityManager("fieldaccess");
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
        em = createEntityManager("fieldaccess");
        beginTransaction(em);
        try {
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("name").trim().equal("tem");

            ReadAllQuery query = new ReadAllQuery();
            query.setReferenceClass(Item.class);
            query.setSelectionCriteria(expression);
            List result = ((JpaEntityManager)em.getDelegate()).createQuery(query).getResultList();
            commitTransaction(em);
            closeEntityManager(em);
            assertTrue("Test error: No Items found", result.size()!=0 );
            Item returned = (Item)result.get(0);
            assertTrue("Test error: No Items found"," tem ".equals(returned.getName()) );

        } catch(Exception e) {
            try {
                commitTransaction(em);
            } catch (Throwable t) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
            }
            em = createEntityManager("fieldaccess");
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
        EntityManager em = createEntityManager("fieldaccess");
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
        em = createEntityManager("fieldaccess");
        beginTransaction(em);
        try {
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("name").locate("t").equal(new Integer(2));

            ReadAllQuery query = new ReadAllQuery();
            query.setReferenceClass(Item.class);
            query.setSelectionCriteria(expression);
            List result = ((JpaEntityManager)em.getDelegate()).createQuery(query).getResultList();
            commitTransaction(em);
            closeEntityManager(em);
            assertTrue("Test error: No Items found", result.size()!=0 );
            Item returned = (Item)result.get(0);
            assertTrue("Test error: IncorrectItem found","itemi".equals(returned.getName()) );

        } catch(Exception e) {
            try {
                commitTransaction(em);
            } catch (Throwable t) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
            }
            em = createEntityManager("fieldaccess");
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
        EntityManager em = createEntityManager("fieldaccess");
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
        em = createEntityManager("fieldaccess");
        beginTransaction(em);
        try {
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("name").locate("i", 2).equal(new Integer(5));

            ReadAllQuery query = new ReadAllQuery();
            query.setReferenceClass(Item.class);
            query.setSelectionCriteria(expression);
            List result = ((JpaEntityManager)em.getDelegate()).createQuery(query).getResultList();
            commitTransaction(em);
            closeEntityManager(em);
            assertTrue("Test error: No Items found", result.size()!=0 );
            Item returned = (Item)result.get(0);
            assertTrue("Test error: IncorrectItem found","itemi".equals(returned.getName()) );

        } catch(Exception e) {
            try {
                commitTransaction(em);
            } catch (Throwable t) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
            }
            em = createEntityManager("fieldaccess");
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
