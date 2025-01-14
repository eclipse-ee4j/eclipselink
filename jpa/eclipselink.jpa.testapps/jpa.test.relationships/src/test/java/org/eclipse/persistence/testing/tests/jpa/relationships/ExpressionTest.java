/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.tests.jpa.relationships;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.relationships.Customer;
import org.eclipse.persistence.testing.models.jpa.relationships.Item;
import org.eclipse.persistence.testing.models.jpa.relationships.RelationshipsExamples;
import org.eclipse.persistence.testing.models.jpa.relationships.RelationshipsTableManager;

import java.util.Vector;

public class ExpressionTest extends JUnitTestCase {

    public ExpressionTest() {
    }

    public ExpressionTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("ExpressionTest");

        suite.addTest(new ExpressionTest("testSetup"));
        suite.addTest(new ExpressionTest("testLeftTrimWithTrimChar"));
        suite.addTest(new ExpressionTest("testLeftTrimWithoutTrimChar"));
        suite.addTest(new ExpressionTest("testRightTrimWithTrimChar"));
        suite.addTest(new ExpressionTest("testRightTrimWithoutTrimChar"));
        suite.addTest(new ExpressionTest("testTrimWithTrimChar"));
        suite.addTest(new ExpressionTest("testTrimWithoutTrimChar"));
        suite.addTest(new ExpressionTest("testLocateWithSingleArgument"));
        suite.addTest(new ExpressionTest("testLocateWithDoubleArgument"));
        suite.addTest(new ExpressionTest("testLocateWithDoubleArgument_Neg"));

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
    public void testLeftTrimWithTrimChar() {
        Platform dbPlatform = getPlatform();
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
            @SuppressWarnings({"unchecked"})
            Vector<Customer> v = (Vector<Customer>)getServerSession().executeQuery(r);
            assertTrue("Test error: No Customers found",v.size()!=0 );
            Customer returned = v.firstElement();
            assertEquals("Test error: No Customers found", "Manotick", returned.getCity());

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
    public void testLeftTrimWithoutTrimChar() {
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
            @SuppressWarnings({"unchecked"})
            Vector<Customer> v = (Vector<Customer>)getServerSession().executeQuery(r);
            assertTrue("Test error: No Customers found", v.size()!=0 );
            Customer returned = v.firstElement();
            assertEquals("Test error: No Customers found", " anotick", returned.getCity());

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
    public void testRightTrimWithTrimChar() {
        Platform dbPlatform = getPlatform();
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
            @SuppressWarnings({"unchecked"})
            Vector<Customer> v = (Vector<Customer>)getServerSession().executeQuery(r);
            assertTrue("Test error: No Customers found", v.size()!=0 );
            Customer returned = v.firstElement();
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
    public void testRightTrimWithoutTrimChar() {
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
            @SuppressWarnings({"unchecked"})
            Vector<Customer> v = (Vector<Customer>)getServerSession().executeQuery(r);
            assertTrue("Test error: No Customers found", v.size()!=0 );
            Customer returned = v.firstElement();
            assertEquals("Test error: No Customers found", "Manotic ", returned.getCity());

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
    public void testTrimWithTrimChar() {
        Platform dbPlatform = getPlatform();
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
            @SuppressWarnings({"unchecked"})
            Vector<Item> v = (Vector<Item>)getServerSession().executeQuery(r);
            assertTrue("Test error: No Items found",v.size()!=0 );
            Item returned = v.firstElement();
            assertEquals("Test error: No Items found", "itemi", returned.getName());

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
    public void testTrimWithoutTrimChar() {
        Platform dbPlatform = getPlatform();
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
            @SuppressWarnings({"unchecked"})
            Vector<Item> v = (Vector<Item>)getServerSession().executeQuery(r);
            assertTrue("Test error: No Items found",v.size()!=0 );
            Item returned = v.firstElement();
            assertEquals("Test error: No Items found", " tem ", returned.getName());
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
    public void testLocateWithSingleArgument() {
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
            Expression expression = builder.get("name").locate("t").equal(Integer.valueOf(2));

            ReadAllQuery r = new ReadAllQuery();
            r.setReferenceClass(Item.class);
            r.setSelectionCriteria(expression);
            @SuppressWarnings({"unchecked"})
            Vector<Item> v = (Vector<Item>)getServerSession().executeQuery(r);
            assertTrue("Test error: No Items found", v.size()!=0 );
            Item returned = v.firstElement();
            assertEquals("Test error: IncorrectItem found", "itemi", returned.getName());

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
    public void testLocateWithDoubleArgument() {
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
            Expression expression = builder.get("name").locate("i", 2).equal(Integer.valueOf(5));

            ReadAllQuery r = new ReadAllQuery();
            r.setReferenceClass(Item.class);
            r.setSelectionCriteria(expression);
            @SuppressWarnings({"unchecked"})
            Vector<Item> v = (Vector<Item>)getServerSession().executeQuery(r);
            assertTrue("Test error: No Items found", v.size()!=0 );
            Item returned = v.firstElement();
            assertEquals("Test error: IncorrectItem found", "itemi", returned.getName());

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
    public void testLocateWithDoubleArgument_Neg() {
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
            Expression expression = builder.get("name").locate("t", 4).equal(Integer.valueOf(0));

            ReadAllQuery r = new ReadAllQuery();
            r.setReferenceClass(Item.class);
            r.setSelectionCriteria(expression);
            @SuppressWarnings({"unchecked"})
            Vector<Item> v = (Vector<Item>)getServerSession().executeQuery(r);
            assertTrue("Test error: No Items found", v.size()!=0 );
            Item returned = v.firstElement();
            assertEquals("Test error: IncorrectItem found", "itemi", returned.getName());

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
