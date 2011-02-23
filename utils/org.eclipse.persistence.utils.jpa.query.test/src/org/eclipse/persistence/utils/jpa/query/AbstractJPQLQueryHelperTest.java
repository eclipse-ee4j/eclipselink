/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query;

import com.titan.domain.EnumType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Vector;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedType;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedTypeProvider;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;
import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeRepository;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * The unit-tests for {@link AbstractJPQLQueryHelper#getParameterType(String)} and
 * {@link AbstractJPQLQueryHelper#getResultType()}.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractJPQLQueryHelperTest<T> extends AbstractJPQLQueryTest {

	/**
	 * Creates the concrete class of {@link AbstractJPQLQueryHelper}.
	 *
	 * @param query The external form of the query to use for testing the calculation of the result
	 * type or the type of an input parameter
	 * @return The concrete class of {@link AbstractJPQLQueryHelper} to test
	 * @throws Exception If an error occurred during the instantiation
	 */
	protected abstract AbstractJPQLQueryHelper<T> buildQueryHelper(IQuery query) throws Exception;

	protected IManagedType entity(IManagedTypeProvider provider, String entityName) throws Exception {
		IManagedType entity = provider.getManagedType(entityName);
		assertNotNull("The entity " + entityName + " could not be found", entity);
		return entity;
	}

	protected IType mappedType(IQuery query, String entityName) {
		return query.getProvider().getManagedType(entityName).getType();
	}

	protected abstract IQuery namedQuery(String entityName, String queryName) throws Exception;

	protected IManagedTypeProvider provider(IQuery query) {
		return query.getProvider();
	}

	@Test
	public void test_ParameterType_Alias_param1_String() throws Exception {

		// SELECT a.alias FROM Alias AS a WHERE (a.alias IS NULL AND :param1 IS NULL) OR a.alias = :param1
		IQuery namedQuery = namedQuery("Alias", "alias.param1");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":param1");

		assertNotNull(
			"The type of :param1 should have been found",
			type
		);

		assertEquals(
			"The wrong type for :param1 was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Customer_area_String() throws Exception {

		// SELECT Distinct Object(c) From Customer c, IN(c.home.phones) p where p.area LIKE :area
		IQuery namedQuery = namedQuery("Customer", "customer.area");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":area");

		assertNotNull(
			"The type of :area should have been found",
			type
		);

		assertEquals(
			"The wrong type for :area was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Customer_fname_String() throws Exception {

		// select c.firstName FROM Customer c Group By c.firstName HAVING c.firstName = concat(:fname, :lname)
		IQuery namedQuery = namedQuery("Customer", "customer.name");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":fname");

		assertNotNull(
			"The type of :fname should have been found",
			type
		);

		assertEquals(
			"The wrong type for :fname was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Customer_int1_String() throws Exception {

		// select count(c) FROM Customer c JOIN c.aliases a GROUP BY a.alias HAVING a.alias = SUBSTRING(:string1, :int1, :int2)
		IQuery namedQuery = namedQuery("Customer", "customer.substring");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":int1");

		assertNotNull(
			"The type of :int1 should have been found",
			type
		);

		assertEquals(
			"The wrong type for :int1 was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Customer_int2_String() throws Exception {

		// select count(c) FROM Customer c JOIN c.aliases a GROUP BY a.alias HAVING a.alias = SUBSTRING(:string1, :int1, :int2)
		IQuery namedQuery = namedQuery("Customer", "customer.substring");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":int2");

		assertNotNull(
			"The type of :int2 should have been found",
			type
		);

		assertEquals(
			"The wrong type for :int2 was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Customer_lname_String() throws Exception {

		// select c.firstName FROM Customer c Group By c.firstName HAVING c.firstName = concat(:fname, :lname)
		IQuery namedQuery = namedQuery("Customer", "customer.name");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":lname");

		assertNotNull(
			"The type of :lname should have been found",
			type
		);

		assertEquals(
			"The wrong type for :lname was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Customer_string1_String() throws Exception {

		// select count(c) FROM Customer c JOIN c.aliases a GROUP BY a.alias HAVING a.alias = SUBSTRING(:string1, :int1, :int2)
		IQuery namedQuery = namedQuery("Customer", "customer.substring");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":string1");

		assertNotNull(
			"The type of :string1 should have been found",
			type
		);

		assertEquals(
			"The wrong type for :string1 was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Dept_dname1_String() throws Exception {

		// select o from Dept o where o.dname in (:dname1, :dname2, :dname3)
		IQuery namedQuery = namedQuery("Dept", "dept.dname");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":dname1");

		assertNotNull(
			"The type of :dname1 should have been found",
			type
		);

		assertEquals(
			"The wrong type for :newdate was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Dept_dname2_String() throws Exception {

		// select o from Dept o where o.dname in (:dname1, :dname2, :dname3)
		IQuery namedQuery = namedQuery("Dept", "dept.dname");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":dname2");

		assertNotNull(
			"The type of :dname2 should have been found",
			type
		);

		assertEquals(
			"The wrong type for :newdate was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Dept_dname3_String() throws Exception {

		// select o from Dept o where o.dname in (:dname1, :dname2, :dname3)
		IQuery namedQuery = namedQuery("Dept", "dept.dname");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":dname3");

		assertNotNull(
			"The type of :dname3 should have been found",
			type
		);

		assertEquals(
			"The wrong type for :newdate was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Employee_Base_Long() throws Exception {

		// SELECT e FROM Employee e WHERE e.department = :dept AND e.salary > :base
		IQuery namedQuery = namedQuery("Employee", "employee.deptBase");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":base");

		assertNotNull(
			"The type of :base should have been found",
			type
		);

		assertEquals(
			"The wrong type for :base was retrieved",
			typeFor(namedQuery, Long.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Employee_Delete_dept() throws Exception {

		// DELETE FROM Employee e WHERE e.department = :dept
		IQuery namedQuery = namedQuery("Employee", "employee.delete.dept");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":dept");

		assertNotNull(
			"The type of :dept should have been found",
			type
		);

		assertEquals(
			"The wrong type for :dept was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Employee_Dept_String() throws Exception {

		// SELECT e FROM Employee e WHERE e.department = :dept AND e.salary > :base
		IQuery namedQuery = namedQuery("Employee", "employee.deptBase");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":dept");

		assertNotNull(
			"The type of :dept should have been found",
			type
		);

		assertEquals(
			"The wrong type for :dept was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Employee_In_Collection() throws Exception {

		// SELECT e FROM Employee e WHERE e.name IN :type
		IQuery namedQuery = namedQuery("Employee", "employee.in");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":type");

		assertNotNull(
			"The type of :type should have been found",
			type
		);

		assertEquals(
			"The wrong type for :type was retrieved",
			typeFor(namedQuery, Collection.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Employee_positional1_String() throws Exception {

		// SELECT e FROM Employee e WHERE e.name = ?1 ORDER BY e.name
		IQuery namedQuery = namedQuery("Employee", "employee.?1");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType("?1");

		assertNotNull(
			"The type of ?1 should have been found",
			type
		);

		assertEquals(
			"The wrong type for ?1 was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Employee_SubQuery_Dept_String() throws Exception {

		// SELECT e FROM Employee e WHERE e.salary = (SELECT MAX(e.salary) FROM Employee e WHERE e.department = :dept)
		IQuery namedQuery = namedQuery("Employee", "employee.dept");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":dept");

		assertNotNull(
			"The type of :dept should have been found",
			type
		);

		assertEquals(
			"The wrong type for :dept was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Employee_Update_positional_1() throws Exception {

		// UPDATE Employee e SET e.manager = ?1 WHERE e.department = ?2
		IQuery namedQuery = namedQuery("Employee", "employee.update.positional");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType("?1");

		assertNotNull(
			"The type of ?1 should have been found",
			type
		);

		assertEquals(
			"The wrong type for ?1 was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Employee_Update_positional_2() throws Exception {

		// UPDATE Employee e SET e.manager = ?1 WHERE e.department = ?2
		IQuery namedQuery = namedQuery("Employee", "employee.update.positional");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType("?2");

		assertNotNull(
			"The type of ?2 should have been found",
			type
		);

		assertEquals(
			"The wrong type for ?2 was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Order_doubleValue_Double() throws Exception {

		// select object(o) FROM Order o Where SQRT(o.totalPrice) > :doubleValue
		IQuery namedQuery = namedQuery("Order", "order.doubleValue");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":doubleValue");

		assertNotNull(
			"The type of :dept should have been found",
			type
		);

		assertEquals(
			"The wrong type for :dept was retrieved",
			typeFor(namedQuery, Double.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Product_date1_Date() throws Exception {

		// SELECT DISTINCT p From Product p where p.shelfLife.soldDate NOT BETWEEN :date1 AND :newdate
		IQuery namedQuery = namedQuery("Product", "product.date");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":date1");

		assertNotNull(
			"The type of :date1 should have been found",
			type
		);

		assertEquals(
			"The wrong type for :date1 was retrieved",
			typeFor(namedQuery, Date.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Product_int1_int() throws Exception {

		// Select Distinct Object(p) from Product p where (p.quantity > (500 + :int1)) AND (p.partNumber IS NULL))
		IQuery namedQuery = namedQuery("Product", "product.int1");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":int1");

		assertNotNull(
			"The type of :dept should have been found",
			type
		);

		assertEquals(
			"The wrong type for :dept was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Product_newDate_Date() throws Exception {

		// SELECT DISTINCT p From Product p where p.shelfLife.soldDate NOT BETWEEN :date1 AND :newdate
		IQuery namedQuery = namedQuery("Product", "product.date");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":newdate");

		assertNotNull(
			"The type of :newdate should have been found",
			type
		);

		assertEquals(
			"The wrong type for :newdate was retrieved",
			typeFor(namedQuery, Date.class),
			type
		);
	}

	@Test
	public void test_ParameterType_SubQuery_String() throws Exception {

		// SELECT e FROM Employee e WHERE EXISTS (SELECT p FROM Project p JOIN p.employees emp WHERE emp = e AND p.name = :name)
		IQuery namedQuery = namedQuery("Employee", "employee.subquery1");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":name");

		assertNotNull(
			"The type of :name should have been found",
			type
		);

		assertEquals(
			"The wrong type for :name was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_Abs_1() throws Exception {

		// SELECT ABS(p.quantity) FROM Product p
		IQuery namedQuery = namedQuery("Product", "product.abs");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of ABS(p.quantity) should have been found",
			type
		);

		assertEquals(
			"The wrong type for ABS(p.quantity) was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ResultType_Abs_2() throws Exception {

		// SELECT ABS(o.totalPrice) FROM Order o
		IQuery namedQuery = namedQuery("Order", "order.abs");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of ABS(o.totalPrice) should have been found",
			type
		);

		assertEquals(
			"The wrong type for ABS(o.totalPrice) was retrieved",
			typeFor(namedQuery, Double.class),
			type
		);
	}

	@Test
	public void test_ResultType_Abs_3() throws Exception {

		// SELECT ABS(p.id) FROM Project p
		IQuery namedQuery = namedQuery("Project", "project.abs");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of ABS(p.id) should have been found",
			type
		);

		assertEquals(
			"The wrong type for ABS(p.id) was retrieved",
			typeFor(namedQuery, Float.class),
			type
		);
	}

	@Test
	public void test_ResultType_Addition_1() throws Exception {

		// SELECT 2 + 2.2F FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.addition1");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of 2 + 2.2F should have been found",
			type
		);

		assertEquals(
			"The wrong type for 2 + 2.2F was retrieved",
			typeFor(namedQuery, Float.class),
			type
		);
	}

	@Test
	public void test_ResultType_Addition_2() throws Exception {

		// SELECT AVG(e.salary) + 2E2 FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.addition2");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of AVG(salary) + 2E2 should have been found",
			type
		);

		assertEquals(
			"The wrong type for AVG(salary) + 2E2 was retrieved",
			typeFor(namedQuery, Double.class),
			type
		);
	}

	@Test
	public void test_ResultType_Addition_3() throws Exception {

		// SELECT e.salary + 2 FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.addition3");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of e.salary + 2 should have been found",
			type
		);

		assertEquals(
			"The wrong type for e.salary + 2 was retrieved",
			typeFor(namedQuery, Long.class),
			type
		);
	}

	@Test
	public void test_ResultType_Addition_4() throws Exception {

		// SELECT e.name + 2 FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.addition4");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of e.name + 2 should have been found",
			type
		);

		assertEquals(
			"The wrong type for e.name + 2 was retrieved",
			typeFor(namedQuery, Object.class),
			type
		);
	}

	@Test
	public void test_ResultType_Avg_1() throws Exception {

		// SELECT AVG(p.quantity) FROM Product p
		IQuery namedQuery = namedQuery("Product", "product.quantity");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of AVG(p.quantity) should have been found",
			type
		);

		assertEquals(
			"The wrong type for AVG(p.quantity) was retrieved",
			typeFor(namedQuery, Double.class),
			type
		);
	}

//	@Test
//	public void test_ResultType_Bad_1() throws Exception {
//		// SELECT FROM Home h
//		IQuery namedQuery = namedQuery("Home", "home.bad1");
//
//		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
//		IType type = helper.getResultType();
//
//		assertNotNull(
//			"The type of (nothing) should have been found",
//			type
//		);
//
//		assertEquals(
//			"The wrong type for (nothing) was retrieved",
//			typeFor(namedQuery, Object.class),
//			type
//		);
//	}

//	@Test
//	public void test_ResultType_Bad_2() throws Exception {
//		// SELEC
//		IQuery namedQuery = namedQuery("Home", "home.bad2");
//
//		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
//		IType type = helper.getResultType();
//
//		assertNotNull(
//			"The type of (nothing) should have been found",
//			type
//		);
//
//		assertEquals(
//			"The wrong type for (nothing) was retrieved",
//			typeFor(namedQuery, Object.class),
//			type
//		);
//	}

	@Test
	public void test_ResultType_Case_1() throws Exception {

		// SELECT CASE WHEN e.name = 'Java Persistence Query Language' THEN 'Java Persistence Query Language'
		//             WHEN 1 + 2 THEN SUBSTRING(e.name, 0, 2)
		//             ELSE e.name
		//        END
		// FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.case1");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of CASE(...) should have been found",
			type
		);

		assertEquals(
			"The wrong type for CASE(...) was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_Case_2() throws Exception {

		// SELECT CASE WHEN e.name = 'JPQL' THEN e.working
		//             WHEN 1 + 2 THEN TRUE
		//             ELSE p.completed
		//        END
		// FROM Employee e, Project p
		IQuery namedQuery = namedQuery("Employee", "employee.case2");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of CASE(...) should have been found",
			type
		);

		assertEquals(
			"The wrong type for CASE(...) was retrieved",
			typeFor(namedQuery, Boolean.class),
			type
		);
	}

	@Test
	public void test_ResultType_Case_3() throws Exception {

		// SELECT CASE WHEN e.name = 'JPQL' THEN e.working
		//             WHEN 1 + 2 THEN SUBSTRING(e.name, 0, 2)
		//             ELSE e.dept
		//        END
		// FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.case3");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of CASE(...) should have been found",
			type
		);

		assertEquals(
			"The wrong type for CASE(...) was retrieved",
			typeFor(namedQuery, Object.class),
			type
		);
	}

	@Test
	public void test_ResultType_CaseInsensitive() throws Exception {

		// SELECT e FROM Employee E
		IQuery namedQuery = namedQuery("Employee", "employee.caseInsensitive");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
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
	public void test_ResultType_Coalesce_1() throws Exception {

		// SELECT COALESCE(o.price, o.price) FROM Order o
		IQuery namedQuery = namedQuery("Order", "order.coalesce1");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of COALESCE(o.price, o.price) should have been found",
			type
		);

		assertEquals(
			"The wrong type for COALESCE(o.price, o.price) was retrieved",
			typeFor(namedQuery, BigInteger.class),
			type
		);
	}

	@Test
	public void test_ResultType_Coalesce_2() throws Exception {

		// SELECT COALESCE(o.totalPrice, SQRT(o.realPrice)) FROM Order o
		IQuery namedQuery = namedQuery("Order", "order.coalesce2");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of COALESCE(o.totalPrice, SQRT(o.realPrice)) should have been found",
			type
		);

		assertEquals(
			"The wrong type for COALESCE(o.totalPrice, SQRT(o.realPrice)) was retrieved",
			typeFor(namedQuery, Double.class),
			type
		);
	}

	@Test
	public void test_ResultType_Coalesce_3() throws Exception {

		// SELECT COALESCE(o.number, e.name) FROM Order o, Employee e
		IQuery namedQuery = namedQuery("Order", "order.coalesce3");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of COALESCE(o.number, e.name) should have been found",
			type
		);

		assertEquals(
			"The wrong type for COALESCE(o.number, e.name) was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_Coalesce_4() throws Exception {

		// SELECT COALESCE(o.price, o.number) FROM Order o
		IQuery namedQuery = namedQuery("Order", "order.coalesce4");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of COALESCE(o.price, o.number) should have been found",
			type
		);

		assertEquals(
			"The wrong type for COALESCE(o.price, o.number) was retrieved",
			typeFor(namedQuery, Object.class),
			type
		);
	}

	@Test
	public void test_ResultType_Collection_1() throws Exception {

		// SELECT e.name, d.dname FROM Employee e, Dept d
		IQuery namedQuery = namedQuery("Employee", "employee.collection");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of (e.name, d.dname) should have been found",
			type
		);

		assertEquals(
			"The wrong type for (e.name, d.dname) was retrieved",
			typeFor(namedQuery, Object[].class),
			type
		);
	}

	@Test
	public void test_ResultType_CollectionType_1() throws Exception {

		// SELECT c FROM Address a JOIN a.customerList c
		IQuery namedQuery = namedQuery("Address", "address.collection");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
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
	public void test_ResultType_Concat_1() throws Exception {

		// SELECT CONCAT(a.street, a.city) FROM Address a
		IQuery namedQuery = namedQuery("Address", "address.concat");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of CONCAT(a.street, a.city) should have been found",
			type
		);

		assertEquals(
			"The wrong type for CONCAT(a.street, a.city) was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_Count_1() throws Exception {

		// select count(c) FROM Customer c JOIN c.aliases a GROUP BY a.alias HAVING a.alias = SUBSTRING(:string1, :int1, :int2)
		IQuery namedQuery = namedQuery("Customer", "customer.substring");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of count(c) should have been found",
			type
		);

		assertEquals(
			"The wrong type for count(c) was retrieved",
			typeFor(namedQuery, Long.class),
			type
		);
	}

	@Test
	public void test_ResultType_Count_2() throws Exception {

		// select count(c) FROM Customer c JOIN c.aliases a GROUP BY a.alias HAVING a.alias = SUBSTRING(:string1, :int1, :int2)
		IQuery namedQuery = namedQuery("Customer", "customer.substring");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of count(c) should have been found",
			type
		);

		assertEquals(
			"The wrong type for count(c) was retrieved",
			typeFor(namedQuery, Long.class),
			type
		);
	}

	@Test
	public void test_ResultType_Date_1() throws Exception {

		// SELECT CURRENT_DATE FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.date1");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of CURRENT_DATE should have been found",
			type
		);

		assertEquals(
			"The wrong type for CURRENT_DATE was retrieved",
			typeFor(namedQuery, java.sql.Date.class),
			type
		);
	}

	@Test
	public void test_ResultType_Date_2() throws Exception {

		// SELECT {d '2008-12-31'} FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.date2");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of {d '2008-12-31'} should have been found",
			type
		);

		assertEquals(
			"The wrong type for {d '2008-12-31'} was retrieved",
			typeFor(namedQuery, java.sql.Date.class),
			type
		);
	}

	@Test
	public void test_ResultType_Delete_1() throws Exception {

		// DELETE FROM Employee e WHERE e.department = :dept
		IQuery namedQuery = namedQuery("Employee", "employee.delete.dept");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The object type should have been returned",
			type
		);

		assertEquals(
			"The wrong type was retrieved",
			typeFor(namedQuery, Object.class),
			type
		);
	}

	@Test
	public void test_ResultType_Division_1() throws Exception {

		// SELECT 2 / 2.2F FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.division1");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of 2 / 2.2F should have been found",
			type
		);

		assertEquals(
			"The wrong type for 2 / 2.2F was retrieved",
			typeFor(namedQuery, Float.class),
			type
		);
	}

	@Test
	public void test_ResultType_Division_2() throws Exception {

		// SELECT AVG(e.salary) / 2E2 FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.division2");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of AVG(salary) / 2E2 should have been found",
			type
		);

		assertEquals(
			"The wrong type for AVG(salary) / 2E2 was retrieved",
			typeFor(namedQuery, Double.class),
			type
		);
	}

	@Test
	public void test_ResultType_Division_3() throws Exception {

		// SELECT e.salary / 2 FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.division3");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of e.salary / 2 should have been found",
			type
		);

		assertEquals(
			"The wrong type for e.salary / 2 was retrieved",
			typeFor(namedQuery, Long.class),
			type
		);
	}

	@Test
	public void test_ResultType_Division_4() throws Exception {

		// SELECT e.name / 2 FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.division4");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of e.name / 2 should have been found",
			type
		);

		assertEquals(
			"The wrong type for e.name / 2 was retrieved",
			typeFor(namedQuery, Object.class),
			type
		);
	}

	@Test
	public void test_ResultType_Entry_1() throws Exception {

		// SELECT ENTRY(e) FROM Alias a JOIN a.ids e
		IQuery namedQuery = namedQuery("Alias", "alias.entry");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of ENTRY(e) should have been found",
			type
		);

		assertEquals(
			"The wrong type for ENTRY(e) was retrieved",
			typeFor(namedQuery, Map.Entry.class),
			type
		);
	}

	@Test
	public void test_ResultType_Enum() throws Exception {

		// SELECT CASE WHEN e.name = 'Pascal' THEN com.titan.domain.EnumType.FIRST_NAME WHEN e.name = 'JPQL' THEN com.titan.domain.EnumType.LAST_NAME ELSE com.titan.domain.EnumType.NAME END FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.enum");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of the case should have been found",
			type
		);

		assertEquals(
			"The wrong type for 2 - 2.2F was retrieved",
			typeFor(namedQuery, EnumType.class),
			type
		);
	}

	@Test
	public void test_ResultType_Func_1() throws Exception {

		// SELECT FUNC('toString', e.name) FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.func1");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of FUNC('toString', e.name) should have been found",
			type
		);

		assertEquals(
			"The wrong type for FUNC('toString', e.name) was retrieved",
			typeFor(namedQuery, Object.class),
			type
		);
	}

	@Test
	public void test_ResultType_Func_2() throws Exception {

		// SELECT FUNC('age', e.empId, e.salary) FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.func2");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of FUNC('age', e.empId, e.salary) should have been found",
			type
		);

		assertEquals(
			"The wrong type for FUNC('age', e.empId, e.salary) was retrieved",
			typeFor(namedQuery, Object.class),
			type
		);
	}

	@Test
	public void test_ResultType_Func_3() throws Exception {

		// SELECT FUNC('age', e.empId, e.name) FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.func3");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of FUNC('toString', e.name) should have been found",
			type
		);

		assertEquals(
			"The wrong type for FUNC('toString', e.name) was retrieved",
			typeFor(namedQuery, Object.class),
			type
		);
	}

	@Test
	public void test_ResultType_Func_4() throws Exception {

		// SELECT FUNC('age', e.empId, ?name) FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.func4");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of FUNC('age', e.empId, ?name) should have been found",
			type
		);

		assertEquals(
			"The wrong type for FUNC('age', e.empId, ?name) was retrieved",
			typeFor(namedQuery, Object.class),
			type
		);
	}

	@Test
	public void test_ResultType_Index_1() throws Exception {

		// SELECT INDEX(c) FROM Address a JOIN a.customerList c
		IQuery namedQuery = namedQuery("Address", "address.index");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of INDEX(e) should have been found",
			type
		);

		assertEquals(
			"The wrong type for INDEX(c) was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ResultType_Join_1() throws Exception {

		// SELECT c.lastName FROM Address a JOIN a.customerList AS c
		IQuery namedQuery = namedQuery("Address", "address.stateField");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The object type should have been returned",
			type
		);

		assertEquals(
			"The wrong type was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_Key_1() throws Exception {

		// SELECT KEY(k) FROM Alias a JOIN a.ids k
		IQuery namedQuery = namedQuery("Alias", "alias.key1");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of KEY(k) should have been found",
			type
		);

		assertEquals(
			"The wrong type for KEY(k) was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_Keyword_1() throws Exception {

		// SELECT TRUE FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.true");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of TRUE should have been found",
			type
		);

		assertEquals(
			"The wrong type for TRUE was retrieved",
			typeFor(namedQuery, Boolean.class),
			type
		);
	}

	@Test
	public void test_ResultType_Keyword_2() throws Exception {

		// SELECT FALSE FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.false");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of FALSE should have been found",
			type
		);

		assertEquals(
			"The wrong type for FALSE was retrieved",
			typeFor(namedQuery, Boolean.class),
			type
		);
	}

	@Test
	public void test_ResultType_Length_1() throws Exception {

		// SELECT LENGTH(a.street) FROM Address a
		IQuery namedQuery = namedQuery("Address", "address.length");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of LENGTH(a.street) should have been found",
			type
		);

		assertEquals(
			"The wrong type for LENGTH(a.street) was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ResultType_Locate_1() throws Exception {

		// SELECT LOCATE(a.street, 'Arco Drive') FROM Address a
		IQuery namedQuery = namedQuery("Address", "address.locate");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of LOCATE(a.street, 'Arco Drive') should have been found",
			type
		);

		assertEquals(
			"The wrong type for LOCATE(a.street, 'Arco Drive') was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ResultType_Lower_1() throws Exception {

		// SELECT LOWER(e.name) FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.lower");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of LOWER(e.name) should have been found",
			type
		);

		assertEquals(
			"The wrong type for LOWER(e.name) was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_Max_1() throws Exception {

		// SELECT MAX(p.quantity) FROM Product p
		IQuery namedQuery = namedQuery("Product", "product.max");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of MAX(p.quantity) should have been found",
			type
		);

		assertEquals(
			"The wrong type for MAX(p.quantity) was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ResultType_Max_2() throws Exception {

		// SELECT MAX(e.salary) FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.max");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of MAX(e.salary) should have been found",
			type
		);

		assertEquals(
			"The wrong type for MAX(e.salary) was retrieved",
			typeFor(namedQuery, Long.class),
			type
		);
	}

	@Test
	public void test_ResultType_Min_1() throws Exception {

		// SELECT MIN(p.quantity) FROM Product p
		IQuery namedQuery = namedQuery("Product", "product.min");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of MIN(p.quantity) should have been found",
			type
		);

		assertEquals(
			"The wrong type for MIN(p.quantity) was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ResultType_Min_2() throws Exception {

		// SELECT MIN(e.salary) FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.min");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of MIN(e.salary) should have been found",
			type
		);

		assertEquals(
			"The wrong type for MIN(e.salary) was retrieved",
			typeFor(namedQuery, Long.class),
			type
		);
	}

	@Test
	public void test_ResultType_Mod_1() throws Exception {

		// SELECT MOD(e.salary, e.empId) FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.mod");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of MOD(e.salary, e.empId) should have been found",
			type
		);

		assertEquals(
			"The wrong type for MOD(e.salary, e.empId) was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ResultType_Multiplication_1() throws Exception {

		// SELECT 2 * 2.2F FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.multiplication1");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of 2 * 2.2F should have been found",
			type
		);

		assertEquals(
			"The wrong type for 2 * 2.2F was retrieved",
			typeFor(namedQuery, Float.class),
			type
		);
	}

	@Test
	public void test_ResultType_Multiplication_2() throws Exception {

		// SELECT AVG(e.salary) * 2E2 FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.multiplication2");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of AVG(salary) * 2E2 should have been found",
			type
		);

		assertEquals(
			"The wrong type for AVG(salary) * 2E2 was retrieved",
			typeFor(namedQuery, Double.class),
			type
		);
	}

	@Test
	public void test_ResultType_Multiplication_3() throws Exception {

		// SELECT e.salary * 2 FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.multiplication3");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of e.salary * 2 should have been found",
			type
		);

		assertEquals(
			"The wrong type for e.salary * 2 was retrieved",
			typeFor(namedQuery, Long.class),
			type
		);
	}

	@Test
	public void test_ResultType_Multiplication_4() throws Exception {

		// SELECT e.name * 2 FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.multiplication4");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of e.name * 2 should have been found",
			type
		);

		assertEquals(
			"The wrong type for e.name * 2 was retrieved",
			typeFor(namedQuery, Object.class),
			type
		);
	}

	@Test
	public void test_ResultType_New_1() throws Exception {

		// SELECT NEW java.lang.Vector(d.empList) FROM Dept d
		IQuery namedQuery = namedQuery("Dept", "dept.new1");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of NEW java.lang.Vector(d.empList) should have been found",
			type
		);

		assertEquals(
			"The wrong type for NEW java.lang.Vector(d.empList) was retrieved",
			typeFor(namedQuery, Vector.class),
			type
		);
	}

	@Test
	public void test_ResultType_NullIf_1() throws Exception {

		// SELECT NULLIF(e.name, 'JPQL') FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.nullif1");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of NULLIF(e.name, 'JPQL') should have been found",
			type
		);

		assertEquals(
			"The wrong type for NULLIF(e.name, 'JPQL') was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_NullIf_2() throws Exception {

		// SELECT NULLIF(2 + 2, 'JPQL') FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.nullif2");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of NULLIF(2 + 2, 'JPQL') should have been found",
			type
		);

		assertEquals(
			"The wrong type for NULLIF(2 + 2, 'JPQL') was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ResultType_Object_1() throws Exception {

		// SELECT Distinct Object(c) From Customer c, IN(c.home.phones) p where p.area LIKE :area
		IQuery namedQuery = namedQuery("Customer", "customer.area");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of Object(c) should have been found",
			type
		);

		assertEquals(
			"The wrong type for Object(c) was retrieved",
			typeNamed(namedQuery, "jpql.query.Customer"),
			type
		);
	}

	@Test
	public void test_ResultType_Object_2() throws Exception {

		// select object(o) FROM Order o Where SQRT(o.totalPrice) > :doubleValue
		IQuery namedQuery = namedQuery("Order", "order.doubleValue");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of object(o) should have been found",
			type
		);

		assertEquals(
			"The wrong type for object(o) was retrieved",
			typeNamed(namedQuery, "jpql.query.Order"),
			type
		);
	}

	@Test
	public void test_ResultType_Object_3() throws Exception {

		// Select Distinct Object(p) from Product p where (p.quantity > (500 + :int1)) AND (p.partNumber IS NULL))
		IQuery namedQuery = namedQuery("Product", "product.int1");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of Object(p) should have been found",
			type
		);

		assertEquals(
			"The wrong type for Object(p) was retrieved",
			typeNamed(namedQuery, "jpql.query.Product"),
			type
		);
	}

	@Test
	public void test_ResultType_Path_1() throws Exception {

		// SELECT a.alias FROM Alias AS a WHERE (a.alias IS NULL AND :param1 IS NULL) OR a.alias = :param1
		IQuery namedQuery = namedQuery("Alias", "alias.param1");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of a.alias should have been found",
			type
		);

		assertEquals(
			"The wrong type for a.alias was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_Path_2() throws Exception {

		// select c.firstName FROM Customer c Group By c.firstName HAVING c.firstName = concat(:fname, :lname)
		IQuery namedQuery = namedQuery("Customer", "customer.name");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of c.firstName should have been found",
			type
		);

		assertEquals(
			"The wrong type for c.firstName was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_Path_3() throws Exception {

		// select Dept.floorNumber from Dept Dept
		IQuery namedQuery = namedQuery("Dept", "dept.floorNumber");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of Dept.floorNumber should have been found",
			type
		);

		assertEquals(
			"The wrong type for Dept.floorNumber was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ResultType_Schema_1() throws Exception {

		// SELECT c from Customer c where c.home.city IN :city
		IQuery namedQuery = namedQuery("Customer", "customer.city");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of c should have been found",
			type
		);

		assertEquals(
			"The wrong type for c was retrieved",
			typeNamed(namedQuery, "jpql.query.Customer"),
			type
		);
	}

	@Test
	public void test_ResultType_Schema_2() throws Exception {

		// SELECT DISTINCT p From Product p where p.shelfLife.soldDate NOT BETWEEN :date1 AND :newdate
		IQuery namedQuery = namedQuery("Product", "product.date");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of p should have been found",
			type
		);

		assertEquals(
			"The wrong type for p was retrieved",
			typeNamed(namedQuery, "jpql.query.Product"),
			type
		);
	}

	@Test
	public void test_ResultType_SelectWithResultVariable_1() throws Exception {

		// SELECT e.name AS n From Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.resultVariable1");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of e.name as n should have been found",
			type
		);

		assertEquals(
			"The wrong type for e.name as n was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_SelectWithResultVariable_2() throws Exception {

		// SELECT e.name n From Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.resultVariable2");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of e.name as n should have been found",
			type
		);

		assertEquals(
			"The wrong type for e.name as n was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_SelectWithResultVariable_3() throws Exception {

		// SELECT e.salary / 1000D n From Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.resultVariable3");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of e.salary / 1000D n as n should have been found",
			type
		);

		assertEquals(
			"The wrong type for e.salary / 1000D n as n was retrieved",
			typeFor(namedQuery, Double.class),
			type
		);
	}

	@Test
	public void test_ResultType_Size_1() throws Exception {

		// SELECT SIZE(c) FROM Address a JOIN a.customerList c
		IQuery namedQuery = namedQuery("Address", "address.size");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of SIZE(c) should have been found",
			type
		);

		assertEquals(
			"The wrong type for SIZE(c) was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ResultType_Sqrt_1() throws Exception {

		// SELECT SQRT(o.totalPrice) FROM Order o
		IQuery namedQuery = namedQuery("Order", "order.sqrt");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of SQRT(o.totalPrice) should have been found",
			type
		);

		assertEquals(
			"The wrong type for SQRT(o.totalPrice) was retrieved",
			typeFor(namedQuery, Double.class),
			type
		);
	}

	@Test
	public void test_ResultType_Substraction_1() throws Exception {

		// SELECT 2 / 2.2F FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.substraction1");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of 2 - 2.2F should have been found",
			type
		);

		assertEquals(
			"The wrong type for 2 - 2.2F was retrieved",
			typeFor(namedQuery, Float.class),
			type
		);
	}

	@Test
	public void test_ResultType_Substraction_2() throws Exception {

		// SELECT AVG(e.salary) - 2E2 FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.substraction2");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of AVG(salary) / 2E2 should have been found",
			type
		);

		assertEquals(
			"The wrong type for AVG(salary) / 2E2 was retrieved",
			typeFor(namedQuery, Double.class),
			type
		);
	}

	@Test
	public void test_ResultType_Substraction_3() throws Exception {

		// SELECT e.name - 2 FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.substraction3");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of e.name - 2 should have been found",
			type
		);

		assertEquals(
			"The wrong type for e.name - 2 was retrieved",
			typeFor(namedQuery, Long.class),
			type
		);
	}

	@Test
	public void test_ResultType_Substraction_4() throws Exception {

		// SELECT e.salary - 2 FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.substraction4");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of e.salary - 2 should have been found",
			type
		);

		assertEquals(
			"The wrong type for e.salary - 2 was retrieved",
			typeFor(namedQuery, Object.class),
			type
		);
	}

	@Test
	public void test_ResultType_Substring_1() throws Exception {

		// SELECT SUBSTRING(a.state, 0, 1) FROM Address a
		IQuery namedQuery = namedQuery("Address", "address.length");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of SUBSTRING(a.state, 0, 1) should have been found",
			type
		);

		assertEquals(
			"The wrong type for SUBSTRING(a.state, 0, 1) was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ResultType_Sum_1() throws Exception {

		// SELECT SUM(e.salary) FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.sum");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of SUM(e.salary) should have been found",
			type
		);

		assertEquals(
			"The wrong type for SUM(p.quantity) was retrieved",
			typeFor(namedQuery, Long.class),
			type
		);
	}

	@Test
	public void test_ResultType_Sum_2() throws Exception {

		// SELECT SUM(o.totalPrice) FROM Order o
		IQuery namedQuery = namedQuery("Order", "order.sum1");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of SUM(o.totalPrice) should have been found",
			type
		);

		assertEquals(
			"The wrong type for SUM(o.totalPrice) was retrieved",
			typeFor(namedQuery, Double.class),
			type
		);
	}

	@Test
	public void test_ResultType_Sum_4() throws Exception {

		// SELECT SUM(o.totalPrice) FROM Order o
		IQuery namedQuery = namedQuery("Order", "order.sum2");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of SUM(o.price) should have been found",
			type
		);

		assertEquals(
			"The wrong type for SUM(o.price) was retrieved",
			typeFor(namedQuery, BigInteger.class),
			type
		);
	}

	@Test
	public void test_ResultType_Sum_5() throws Exception {

		// SELECT SUM(o.realPrice) FROM Order o
		IQuery namedQuery = namedQuery("Order", "order.sum3");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of SUM(o.realPrice) should have been found",
			type
		);

		assertEquals(
			"The wrong type for SUM(o.realPrice) was retrieved",
			typeFor(namedQuery, BigDecimal.class),
			type
		);
	}

	@Test
	public void test_ResultType_Trim_1() throws Exception {

		// SELECT TRIM(e.name) FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.trim");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of TRIM(e.name) should have been found",
			type
		);

		assertEquals(
			"The wrong type for TRIM(e.name) was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_Update_1() throws Exception {

		// UPDATE Employee e SET e.manager = ?1 WHERE e.department = ?2
		IQuery namedQuery = namedQuery("Employee", "employee.update.positional");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The Object type should have been returned",
			type
		);

		assertEquals(
			"The wrong type was retrieved",
			typeFor(namedQuery, Object.class),
			type
		);
	}

	@Test
	public void test_ResultType_Upper_1() throws Exception {

		// SELECT UPPER(e.name) FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.upper");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of UPPER(e.name) should have been found",
			type
		);

		assertEquals(
			"The wrong type for UPPER(e.name) was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_Value_1() throws Exception {

		// SELECT VALUE(v) FROM Alias a JOIN a.ids v
		IQuery namedQuery = namedQuery("Alias", "alias.value1");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of VALUE(v) should have been found",
			type
		);

		assertEquals(
			"The wrong type for VALUE(v) was retrieved",
			typeFor(namedQuery, Date.class),
			type
		);
	}

	@Test
	public void test_ResultType_Value_2() throws Exception {

		// SELECT v FROM Alias a JOIN a.ids v
		IQuery namedQuery = namedQuery("Alias", "alias.value2");

		AbstractJPQLQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull(
			"The type of v should have been found",
			type
		);

		assertEquals(
			"The wrong type for v was retrieved",
			typeFor(namedQuery, Date.class),
			type
		);
	}

	protected IType typeFor(IQuery query, Class<?> type) {
		return typeRepository(query).getType(type);
	}

	protected IType typeNamed(IQuery query, String typeName) {
		return query.getProvider().getTypeRepository().getType(typeName);
	}

	protected ITypeRepository typeRepository(IQuery query) {
		return query.getProvider().getTypeRepository();
	}
}