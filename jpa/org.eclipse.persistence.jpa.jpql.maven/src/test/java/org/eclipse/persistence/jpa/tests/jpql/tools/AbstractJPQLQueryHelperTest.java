/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.tests.jpql.tools;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Vector;
import org.eclipse.persistence.jpa.jpql.tools.AbstractJPQLQueryHelper;
import org.eclipse.persistence.jpa.jpql.tools.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.tools.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeRepository;
import org.eclipse.persistence.jpa.tests.jpql.JPQLCoreTest;
import org.eclipse.persistence.jpa.tests.jpql.JPQLQueryHelperTestHelper;
import org.eclipse.persistence.jpa.tests.jpql.tools.spi.java.JavaQuery;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * The unit-tests for {@link AbstractJPQLQueryHelper#getParameterType(String)} and
 * {@link AbstractJPQLQueryHelper#getResultType()}.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractJPQLQueryHelperTest extends JPQLCoreTest {

    @JPQLQueryHelperTestHelper
    private AbstractJPQLQueryHelper jpqlQueryHelper;

    protected AbstractJPQLQueryHelper buildQueryHelper(IQuery namedQuery) {
        jpqlQueryHelper.setQuery(namedQuery);
        return jpqlQueryHelper;
    }

    protected IManagedType entity(IManagedTypeProvider provider, String entityName) throws Exception {
        IEntity entity = provider.getEntityNamed(entityName);
        assertNotNull("The entity " + entityName + " could not be found", entity);
        return entity;
    }

    protected IType getType(IQuery query, Class<?> type) {
        return typeRepository(query).getType(type);
    }

    protected IType getType(IQuery query, String typeName) {
        return typeRepository(query).getType(typeName);
    }

    protected IType mappedType(IQuery query, String entityName) {
        return query.getProvider().getEntityNamed(entityName).getType();
    }

    protected abstract IQuery namedQuery(String entityName, String queryName) throws Exception;

    protected IManagedTypeProvider provider(IQuery query) {
        return query.getProvider();
    }

    @Override
    protected void tearDown() throws Exception {
        jpqlQueryHelper.dispose();
        super.tearDown();
    }

    @Override
    protected void tearDownClass() throws Exception {
        jpqlQueryHelper = null;
        super.tearDownClass();
    }

    @Test
    public final void test_ParameterType_01() throws Exception {

        // select ds from Employee ds, Product ci where ds.empId = :conversationId AND ds.state = 0 AND ds.cikey = ci.cikey AND  ci.state <> 2
        IQuery namedQuery = namedQuery("Employee", "employee.empId");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType(":conversationId");

        assertNotNull(
            "The type of :conversationId should have been found",
            type
        );

        assertEquals(
            "The wrong type for :conversationId was retrieved",
            getType(namedQuery, Long.class),
            type
        );
    }

    @Test
    public final void test_ParameterType_02() throws Exception {

        // SELECT a.alias FROM Alias AS a WHERE (a.alias IS NULL AND :param1 IS NULL) OR a.alias = :param1
        IQuery namedQuery = namedQuery("Alias", "alias.param1");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType(":param1");

        assertNotNull(
            "The type of :param1 should have been found",
            type
        );

        assertEquals(
            "The wrong type for :param1 was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ParameterType_03() throws Exception {

        // SELECT Distinct Object(c) From Customer c, IN(c.home.phones) p where p.area LIKE :area
        IQuery namedQuery = namedQuery("Customer", "customer.area");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType(":area");

        assertNotNull(
            "The type of :area should have been found",
            type
        );

        assertEquals(
            "The wrong type for :area was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ParameterType_04() throws Exception {

        // select c.firstName FROM Customer c Group By c.firstName HAVING c.firstName = concat(:fname, :lname)
        IQuery namedQuery = namedQuery("Customer", "customer.name");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType(":fname");

        assertNotNull(
            "The type of :fname should have been found",
            type
        );

        assertEquals(
            "The wrong type for :fname was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ParameterType_05() throws Exception {

        // select count(c) FROM Customer c JOIN c.aliases a GROUP BY a.alias HAVING a.alias = SUBSTRING(:string1, :int1, :int2)
        IQuery namedQuery = namedQuery("Customer", "customer.substring");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType(":int1");

        assertNotNull(
            "The type of :int1 should have been found",
            type
        );

        assertEquals(
            "The wrong type for :int1 was retrieved",
            getType(namedQuery, Integer.class),
            type
        );
    }

    @Test
    public final void test_ParameterType_06() throws Exception {

        // select count(c) FROM Customer c JOIN c.aliases a GROUP BY a.alias HAVING a.alias = SUBSTRING(:string1, :int1, :int2)
        IQuery namedQuery = namedQuery("Customer", "customer.substring");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType(":int2");

        assertNotNull(
            "The type of :int2 should have been found",
            type
        );

        assertEquals(
            "The wrong type for :int2 was retrieved",
            getType(namedQuery, Integer.class),
            type
        );
    }

    @Test
    public final void test_ParameterType_07() throws Exception {

        // select c.firstName FROM Customer c Group By c.firstName HAVING c.firstName = concat(:fname, :lname)
        IQuery namedQuery = namedQuery("Customer", "customer.name");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType(":lname");

        assertNotNull(
            "The type of :lname should have been found",
            type
        );

        assertEquals(
            "The wrong type for :lname was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ParameterType_08() throws Exception {

        // select count(c) FROM Customer c JOIN c.aliases a GROUP BY a.alias HAVING a.alias = SUBSTRING(:string1, :int1, :int2)
        IQuery namedQuery = namedQuery("Customer", "customer.substring");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType(":string1");

        assertNotNull(
            "The type of :string1 should have been found",
            type
        );

        assertEquals(
            "The wrong type for :string1 was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ParameterType_09() throws Exception {

        // select o from Dept o where o.dname in (:dname1, :dname2, :dname3)
        IQuery namedQuery = namedQuery("Dept", "dept.dname");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType(":dname1");

        assertNotNull(
            "The type of :dname1 should have been found",
            type
        );

        assertEquals(
            "The wrong type for :newdate was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ParameterType_10() throws Exception {

        // select o from Dept o where o.dname in (:dname1, :dname2, :dname3)
        IQuery namedQuery = namedQuery("Dept", "dept.dname");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType(":dname2");

        assertNotNull(
            "The type of :dname2 should have been found",
            type
        );

        assertEquals(
            "The wrong type for :newdate was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ParameterType_11() throws Exception {

        // select o from Dept o where o.dname in (:dname1, :dname2, :dname3)
        IQuery namedQuery = namedQuery("Dept", "dept.dname");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType(":dname3");

        assertNotNull(
            "The type of :dname3 should have been found",
            type
        );

        assertEquals(
            "The wrong type for :newdate was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ParameterType_12() throws Exception {

        // SELECT e FROM Employee e WHERE e.department = :dept AND e.salary > :base
        IQuery namedQuery = namedQuery("Employee", "employee.deptBase");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType(":base");

        assertNotNull(
            "The type of :base should have been found",
            type
        );

        assertEquals(
            "The wrong type for :base was retrieved",
            getType(namedQuery, Long.class),
            type
        );
    }

    @Test
    public final void test_ParameterType_13() throws Exception {

        // DELETE FROM Employee e WHERE e.department = :dept
        IQuery namedQuery = namedQuery("Employee", "employee.delete.dept");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType(":dept");

        assertNotNull(
            "The type of :dept should have been found",
            type
        );

        assertEquals(
            "The wrong type for :dept was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ParameterType_14() throws Exception {

        // SELECT e FROM Employee e WHERE e.department = :dept AND e.salary > :base
        IQuery namedQuery = namedQuery("Employee", "employee.deptBase");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType(":dept");

        assertNotNull(
            "The type of :dept should have been found",
            type
        );

        assertEquals(
            "The wrong type for :dept was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ParameterType_15() throws Exception {

        // SELECT e FROM Employee e WHERE e.name IN :type
        IQuery namedQuery = namedQuery("Employee", "employee.in");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType(":type");

        assertNotNull(
            "The type of :type should have been found",
            type
        );

        assertEquals(
            "The wrong type for :type was retrieved",
            getType(namedQuery, Collection.class),
            type
        );
    }

    @Test
    public final void test_ParameterType_16() throws Exception {

        // SELECT e FROM Employee e WHERE e.name = ?1 ORDER BY e.name
        IQuery namedQuery = namedQuery("Employee", "employee.?1");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType("?1");

        assertNotNull(
            "The type of ?1 should have been found",
            type
        );

        assertEquals(
            "The wrong type for ?1 was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ParameterType_17() throws Exception {

        // SELECT e FROM Employee e WHERE e.salary = (SELECT MAX(e.salary) FROM Employee a WHERE a.department = :dept)
        IQuery namedQuery = namedQuery("Employee", "employee.dept");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType(":dept");

        assertNotNull(
            "The type of :dept should have been found",
            type
        );

        assertEquals(
            "The wrong type for :dept was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ParameterType_18() throws Exception {

        // UPDATE Employee e SET e.manager = ?1 WHERE e.department = ?2
        IQuery namedQuery = namedQuery("Employee", "employee.update.positional");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType("?1");

        assertNotNull(
            "The type of ?1 should have been found",
            type
        );

        assertEquals(
            "The wrong type for ?1 was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ParameterType_19() throws Exception {

        // UPDATE Employee e SET e.manager = ?1 WHERE e.department = ?2
        IQuery namedQuery = namedQuery("Employee", "employee.update.positional");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType("?2");

        assertNotNull(
            "The type of ?2 should have been found",
            type
        );

        assertEquals(
            "The wrong type for ?2 was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ParameterType_20() throws Exception {

        String jpqlQuery = "SELECT c From Customer c where c.home.city IN(:city)";
        IQuery namedQuery = new JavaQuery(getPersistenceUnit(), jpqlQuery);

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType(":city");

        assertNotNull("The type of :city should have been found", type);

        assertEquals(
            "The wrong type for :city was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ParameterType_21() throws Exception {

        // select object(o) FROM Order o Where SQRT(o.totalPrice) > :doubleValue
        IQuery namedQuery = namedQuery("Order", "order.doubleValue");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType(":doubleValue");

        assertNotNull(
            "The type of :dept should have been found",
            type
        );

        assertEquals(
            "The wrong type for :dept was retrieved",
            getType(namedQuery, Double.class),
            type
        );
    }

    @Test
    public final void test_ParameterType_22() throws Exception {

        // SELECT DISTINCT p From Product p where p.shelfLife.soldDate NOT BETWEEN :date1 AND :newdate
        IQuery namedQuery = namedQuery("Product", "product.date");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType(":date1");

        assertNotNull(
            "The type of :date1 should have been found",
            type
        );

        assertEquals(
            "The wrong type for :date1 was retrieved",
            getType(namedQuery, Date.class),
            type
        );
    }

    @Test
    public final void test_ParameterType_23() throws Exception {

        // Select Distinct Object(p) from Product p where (p.quantity > (500 + :int1)) AND (p.partNumber IS NULL))
        IQuery namedQuery = namedQuery("Product", "product.int1");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType(":int1");

        assertNotNull(
            "The type of :dept should have been found",
            type
        );

        assertEquals(
            "The wrong type for :dept was retrieved",
            getType(namedQuery, Integer.class),
            type
        );
    }

    @Test
    public final void test_ParameterType_24() throws Exception {

        // SELECT DISTINCT p From Product p where p.shelfLife.soldDate NOT BETWEEN :date1 AND :newdate
        IQuery namedQuery = namedQuery("Product", "product.date");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType(":newdate");

        assertNotNull(
            "The type of :newdate should have been found",
            type
        );

        assertEquals(
            "The wrong type for :newdate was retrieved",
            getType(namedQuery, Date.class),
            type
        );
    }

    @Test
    public final void test_ParameterType_25() throws Exception {

        // SELECT e FROM Employee e WHERE EXISTS (SELECT p FROM Project p JOIN p.employees emp WHERE emp = e AND p.name = :name)
        IQuery namedQuery = namedQuery("Employee", "employee.subquery1");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType(":name");

        assertNotNull(
            "The type of :name should have been found",
            type
        );

        assertEquals(
            "The wrong type for :name was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ParameterType_26() throws Exception {

        // SELECT e FROM Employee e WHERE 2 < ALL(SELECT a FROM Address a WHERE 2 < ANY(SELECT d FROM Dept WHERE a.city = ?1)
        IQuery namedQuery = namedQuery("Address", "address.subquery.subquery");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getParameterType("?1");

        assertNotNull(
            "The type of ?1 should have been found",
            type
        );

        assertEquals(
            "The wrong type for ?1 was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ResultType_01() throws Exception {

        // SELECT ABS(p.quantity) FROM Product p
        IQuery namedQuery = namedQuery("Product", "product.abs");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of ABS(p.quantity) should have been found",
            type
        );

        assertEquals(
            "The wrong type for ABS(p.quantity) was retrieved",
            getType(namedQuery, Integer.class),
            type
        );
    }

    @Test
    public final void test_ResultType_02() throws Exception {

        // SELECT ABS(o.totalPrice) FROM Order o
        IQuery namedQuery = namedQuery("Order", "order.abs");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of ABS(o.totalPrice) should have been found",
            type
        );

        assertEquals(
            "The wrong type for ABS(o.totalPrice) was retrieved",
            getType(namedQuery, Double.class),
            type
        );
    }

    @Test
    public final void test_ResultType_03() throws Exception {

        // SELECT ABS(p.id) FROM Project p
        IQuery namedQuery = namedQuery("Project", "project.abs");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of ABS(p.id) should have been found",
            type
        );

        assertEquals(
            "The wrong type for ABS(p.id) was retrieved",
            getType(namedQuery, Float.class),
            type
        );
    }

    @Test
    public final void test_ResultType_04() throws Exception {

        // SELECT 2 + 2.2F FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.addition1");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of 2 + 2.2F should have been found",
            type
        );

        assertEquals(
            "The wrong type for 2 + 2.2F was retrieved",
            getType(namedQuery, Float.class),
            type
        );
    }

    @Test
    public final void test_ResultType_05() throws Exception {

        // SELECT AVG(e.salary) + 2E2 FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.addition2");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of AVG(salary) + 2E2 should have been found",
            type
        );

        assertEquals(
            "The wrong type for AVG(salary) + 2E2 was retrieved",
            getType(namedQuery, Double.class),
            type
        );
    }

    @Test
    public final void test_ResultType_06() throws Exception {

        // SELECT e.salary + 2 FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.addition3");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of e.salary + 2 should have been found",
            type
        );

        assertEquals(
            "The wrong type for e.salary + 2 was retrieved",
            getType(namedQuery, Long.class),
            type
        );
    }

    @Test
    public final void test_ResultType_07() throws Exception {

        // SELECT e.name + 2 FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.addition4");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of e.name + 2 should have been found",
            type
        );

        assertEquals(
            "The wrong type for e.name + 2 was retrieved",
            getType(namedQuery, Object.class),
            type
        );
    }

    @Test
    public final void test_ResultType_08() throws Exception {

        // SELECT AVG(p.quantity) FROM Product p
        IQuery namedQuery = namedQuery("Product", "product.quantity");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of AVG(p.quantity) should have been found",
            type
        );

        assertEquals(
            "The wrong type for AVG(p.quantity) was retrieved",
            getType(namedQuery, Double.class),
            type
        );
    }

    @Test
    public final void test_ResultType_09() throws Exception {

        // SELECT CASE WHEN e.name = 'Java Persistence Query Language' THEN 'Java Persistence Query Language'
        //             WHEN 1 + 2 THEN SUBSTRING(e.name, 0, 2)
        //             ELSE e.name
        //        END
        // FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.case1");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of CASE(...) should have been found",
            type
        );

        assertEquals(
            "The wrong type for CASE(...) was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ResultType_10() throws Exception {

        // SELECT CASE WHEN e.name = 'JPQL' THEN e.working
        //             WHEN 1 + 2 THEN TRUE
        //             ELSE p.completed
        //        END
        // FROM Employee e, Project p
        IQuery namedQuery = namedQuery("Employee", "employee.case2");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of CASE(...) should have been found",
            type
        );

        assertEquals(
            "The wrong type for CASE(...) was retrieved",
            getType(namedQuery, Boolean.class),
            type
        );
    }

    @Test
    public final void test_ResultType_11() throws Exception {

        // SELECT CASE WHEN e.name = 'JPQL' THEN e.working
        //             WHEN 1 + 2 THEN SUBSTRING(e.name, 0, 2)
        //             ELSE e.dept
        //        END
        // FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.case3");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of CASE(...) should have been found",
            type
        );

        assertEquals(
            "The wrong type for CASE(...) was retrieved",
            getType(namedQuery, Object.class),
            type
        );
    }

    @Test
    public final void test_ResultType_12() throws Exception {

        // SELECT e FROM Employee E
        IQuery namedQuery = namedQuery("Employee", "employee.caseInsensitive");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of e should have been found",
            type
        );

        assertEquals(
            "The wrong type for e was retrieved",
            mappedType(namedQuery, "Employee"),
            type
        );
    }

    @Test
    public final void test_ResultType_13() throws Exception {

        // SELECT COALESCE(o.price, o.price) FROM Order o
        IQuery namedQuery = namedQuery("Order", "order.coalesce1");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of COALESCE(o.price, o.price) should have been found",
            type
        );

        assertEquals(
            "The wrong type for COALESCE(o.price, o.price) was retrieved",
            getType(namedQuery, BigInteger.class),
            type
        );
    }

    @Test
    public final void test_ResultType_14() throws Exception {

        // SELECT COALESCE(o.totalPrice, SQRT(o.realPrice)) FROM Order o
        IQuery namedQuery = namedQuery("Order", "order.coalesce2");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of COALESCE(o.totalPrice, SQRT(o.realPrice)) should have been found",
            type
        );

        assertEquals(
            "The wrong type for COALESCE(o.totalPrice, SQRT(o.realPrice)) was retrieved",
            getType(namedQuery, Double.class),
            type
        );
    }

    @Test
    public final void test_ResultType_15() throws Exception {

        // SELECT COALESCE(o.number, e.name) FROM Order o, Employee e
        IQuery namedQuery = namedQuery("Order", "order.coalesce3");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of COALESCE(o.number, e.name) should have been found",
            type
        );

        assertEquals(
            "The wrong type for COALESCE(o.number, e.name) was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ResultType_16() throws Exception {

        // SELECT COALESCE(o.price, o.number) FROM Order o
        IQuery namedQuery = namedQuery("Order", "order.coalesce4");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of COALESCE(o.price, o.number) should have been found",
            type
        );

        assertEquals(
            "The wrong type for COALESCE(o.price, o.number) was retrieved",
            getType(namedQuery, Object.class),
            type
        );
    }

    @Test
    public final void test_ResultType_17() throws Exception {

        // SELECT e.name, d.dname FROM Employee e, Dept d
        IQuery namedQuery = namedQuery("Employee", "employee.collection");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of (e.name, d.dname) should have been found",
            type
        );

        assertEquals(
            "The wrong type for (e.name, d.dname) was retrieved",
            getType(namedQuery, Object[].class),
            type
        );
    }

    @Test
    public final void test_ResultType_18() throws Exception {

        // SELECT c FROM Address a JOIN a.customerList c
        IQuery namedQuery = namedQuery("Address", "address.collection");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of c should have been found",
            type
        );

        assertEquals(
            "The wrong type for c was retrieved",
            mappedType(namedQuery, "Customer"),
            type
        );
    }

    @Test
    public final void test_ResultType_19() throws Exception {

        // SELECT CONCAT(a.street, a.city) FROM Address a
        IQuery namedQuery = namedQuery("Address", "address.concat");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of CONCAT(a.street, a.city) should have been found",
            type
        );

        assertEquals(
            "The wrong type for CONCAT(a.street, a.city) was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ResultType_20() throws Exception {

        // select count(c) FROM Customer c JOIN c.aliases a GROUP BY a.alias HAVING a.alias = SUBSTRING(:string1, :int1, :int2)
        IQuery namedQuery = namedQuery("Customer", "customer.substring");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of count(c) should have been found",
            type
        );

        assertEquals(
            "The wrong type for count(c) was retrieved",
            getType(namedQuery, Long.class),
            type
        );
    }

    @Test
    public final void test_ResultType_21() throws Exception {

        // select count(c) FROM Customer c JOIN c.aliases a GROUP BY a.alias HAVING a.alias = SUBSTRING(:string1, :int1, :int2)
        IQuery namedQuery = namedQuery("Customer", "customer.substring");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of count(c) should have been found",
            type
        );

        assertEquals(
            "The wrong type for count(c) was retrieved",
            getType(namedQuery, Long.class),
            type
        );
    }

    @Test
    public final void test_ResultType_22() throws Exception {

        // SELECT CURRENT_DATE FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.date1");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of CURRENT_DATE should have been found",
            type
        );

        assertEquals(
            "The wrong type for CURRENT_DATE was retrieved",
            getType(namedQuery, java.sql.Date.class),
            type
        );
    }

    @Test
    public final void test_ResultType_23() throws Exception {

        // SELECT {d '2008-12-31'} FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.date2");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of {d '2008-12-31'} should have been found",
            type
        );

        assertEquals(
            "The wrong type for {d '2008-12-31'} was retrieved",
            getType(namedQuery, java.sql.Date.class),
            type
        );
    }

    @Test
    public final void test_ResultType_24() throws Exception {

        // DELETE FROM Employee e WHERE e.department = :dept
        IQuery namedQuery = namedQuery("Employee", "employee.delete.dept");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The object type should have been returned",
            type
        );

        assertEquals(
            "The wrong type was retrieved",
            getType(namedQuery, Object.class),
            type
        );
    }

    @Test
    public final void test_ResultType_25() throws Exception {

        // SELECT 2 / 2.2F FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.division1");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of 2 / 2.2F should have been found",
            type
        );

        assertEquals(
            "The wrong type for 2 / 2.2F was retrieved",
            getType(namedQuery, Float.class),
            type
        );
    }

    @Test
    public final void test_ResultType_26() throws Exception {

        // SELECT AVG(e.salary) / 2E2 FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.division2");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of AVG(salary) / 2E2 should have been found",
            type
        );

        assertEquals(
            "The wrong type for AVG(salary) / 2E2 was retrieved",
            getType(namedQuery, Double.class),
            type
        );
    }

    @Test
    public final void test_ResultType_27() throws Exception {

        // SELECT e.salary / 2 FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.division3");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of e.salary / 2 should have been found",
            type
        );

        assertEquals(
            "The wrong type for e.salary / 2 was retrieved",
            getType(namedQuery, Long.class),
            type
        );
    }

    @Test
    public final void test_ResultType_28() throws Exception {

        // SELECT e.name / 2 FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.division4");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of e.name / 2 should have been found",
            type
        );

        assertEquals(
            "The wrong type for e.name / 2 was retrieved",
            getType(namedQuery, Object.class),
            type
        );
    }

    @Test
    public final void test_ResultType_29() throws Exception {

        // SELECT ENTRY(e) FROM Alias a JOIN a.ids e
        IQuery namedQuery = namedQuery("Alias", "alias.entry");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of ENTRY(e) should have been found",
            type
        );

        assertEquals(
            "The wrong type for ENTRY(e) was retrieved",
            getType(namedQuery, Map.Entry.class),
            type
        );
    }

    @Test
    public final void test_ResultType_30() throws Exception {

        // SELECT CASE WHEN e.name = 'Pascal' THEN jpql.query.EnumType.FIRST_NAME
        //             WHEN e.name = 'JPQL'   THEN jpql.query.EnumType.LAST_NAME
        //             ELSE jpql.query.EnumType.NAME
        //        END
        // FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.enum");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of the case should have been found",
            type
        );

        assertEquals(
            "The wrong type for 2 - 2.2F was retrieved",
            helper.getTypeRepository().getType("jpql.query.EnumType"),
            type
        );
    }

    @Test
    public final void test_ResultType_31() throws Exception {

        // SELECT INDEX(c) FROM Address a JOIN a.customerList c
        IQuery namedQuery = namedQuery("Address", "address.index");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of INDEX(e) should have been found",
            type
        );

        assertEquals(
            "The wrong type for INDEX(c) was retrieved",
            getType(namedQuery, Integer.class),
            type
        );
    }

    @Test
    public final void test_ResultType_32() throws Exception {

        // SELECT c.lastName FROM Address a JOIN a.customerList AS c
        IQuery namedQuery = namedQuery("Address", "address.stateField");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The object type should have been returned",
            type
        );

        assertEquals(
            "The wrong type was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ResultType_33() throws Exception {

        // SELECT KEY(k) FROM Alias a JOIN a.ids k
        IQuery namedQuery = namedQuery("Alias", "alias.key1");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of KEY(k) should have been found",
            type
        );

        assertEquals(
            "The wrong type for KEY(k) was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ResultType_34() throws Exception {

        // SELECT TRUE FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.true");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of TRUE should have been found",
            type
        );

        assertEquals(
            "The wrong type for TRUE was retrieved",
            getType(namedQuery, Boolean.class),
            type
        );
    }

    @Test
    public final void test_ResultType_35() throws Exception {

        // SELECT FALSE FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.false");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of FALSE should have been found",
            type
        );

        assertEquals(
            "The wrong type for FALSE was retrieved",
            getType(namedQuery, Boolean.class),
            type
        );
    }

    @Test
    public final void test_ResultType_36() throws Exception {

        // SELECT LENGTH(a.street) FROM Address a
        IQuery namedQuery = namedQuery("Address", "address.length");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of LENGTH(a.street) should have been found",
            type
        );

        assertEquals(
            "The wrong type for LENGTH(a.street) was retrieved",
            getType(namedQuery, Integer.class),
            type
        );
    }

    @Test
    public final void test_ResultType_37() throws Exception {

        // SELECT LOCATE(a.street, 'Arco Drive') FROM Address a
        IQuery namedQuery = namedQuery("Address", "address.locate");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of LOCATE(a.street, 'Arco Drive') should have been found",
            type
        );

        assertEquals(
            "The wrong type for LOCATE(a.street, 'Arco Drive') was retrieved",
            getType(namedQuery, Integer.class),
            type
        );
    }

    @Test
    public final void test_ResultType_38() throws Exception {

        // SELECT LOWER(e.name) FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.lower");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of LOWER(e.name) should have been found",
            type
        );

        assertEquals(
            "The wrong type for LOWER(e.name) was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ResultType_39() throws Exception {

        // SELECT MAX(p.quantity) FROM Product p
        IQuery namedQuery = namedQuery("Product", "product.max");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of MAX(p.quantity) should have been found",
            type
        );

        assertEquals(
            "The wrong type for MAX(p.quantity) was retrieved",
            getType(namedQuery, Integer.class),
            type
        );
    }

    @Test
    public final void test_ResultType_40() throws Exception {

        // SELECT MAX(e.salary) FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.max");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of MAX(e.salary) should have been found",
            type
        );

        assertEquals(
            "The wrong type for MAX(e.salary) was retrieved",
            getType(namedQuery, Long.class),
            type
        );
    }

    @Test
    public final void test_ResultType_41() throws Exception {

        // SELECT MIN(p.quantity) FROM Product p
        IQuery namedQuery = namedQuery("Product", "product.min");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of MIN(p.quantity) should have been found",
            type
        );

        assertEquals(
            "The wrong type for MIN(p.quantity) was retrieved",
            getType(namedQuery, Integer.class),
            type
        );
    }

    @Test
    public final void test_ResultType_42() throws Exception {

        // SELECT MIN(e.salary) FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.min");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of MIN(e.salary) should have been found",
            type
        );

        assertEquals(
            "The wrong type for MIN(e.salary) was retrieved",
            getType(namedQuery, Long.class),
            type
        );
    }

    @Test
    public final void test_ResultType_43() throws Exception {

        // SELECT MOD(e.salary, e.empId) FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.mod");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of MOD(e.salary, e.empId) should have been found",
            type
        );

        assertEquals(
            "The wrong type for MOD(e.salary, e.empId) was retrieved",
            getType(namedQuery, Integer.class),
            type
        );
    }

    @Test
    public final void test_ResultType_44() throws Exception {

        // SELECT 2 * 2.2F FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.multiplication1");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of 2 * 2.2F should have been found",
            type
        );

        assertEquals(
            "The wrong type for 2 * 2.2F was retrieved",
            getType(namedQuery, Float.class),
            type
        );
    }

    @Test
    public final void test_ResultType_45() throws Exception {

        // SELECT AVG(e.salary) * 2E2 FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.multiplication2");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of AVG(salary) * 2E2 should have been found",
            type
        );

        assertEquals(
            "The wrong type for AVG(salary) * 2E2 was retrieved",
            getType(namedQuery, Double.class),
            type
        );
    }

    @Test
    public final void test_ResultType_46() throws Exception {

        // SELECT e.salary * 2 FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.multiplication3");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of e.salary * 2 should have been found",
            type
        );

        assertEquals(
            "The wrong type for e.salary * 2 was retrieved",
            getType(namedQuery, Long.class),
            type
        );
    }

    @Test
    public final void test_ResultType_47() throws Exception {

        // SELECT e.name * 2 FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.multiplication4");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of e.name * 2 should have been found",
            type
        );

        assertEquals(
            "The wrong type for e.name * 2 was retrieved",
            getType(namedQuery, Object.class),
            type
        );
    }

    @Test
    public final void test_ResultType_48() throws Exception {

        // SELECT NEW java.lang.Vector(d.empList) FROM Dept d
        IQuery namedQuery = namedQuery("Dept", "dept.new1");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of NEW java.lang.Vector(d.empList) should have been found",
            type
        );

        assertEquals(
            "The wrong type for NEW java.lang.Vector(d.empList) was retrieved",
            getType(namedQuery, Vector.class),
            type
        );
    }

    @Test
    public final void test_ResultType_49() throws Exception {

        // SELECT NULLIF(e.name, 'JPQL') FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.nullif1");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of NULLIF(e.name, 'JPQL') should have been found",
            type
        );

        assertEquals(
            "The wrong type for NULLIF(e.name, 'JPQL') was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ResultType_50() throws Exception {

        // SELECT NULLIF(2 + 2, 'JPQL') FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.nullif2");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of NULLIF(2 + 2, 'JPQL') should have been found",
            type
        );

        assertEquals(
            "The wrong type for NULLIF(2 + 2, 'JPQL') was retrieved",
            getType(namedQuery, Integer.class),
            type
        );
    }

    @Test
    public final void test_ResultType_51() throws Exception {

        // SELECT Distinct Object(c) From Customer c, IN(c.home.phones) p where p.area LIKE :area
        IQuery namedQuery = namedQuery("Customer", "customer.area");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of Object(c) should have been found",
            type
        );

        assertEquals(
            "The wrong type for Object(c) was retrieved",
            getType(namedQuery, "jpql.query.Customer"),
            type
        );
    }

    @Test
    public final void test_ResultType_52() throws Exception {

        // select object(o) FROM Order o Where SQRT(o.totalPrice) > :doubleValue
        IQuery namedQuery = namedQuery("Order", "order.doubleValue");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of object(o) should have been found",
            type
        );

        assertEquals(
            "The wrong type for object(o) was retrieved",
            getType(namedQuery, "jpql.query.Order"),
            type
        );
    }

    @Test
    public final void test_ResultType_53() throws Exception {

        // Select Distinct Object(p) from Product p where (p.quantity > (500 + :int1)) AND (p.partNumber IS NULL))
        IQuery namedQuery = namedQuery("Product", "product.int1");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of Object(p) should have been found",
            type
        );

        assertEquals(
            "The wrong type for Object(p) was retrieved",
            getType(namedQuery, "jpql.query.Product"),
            type
        );
    }

    @Test
    public final void test_ResultType_54() throws Exception {

        // SELECT a.alias FROM Alias AS a WHERE (a.alias IS NULL AND :param1 IS NULL) OR a.alias = :param1
        IQuery namedQuery = namedQuery("Alias", "alias.param1");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of a.alias should have been found",
            type
        );

        assertEquals(
            "The wrong type for a.alias was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ResultType_55() throws Exception {

        // select c.firstName FROM Customer c Group By c.firstName HAVING c.firstName = concat(:fname, :lname)
        IQuery namedQuery = namedQuery("Customer", "customer.name");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of c.firstName should have been found",
            type
        );

        assertEquals(
            "The wrong type for c.firstName was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ResultType_56() throws Exception {

        // select Dept.floorNumber from Dept Dept
        IQuery namedQuery = namedQuery("Dept", "dept.floorNumber");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of Dept.floorNumber should have been found",
            type
        );

        assertEquals(
            "The wrong type for Dept.floorNumber was retrieved",
            getType(namedQuery, Integer.class),
            type
        );
    }

    @Test
    public final void test_ResultType_57() throws Exception {

        // SELECT c from Customer c where c.home.city IN :city
        IQuery namedQuery = namedQuery("Customer", "customer.city");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of c should have been found",
            type
        );

        assertEquals(
            "The wrong type for c was retrieved",
            getType(namedQuery, "jpql.query.Customer"),
            type
        );
    }

    @Test
    public final void test_ResultType_58() throws Exception {

        // SELECT DISTINCT p From Product p where p.shelfLife.soldDate NOT BETWEEN :date1 AND :newdate
        IQuery namedQuery = namedQuery("Product", "product.date");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of p should have been found",
            type
        );

        assertEquals(
            "The wrong type for p was retrieved",
            getType(namedQuery, "jpql.query.Product"),
            type
        );
    }

    @Test
    public final void test_ResultType_59() throws Exception {

        // SELECT e.name AS n From Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.resultVariable1");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of e.name as n should have been found",
            type
        );

        assertEquals(
            "The wrong type for e.name as n was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ResultType_60() throws Exception {

        // SELECT e.name n From Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.resultVariable2");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of e.name as n should have been found",
            type
        );

        assertEquals(
            "The wrong type for e.name as n was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ResultType_61() throws Exception {

        // SELECT e.salary / 1000D n From Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.resultVariable3");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of e.salary / 1000D n as n should have been found",
            type
        );

        assertEquals(
            "The wrong type for e.salary / 1000D n as n was retrieved",
            getType(namedQuery, Double.class),
            type
        );
    }

    @Test
    public final void test_ResultType_62() throws Exception {

        // SELECT SIZE(c) FROM Address a JOIN a.customerList c
        IQuery namedQuery = namedQuery("Address", "address.size");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of SIZE(c) should have been found",
            type
        );

        assertEquals(
            "The wrong type for SIZE(c) was retrieved",
            getType(namedQuery, Integer.class),
            type
        );
    }

    @Test
    public final void test_ResultType_63() throws Exception {

        // SELECT SQRT(o.totalPrice) FROM Order o
        IQuery namedQuery = namedQuery("Order", "order.sqrt");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of SQRT(o.totalPrice) should have been found",
            type
        );

        assertEquals(
            "The wrong type for SQRT(o.totalPrice) was retrieved",
            getType(namedQuery, Double.class),
            type
        );
    }

    @Test
    public final void test_ResultType_64() throws Exception {

        // SELECT 2 / 2.2F FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.substraction1");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of 2 - 2.2F should have been found",
            type
        );

        assertEquals(
            "The wrong type for 2 - 2.2F was retrieved",
            getType(namedQuery, Float.class),
            type
        );
    }

    @Test
    public final void test_ResultType_65() throws Exception {

        // SELECT AVG(e.salary) - 2E2 FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.substraction2");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of AVG(salary) / 2E2 should have been found",
            type
        );

        assertEquals(
            "The wrong type for AVG(salary) / 2E2 was retrieved",
            getType(namedQuery, Double.class),
            type
        );
    }

    @Test
    public final void test_ResultType_66() throws Exception {

        // SELECT e.name - 2 FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.substraction3");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of e.name - 2 should have been found",
            type
        );

        assertEquals(
            "The wrong type for e.name - 2 was retrieved",
            getType(namedQuery, Long.class),
            type
        );
    }

    @Test
    public final void test_ResultType_67() throws Exception {

        // SELECT e.salary - 2 FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.substraction4");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of e.salary - 2 should have been found",
            type
        );

        assertEquals(
            "The wrong type for e.salary - 2 was retrieved",
            getType(namedQuery, Object.class),
            type
        );
    }

    @Test
    public final void test_ResultType_68() throws Exception {

        // SELECT SUBSTRING(a.state, 0, 1) FROM Address a
        IQuery namedQuery = namedQuery("Address", "address.length");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of SUBSTRING(a.state, 0, 1) should have been found",
            type
        );

        assertEquals(
            "The wrong type for SUBSTRING(a.state, 0, 1) was retrieved",
            getType(namedQuery, Integer.class),
            type
        );
    }

    @Test
    public final void test_ResultType_69() throws Exception {

        // SELECT SUM(e.salary) FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.sum");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of SUM(e.salary) should have been found",
            type
        );

        assertEquals(
            "The wrong type for SUM(p.quantity) was retrieved",
            getType(namedQuery, Long.class),
            type
        );
    }

    @Test
    public final void test_ResultType_70() throws Exception {

        // SELECT SUM(o.totalPrice) FROM Order o
        IQuery namedQuery = namedQuery("Order", "order.sum1");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of SUM(o.totalPrice) should have been found",
            type
        );

        assertEquals(
            "The wrong type for SUM(o.totalPrice) was retrieved",
            getType(namedQuery, Double.class),
            type
        );
    }

    @Test
    public final void test_ResultType_71() throws Exception {

        // SELECT SUM(o.totalPrice) FROM Order o
        IQuery namedQuery = namedQuery("Order", "order.sum2");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of SUM(o.price) should have been found",
            type
        );

        assertEquals(
            "The wrong type for SUM(o.price) was retrieved",
            getType(namedQuery, BigInteger.class),
            type
        );
    }

    @Test
    public final void test_ResultType_72() throws Exception {

        // SELECT SUM(o.realPrice) FROM Order o
        IQuery namedQuery = namedQuery("Order", "order.sum3");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of SUM(o.realPrice) should have been found",
            type
        );

        assertEquals(
            "The wrong type for SUM(o.realPrice) was retrieved",
            getType(namedQuery, BigDecimal.class),
            type
        );
    }

    @Test
    public final void test_ResultType_73() throws Exception {

        // SELECT TRIM(e.name) FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.trim");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of TRIM(e.name) should have been found",
            type
        );

        assertEquals(
            "The wrong type for TRIM(e.name) was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ResultType_74() throws Exception {

        // UPDATE Employee e SET e.manager = ?1 WHERE e.department = ?2
        IQuery namedQuery = namedQuery("Employee", "employee.update.positional");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The Object type should have been returned",
            type
        );

        assertEquals(
            "The wrong type was retrieved",
            getType(namedQuery, Object.class),
            type
        );
    }

    @Test
    public final void test_ResultType_75() throws Exception {

        // SELECT UPPER(e.name) FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.upper");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of UPPER(e.name) should have been found",
            type
        );

        assertEquals(
            "The wrong type for UPPER(e.name) was retrieved",
            getType(namedQuery, String.class),
            type
        );
    }

    @Test
    public final void test_ResultType_76() throws Exception {

        // SELECT VALUE(v) FROM Alias a JOIN a.ids v
        IQuery namedQuery = namedQuery("Alias", "alias.value1");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of VALUE(v) should have been found",
            type
        );

        assertEquals(
            "The wrong type for VALUE(v) was retrieved",
            getType(namedQuery, Date.class),
            type
        );
    }

    @Test
    public final void test_ResultType_77() throws Exception {

        // SELECT v FROM Alias a JOIN a.ids v
        IQuery namedQuery = namedQuery("Alias", "alias.value2");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of v should have been found",
            type
        );

        assertEquals(
            "The wrong type for v was retrieved",
            getType(namedQuery, Date.class),
            type
        );
    }

    protected ITypeRepository typeRepository(IQuery query) {
        return query.getProvider().getTypeRepository();
    }
}
