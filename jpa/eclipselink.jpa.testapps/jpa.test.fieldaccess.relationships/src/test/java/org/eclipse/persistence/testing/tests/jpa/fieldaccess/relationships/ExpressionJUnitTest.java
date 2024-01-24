/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships;

import jakarta.persistence.EntityManager;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.relationships.Customer;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.relationships.Item;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.relationships.RelationshipsExamples;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.relationships.RelationshipsTableManager;

import java.util.List;

public class ExpressionJUnitTest extends JUnitTestCase {

    public ExpressionJUnitTest() {
    }

    public ExpressionJUnitTest(String name) {
        super(name);
    }

    @Override
    public void setUp () {
        super.setUp();
        clearCache("fieldaccess-relationships");
        new RelationshipsTableManager().replaceTables(JUnitTestCase.getServerSession("fieldaccess-relationships"));
    }


    /*
     * lefTrim(string) feature test
     *   tests that leftTrim(trim_char) works.
     */
    public void testLeftTrimWithTrimChar() {
        Platform dbPlatform = getPlatform("fieldaccess-relationships");
        if (!(dbPlatform.isOracle() || dbPlatform.isMySQL() || dbPlatform.isPostgreSQL() || dbPlatform.isSymfoware())) {
            getServerSession("fieldaccess-relationships").logMessage("Test testLeftTrimWithTrimChar skipped for this platform");
            return;
        }

        Customer c = RelationshipsExamples.customerExample4();
        //customer4.setCity("Manotick");
        EntityManager em = createEntityManager("fieldaccess-relationships");
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
        em = createEntityManager("fieldaccess-relationships");
        beginTransaction(em);
        try {
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("city").leftTrim("M").equal("anotick");

            ReadAllQuery query = new ReadAllQuery();
            query.setReferenceClass(Customer.class);
            query.setSelectionCriteria(expression);
            @SuppressWarnings({"unchecked"})
            List<Customer> result = (List<Customer>) ((JpaEntityManager)em.getDelegate()).createQuery(query).getResultList();
            commitTransaction(em);
            closeEntityManager(em);
            assertTrue("Test error: No Customers found", !result.isEmpty());
            Customer returned = result.get(0);
            assertEquals("Test error: No Customers found", "Manotick", returned.getCity());

        } catch(Exception e) {
            try {
                commitTransaction(em);
            } catch (Throwable t) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
            }
            closeEntityManager(em);
            em = createEntityManager("fieldaccess-relationships");
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
    public void testLeftTrimWithoutTrimChar() {
        // All platforms seem to support this

        Customer c = RelationshipsExamples.customerExample4();
        c.setCity(" anotick");
        EntityManager em = createEntityManager("fieldaccess-relationships");
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
        em = createEntityManager("fieldaccess-relationships");
        beginTransaction(em);
        try {
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("city").leftTrim().equal("anotick");

            ReadAllQuery query = new ReadAllQuery();
            query.setReferenceClass(Customer.class);
            query.setSelectionCriteria(expression);
            @SuppressWarnings({"unchecked"})
            List<Customer> result = (List<Customer>) ((JpaEntityManager)em.getDelegate()).createQuery(query).getResultList();
            commitTransaction(em);
            closeEntityManager(em);
            assertTrue("Test error: No Customers found", !result.isEmpty());
            Customer returned = result.get(0);
            assertEquals("Test error: No Customers found", " anotick", returned.getCity());

        } catch(Exception e) {
            try {
                commitTransaction(em);
            } catch (Throwable t) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
            }
            closeEntityManager(em);
            em = createEntityManager("fieldaccess-relationships");
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
    public void testRightTrimWithTrimChar() {
        Platform dbPlatform = getPlatform("fieldaccess-relationships");
        if (!(dbPlatform.isOracle() || dbPlatform.isMySQL() || dbPlatform.isPostgreSQL() || dbPlatform.isSymfoware())) {
            getServerSession("fieldaccess-relationships").logMessage("Test testRightTrimWithTrimChar skipped for this platform");
            return;
        }

        Customer c = RelationshipsExamples.customerExample4();
        c.setCity("ManotickM");
        EntityManager em = createEntityManager("fieldaccess-relationships");
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
        em = createEntityManager("fieldaccess-relationships");
        beginTransaction(em);
        try {
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("city").rightTrim("M").equal("Manotick");

            ReadAllQuery query = new ReadAllQuery();
            query.setReferenceClass(Customer.class);
            query.setSelectionCriteria(expression);
            @SuppressWarnings({"unchecked"})
            List<Customer> result = (List<Customer>) ((JpaEntityManager)em.getDelegate()).createQuery(query).getResultList();
            commitTransaction(em);
            closeEntityManager(em);
            assertTrue("Test error: No Items found", !result.isEmpty());
            Customer returned = result.get(0);
            assertEquals("Test error: No Customers found", "ManotickM", returned.getCity());

        } catch(Exception e) {
            try {
                commitTransaction(em);
            } catch (Throwable t) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
            }
            em = createEntityManager("fieldaccess-relationships");
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
        EntityManager em = createEntityManager("fieldaccess-relationships");
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
        em = createEntityManager("fieldaccess-relationships");
        beginTransaction(em);
        try {
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("city").rightTrim().equal("Manotic");

            ReadAllQuery query = new ReadAllQuery();
            query.setReferenceClass(Customer.class);
            query.setSelectionCriteria(expression);
            @SuppressWarnings({"unchecked"})
            List<Customer> result = (List<Customer>) ((JpaEntityManager)em.getDelegate()).createQuery(query).getResultList();
            commitTransaction(em);
            closeEntityManager(em);
            assertTrue("Test error: No Items found", !result.isEmpty());
            Customer returned = result.get(0);
            if (!getServerSession("fieldaccess-relationships").getPlatform().isMaxDB() && !getServerSession("fieldaccess-relationships").getPlatform().isSybase()) {
                // bug 327435: MaxDB trims trailing spaces of [VAR]CHAR fields
                assertEquals("Test error: No Customers found", "Manotic ", returned.getCity());
            }

        } catch(Exception e) {
            try {
                commitTransaction(em);
            } catch (Throwable t) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
            }
            em = createEntityManager("fieldaccess-relationships");
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
        Platform dbPlatform = getPlatform("fieldaccess-relationships");
        if (!(dbPlatform.isOracle() || dbPlatform.isMySQL() || dbPlatform.isPostgreSQL() || dbPlatform.isSymfoware() )) {
            getServerSession("fieldaccess-relationships").logMessage("Test testTrimWithTrimChar skipped for this platform");
            return;
        }
        Item i = new Item();
        i.setName("itemi");
        i.setDescription("itemi description");
        EntityManager em = createEntityManager("fieldaccess-relationships");
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
        em = createEntityManager("fieldaccess-relationships");
        beginTransaction(em);
        try {
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("name").trim("i").equal("tem");

            ReadAllQuery query = new ReadAllQuery();
            query.setReferenceClass(Item.class);
            query.setSelectionCriteria(expression);
            @SuppressWarnings({"unchecked"})
            List<Item> result = (List<Item>) ((JpaEntityManager)em.getDelegate()).createQuery(query).getResultList();
            commitTransaction(em);
            closeEntityManager(em);
            assertTrue("Test error: No Items found", !result.isEmpty());
            Item returned = result.get(0);
            assertEquals("Test error: No Items found", "itemi", returned.getName());

        } catch(Exception e) {
            try {
                commitTransaction(em);
            } catch (Throwable t) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
            }
            em = createEntityManager("fieldaccess-relationships");
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
        Platform dbPlatform = getPlatform("fieldaccess-relationships");
        if (!(dbPlatform.isOracle() || dbPlatform.isMySQL() || dbPlatform.isPostgreSQL()
                || dbPlatform.isInformix() || dbPlatform.isSQLAnywhere() || dbPlatform.isHSQL() || dbPlatform.isSymfoware())) {
            getServerSession("fieldaccess-relationships").logMessage("Test testTrimWithoutTrimChar skipped for this platform");
            return;
        }
        Item i = new Item();
        i.setName(" tem ");
        i.setDescription("itemi description");
        EntityManager em = createEntityManager("fieldaccess-relationships");
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
        em = createEntityManager("fieldaccess-relationships");
        beginTransaction(em);
        try {
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("name").trim().equal("tem");

            ReadAllQuery query = new ReadAllQuery();
            query.setReferenceClass(Item.class);
            query.setSelectionCriteria(expression);
            @SuppressWarnings({"unchecked"})
            List<Item> result = (List<Item>) ((JpaEntityManager)em.getDelegate()).createQuery(query).getResultList();
            commitTransaction(em);
            closeEntityManager(em);
            assertTrue("Test error: No Items found", !result.isEmpty());
            Item returned = result.get(0);
            assertEquals("Test error: No Items found", " tem ", returned.getName());

        } catch(Exception e) {
            try {
                commitTransaction(em);
            } catch (Throwable t) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
            }
            em = createEntityManager("fieldaccess-relationships");
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
        EntityManager em = createEntityManager("fieldaccess-relationships");
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
        em = createEntityManager("fieldaccess-relationships");
        beginTransaction(em);
        try {
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("name").locate("t").equal(Integer.valueOf(2));

            ReadAllQuery query = new ReadAllQuery();
            query.setReferenceClass(Item.class);
            query.setSelectionCriteria(expression);
            @SuppressWarnings({"unchecked"})
            List<Item> result = (List<Item>) ((JpaEntityManager)em.getDelegate()).createQuery(query).getResultList();
            commitTransaction(em);
            closeEntityManager(em);
            assertTrue("Test error: No Items found", !result.isEmpty());
            Item returned = result.get(0);
            assertEquals("Test error: IncorrectItem found", "itemi", returned.getName());

        } catch(Exception e) {
            try {
                commitTransaction(em);
            } catch (Throwable t) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
            }
            em = createEntityManager("fieldaccess-relationships");
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
        EntityManager em = createEntityManager("fieldaccess-relationships");
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
        em = createEntityManager("fieldaccess-relationships");
        beginTransaction(em);
        try {
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("name").locate("i", 2).equal(Integer.valueOf(5));

            ReadAllQuery query = new ReadAllQuery();
            query.setReferenceClass(Item.class);
            query.setSelectionCriteria(expression);
            @SuppressWarnings({"unchecked"})
            List<Item> result = (List<Item>) ((JpaEntityManager)em.getDelegate()).createQuery(query).getResultList();
            commitTransaction(em);
            closeEntityManager(em);
            assertTrue("Test error: No Items found", !result.isEmpty());
            Item returned = result.get(0);
            assertEquals("Test error: IncorrectItem found", "itemi", returned.getName());

        } catch(Exception e) {
            try {
                commitTransaction(em);
            } catch (Throwable t) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
            }
            em = createEntityManager("fieldaccess-relationships");
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
