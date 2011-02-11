/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedType;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedTypeProvider;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;
import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeRepository;
import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public abstract class AbstractQueryHelperTest<T> extends AbstractQueryTest
{
	protected abstract AbstractQueryHelper<T> buildQueryHelper(IQuery namedQuery) throws Exception;

	protected IManagedType entity(IManagedTypeProvider provider,
	                              String entityName) throws Exception
	{
		IManagedType entity = provider.getManagedType(entityName);
		assertNotNull("The named query count not be found", entity);
		return entity;
	}

	protected IType mappedType(IQuery query, String entityName)
	{
		return query.getProvider().getManagedType(entityName).getType();
	}

	protected abstract IQuery namedQuery(String entityName, String queryName) throws Exception;

	protected IManagedTypeProvider provider(IQuery query)
	{
		return query.getProvider();
	}

	@Test
	public void test_ParameterType_Alias_param1_String() throws Exception
	{
		// SELECT a.alias FROM Alias AS a WHERE (a.alias IS NULL AND :param1 IS NULL) OR a.alias = :param1
		IQuery namedQuery = namedQuery("Alias", "alias.param1");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":param1");

		assertNotNull
		(
			"The type of :param1 should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for :param1 was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Customer_area_String() throws Exception
	{
		// SELECT Distinct Object(c) From Customer c, IN(c.home.phones) p where p.area LIKE :area
		IQuery namedQuery = namedQuery("Customer", "customer.area");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":area");

		assertNotNull
		(
			"The type of :area should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for :area was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Customer_city_String() throws Exception
	{
		// SELECT c from Customer c where c.home.city IN(:city)
		IQuery namedQuery = namedQuery("Customer", "customer.city");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":city");

		assertNotNull
		(
			"The type of :city should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for :city was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Customer_fname_String() throws Exception
	{
		// select c.firstName FROM Customer c Group By c.firstName HAVING c.firstName = concat(:fname, :lname)
		IQuery namedQuery = namedQuery("Customer", "customer.name");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":fname");

		assertNotNull
		(
			"The type of :fname should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for :fname was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Customer_int1_String() throws Exception
	{
		// select count(c) FROM Customer c JOIN c.aliases a GROUP BY a.alias HAVING a.alias = SUBSTRING(:string1, :int1, :int2)
		IQuery namedQuery = namedQuery("Customer", "customer.substring");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":int1");

		assertNotNull
		(
			"The type of :int1 should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for :int1 was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Customer_int2_String() throws Exception
	{
		// select count(c) FROM Customer c JOIN c.aliases a GROUP BY a.alias HAVING a.alias = SUBSTRING(:string1, :int1, :int2)
		IQuery namedQuery = namedQuery("Customer", "customer.substring");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":int2");

		assertNotNull
		(
			"The type of :int2 should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for :int2 was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Customer_lname_String() throws Exception
	{
		// select c.firstName FROM Customer c Group By c.firstName HAVING c.firstName = concat(:fname, :lname)
		IQuery namedQuery = namedQuery("Customer", "customer.name");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":lname");

		assertNotNull
		(
			"The type of :lname should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for :lname was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Customer_string1_String() throws Exception
	{
		// select count(c) FROM Customer c JOIN c.aliases a GROUP BY a.alias HAVING a.alias = SUBSTRING(:string1, :int1, :int2)
		IQuery namedQuery = namedQuery("Customer", "customer.substring");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":string1");

		assertNotNull
		(
			"The type of :string1 should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for :string1 was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Dept_dname1_String() throws Exception
	{
		// select o from Dept o where o.dname in (:dname1, :dname2, :dname3)
		IQuery namedQuery = namedQuery("Dept", "dept.dname");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":dname1");

		assertNotNull
		(
			"The type of :dname1 should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for :newdate was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Dept_dname2_String() throws Exception
	{
		// select o from Dept o where o.dname in (:dname1, :dname2, :dname3)
		IQuery namedQuery = namedQuery("Dept", "dept.dname");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":dname2");

		assertNotNull
		(
			"The type of :dname2 should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for :newdate was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Dept_dname3_String() throws Exception
	{
		// select o from Dept o where o.dname in (:dname1, :dname2, :dname3)
		IQuery namedQuery = namedQuery("Dept", "dept.dname");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":dname3");

		assertNotNull
		(
			"The type of :dname3 should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for :newdate was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Employee_Base_Long() throws Exception
	{
		// SELECT e FROM Employee e WHERE e.department = :dept AND e.salary > :base
		IQuery namedQuery = namedQuery("Employee", "employee.deptBase");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":base");

		assertNotNull
		(
			"The type of :base should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for :base was retrieved",
			typeFor(namedQuery, Long.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Employee_Delete_dept() throws Exception
	{
		// DELETE FROM Employee e WHERE e.department = :dept
		IQuery namedQuery = namedQuery("Employee", "employee.delete.dept");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":dept");

		assertNotNull
		(
			"The type of :dept should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for :dept was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Employee_Dept_String() throws Exception
	{
		// SELECT e FROM Employee e WHERE e.department = :dept AND e.salary > :base
		IQuery namedQuery = namedQuery("Employee", "employee.deptBase");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":dept");

		assertNotNull
		(
			"The type of :dept should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for :dept was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Employee_deptno_Long() throws Exception
	{
		// select e from Employee e where e.dept.deptno in (:deptno)
		IQuery namedQuery = namedQuery("Employee", "employee.deptno");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":deptno");

		assertNotNull
		(
			"The type of :deptno should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for :deptno was retrieved",
			typeFor(namedQuery, Long.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Employee_positional1_String() throws Exception
	{
		// SELECT e FROM Employee e WHERE e.name = ?1 ORDER BY e.name
		IQuery namedQuery = namedQuery("Employee", "employee.?1");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType("?1");

		assertNotNull
		(
			"The type of ?1 should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for ?1 was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Employee_SubQuery_Dept_String() throws Exception
	{
		// SELECT e FROM Employee e WHERE e.salary = (SELECT MAX(e.salary) FROM Employee e WHERE e.department = :dept)
		IQuery namedQuery = namedQuery("Employee", "employee.dept");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":dept");

		assertNotNull
		(
			"The type of :dept should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for :dept was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Employee_Update_positional_1() throws Exception
	{
		// UPDATE Employee e SET e.manager = ?1 WHERE e.department = ?2
		IQuery namedQuery = namedQuery("Employee", "employee.update.positional");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType("?1");

		assertNotNull
		(
			"The type of ?1 should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for ?1 was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Employee_Update_positional_2() throws Exception
	{
		// UPDATE Employee e SET e.manager = ?1 WHERE e.department = ?2
		IQuery namedQuery = namedQuery("Employee", "employee.update.positional");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType("?2");

		assertNotNull
		(
			"The type of ?2 should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for ?2 was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Order_doubleValue_Double() throws Exception
	{
		// select object(o) FROM Order o Where SQRT(o.totalPrice) > :doubleValue
		IQuery namedQuery = namedQuery("Order", "order.doubleValue");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":doubleValue");

		assertNotNull
		(
			"The type of :dept should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for :dept was retrieved",
			typeFor(namedQuery, Double.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Product_date1_Date() throws Exception
	{
		// SELECT DISTINCT p From Product p where p.shelfLife.soldDate NOT BETWEEN :date1 AND :newdate
		IQuery namedQuery = namedQuery("Product", "product.date");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":date1");

		assertNotNull
		(
			"The type of :date1 should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for :date1 was retrieved",
			typeFor(namedQuery, Date.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Product_int1_int() throws Exception
	{
		// Select Distinct Object(p) from Product p where (p.quantity > (500 + :int1)) AND (p.partNumber IS NULL))
		IQuery namedQuery = namedQuery("Product", "product.int1");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":int1");

		assertNotNull
		(
			"The type of :dept should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for :dept was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ParameterType_Product_newDate_Date() throws Exception
	{
		// SELECT DISTINCT p From Product p where p.shelfLife.soldDate NOT BETWEEN :date1 AND :newdate
		IQuery namedQuery = namedQuery("Product", "product.date");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":newdate");

		assertNotNull
		(
			"The type of :newdate should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for :newdate was retrieved",
			typeFor(namedQuery, Date.class),
			type
		);
	}

	@Test
	public void test_ParameterType_SubQuery_String() throws Exception
	{
		// SELECT e FROM Employee e WHERE EXISTS (SELECT p FROM Project p JOIN p.employees emp WHERE emp = e AND p.name = :name)
		IQuery namedQuery = namedQuery("Employee", "employee.subquery");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":name");

		assertNotNull
		(
			"The type of :name should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for :name was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_Abs_1() throws Exception
	{
		// SELECT ABS(p.quantity) FROM Product p
		IQuery namedQuery = namedQuery("Product", "product.abs");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of ABS(p.quantity) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for ABS(p.quantity) was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ResultType_Abs_2() throws Exception
	{
		// SELECT ABS(o.totalPrice) FROM Order o
		IQuery namedQuery = namedQuery("Order", "order.abs");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of ABS(o.totalPrice) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for ABS(o.totalPrice) was retrieved",
			typeFor(namedQuery, Double.class),
			type
		);
	}

	@Test
	public void test_ResultType_Abs_3() throws Exception
	{
		// SELECT ABS(p.id) FROM Project p
		IQuery namedQuery = namedQuery("Project", "project.abs");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of ABS(p.id) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for ABS(p.id) was retrieved",
			typeFor(namedQuery, Float.class),
			type
		);
	}

	@Test
	public void test_ResultType_Avg_1() throws Exception
	{
		// SELECT AVG(p.quantity) FROM Product p
		IQuery namedQuery = namedQuery("Product", "product.quantity");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of AVG(p.quantity) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for AVG(p.quantity) was retrieved",
			typeFor(namedQuery, Double.class),
			type
		);
	}

	@Test
	public void test_ResultType_Bad_1() throws Exception
	{
		// SELECT FROM Home h
		IQuery namedQuery = namedQuery("Home", "home.bad1");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of (nothing) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for (nothing) was retrieved",
			typeFor(namedQuery, Object.class),
			type
		);
	}

	@Test
	public void test_ResultType_Bad_2() throws Exception
	{
		// SELEC
		IQuery namedQuery = namedQuery("Home", "home.bad2");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of (nothing) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for (nothing) was retrieved",
			typeFor(namedQuery, Object.class),
			type
		);
	}

	@Test
	public void test_ResultType_CaseInsensitive() throws Exception
	{
		// SELECT e FROM Employee E
		IQuery namedQuery = namedQuery("Employee", "employee.caseInsensitive");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of e should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for e was retrieved",
			mappedType(namedQuery, "Employee"),
			type
		);
	}

	@Test
	public void test_ResultType_Coalesce_1() throws Exception
	{
		// SELECT COALESCE(o.price, o.price) FROM Order o
		IQuery namedQuery = namedQuery("Order", "order.coalesce1");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of COALESCE(o.price, o.price) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for COALESCE(o.price, o.price) was retrieved",
			typeFor(namedQuery, BigInteger.class),
			type
		);
	}

	@Test
	public void test_ResultType_Coalesce_2() throws Exception
	{
		// SELECT COALESCE(o.totalPrice, SQRT(o.realPrice)) FROM Order o
		IQuery namedQuery = namedQuery("Order", "order.coalesce2");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of COALESCE(o.totalPrice, SQRT(o.realPrice)) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for COALESCE(o.totalPrice, SQRT(o.realPrice)) was retrieved",
			typeFor(namedQuery, Double.class),
			type
		);
	}

	@Test
	public void test_ResultType_Coalesce_3() throws Exception
	{
		// SELECT COALESCE(o.number, e.name) FROM Order o, Employee e
		IQuery namedQuery = namedQuery("Order", "order.coalesce3");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of COALESCE(o.number, e.name) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for COALESCE(o.number, e.name) was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_Coalesce_4() throws Exception
	{
		// SELECT COALESCE(o.price, o.number) FROM Order o
		IQuery namedQuery = namedQuery("Order", "order.coalesce4");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of COALESCE(o.price, o.number) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for COALESCE(o.price, o.number) was retrieved",
			typeFor(namedQuery, Object.class),
			type
		);
	}

	@Test
	public void test_ResultType_Collection_1() throws Exception
	{
		// SELECT e.name, d.dname FROM Employee e, Dept d
		IQuery namedQuery = namedQuery("Employee", "employee.collection");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of (e.name, d.dname) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for (e.name, d.dname) was retrieved",
			typeFor(namedQuery, Object[].class),
			type
		);
	}

	@Test
	public void test_ResultType_CollectionType_1() throws Exception
	{
		// SELECT c FROM Address a JOIN a.customerList c
		IQuery namedQuery = namedQuery("Address", "address.collection");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of c should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for c was retrieved",
			mappedType(namedQuery, "Customer"),
			type
		);
	}

	@Test
	public void test_ResultType_Concat_1() throws Exception
	{
		// SELECT CONCAT(a.street, a.city) FROM Address a
		IQuery namedQuery = namedQuery("Address", "address.concat");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of CONCAT(a.street, a.city) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for CONCAT(a.street, a.city) was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_Count_1() throws Exception
	{
		// select count(c) FROM Customer c JOIN c.aliases a GROUP BY a.alias HAVING a.alias = SUBSTRING(:string1, :int1, :int2)
		IQuery namedQuery = namedQuery("Customer", "customer.substring");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of count(c) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for count(c) was retrieved",
			typeFor(namedQuery, Long.class),
			type
		);
	}

	@Test
	public void test_ResultType_Count_2() throws Exception
	{
		// select count(c) FROM Customer c JOIN c.aliases a GROUP BY a.alias HAVING a.alias = SUBSTRING(:string1, :int1, :int2)
		IQuery namedQuery = namedQuery("Customer", "customer.substring");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of count(c) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for count(c) was retrieved",
			typeFor(namedQuery, Long.class),
			type
		);
	}

	@Test
	public void test_ResultType_Delete_1() throws Exception
	{
		// DELETE FROM Employee e WHERE e.department = :dept
		IQuery namedQuery = namedQuery("Employee", "employee.delete.dept");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The object type should have been returned",
			type
		);

		assertEquals
		(
			"The wrong type was retrieved",
			typeFor(namedQuery, Object.class),
			type
		);
	}

	@Test
	public void test_ResultType_Entry_1() throws Exception
	{
		// SELECT ENTRY(e) FROM Alias a JOIN a.ids e
		IQuery namedQuery = namedQuery("Alias", "alias.entry");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of ENTRY(e) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for ENTRY(e) was retrieved",
			typeFor(namedQuery, Map.Entry.class),
			type
		);
	}

	@Test
	public void test_ResultType_Func_1() throws Exception
	{
		// SELECT FUNC('toString', e.name) FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.func1");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of FUNC('toString', e.name) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for FUNC('toString', e.name) was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_Func_2() throws Exception
	{
		// SELECT FUNC('age', e.empId, e.salary) FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.func2");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of FUNC('age', e.empId, e.salary) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for FUNC('age', e.empId, e.salary) was retrieved",
			typeFor(namedQuery, Number.class),
			type
		);
	}

	@Test
	public void test_ResultType_Func_3() throws Exception
	{
		// SELECT FUNC('age', e.empId, e.name) FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.func3");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of FUNC('toString', e.name) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for FUNC('toString', e.name) was retrieved",
			typeFor(namedQuery, Object.class),
			type
		);
	}

	@Test
	public void test_ResultType_Func_4() throws Exception
	{
		// SELECT FUNC('age', e.empId, ?name) FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.func4");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of FUNC('age', e.empId, ?name) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for FUNC('age', e.empId, ?name) was retrieved",
			typeFor(namedQuery, Number.class),
			type
		);
	}

	@Test
	public void test_ResultType_Index_1() throws Exception
	{
		// SELECT INDEX(e) FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.index");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of INDEX(e) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for INDEX(e) was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ResultType_Join_1() throws Exception
	{
		// SELECT c.lastName FROM Address a JOIN a.customerList AS c
		IQuery namedQuery = namedQuery("Address", "address.query5");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The object type should have been returned",
			type
		);

		assertEquals
		(
			"The wrong type was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_Key_1() throws Exception
	{
		// SELECT KEY(k) FROM Alias a JOIN a.ids k
		IQuery namedQuery = namedQuery("Alias", "alias.key");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of KEY(k) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for KEY(k) was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_Length_1() throws Exception
	{
		// SELECT LENGTH(a.street) FROM Address a
		IQuery namedQuery = namedQuery("Address", "address.length");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of LENGTH(a.street) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for LENGTH(a.street) was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ResultType_Locate_1() throws Exception
	{
		// SELECT LOCATE(a.street, 'Arco Drive') FROM Address a
		IQuery namedQuery = namedQuery("Address", "address.locate");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of LOCATE(a.street, 'Arco Drive') should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for LOCATE(a.street, 'Arco Drive') was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ResultType_Lower_1() throws Exception
	{
		// SELECT LOWER(e.name) FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.lower");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of LOWER(e.name) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for LOWER(e.name) was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_Max_1() throws Exception
	{
		// SELECT MAX(p.quantity) FROM Product p
		IQuery namedQuery = namedQuery("Product", "product.max");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of MAX(p.quantity) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for MAX(p.quantity) was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ResultType_Max_2() throws Exception
	{
		// SELECT MAX(e.salary) FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.max");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of MAX(e.salary) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for MAX(e.salary) was retrieved",
			typeFor(namedQuery, Long.class),
			type
		);
	}

	@Test
	public void test_ResultType_Min_1() throws Exception
	{
		// SELECT MIN(p.quantity) FROM Product p
		IQuery namedQuery = namedQuery("Product", "product.min");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of MIN(p.quantity) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for MIN(p.quantity) was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ResultType_Min_2() throws Exception
	{
		// SELECT MIN(e.salary) FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.min");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of MIN(e.salary) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for MIN(e.salary) was retrieved",
			typeFor(namedQuery, Long.class),
			type
		);
	}

	@Test
	public void test_ResultType_Mod_1() throws Exception
	{
		// SELECT MOD(e.salary, e.empId) FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.mod");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of MOD(e.salary, e.empId) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for MOD(e.salary, e.empId) was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ResultType_Object_1() throws Exception
	{
		// SELECT Distinct Object(c) From Customer c, IN(c.home.phones) p where p.area LIKE :area
		IQuery namedQuery = namedQuery("Customer", "customer.area");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of Object(c) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for Object(c) was retrieved",
			typeNamed(namedQuery, "jpql.query.Customer"),
			type
		);
	}

	@Test
	public void test_ResultType_Object_2() throws Exception
	{
		// select object(o) FROM Order o Where SQRT(o.totalPrice) > :doubleValue
		IQuery namedQuery = namedQuery("Order", "order.doubleValue");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of object(o) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for object(o) was retrieved",
			typeNamed(namedQuery, "jpql.query.Order"),
			type
		);
	}

	@Test
	public void test_ResultType_Object_3() throws Exception
	{
		// Select Distinct Object(p) from Product p where (p.quantity > (500 + :int1)) AND (p.partNumber IS NULL))
		IQuery namedQuery = namedQuery("Product", "product.int1");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of Object(p) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for Object(p) was retrieved",
			typeNamed(namedQuery, "jpql.query.Product"),
			type
		);
	}

	@Test
	public void test_ResultType_Path_1() throws Exception
	{
		// SELECT a.alias FROM Alias AS a WHERE (a.alias IS NULL AND :param1 IS NULL) OR a.alias = :param1
		IQuery namedQuery = namedQuery("Alias", "alias.param1");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of a.alias should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for a.alias was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_Path_2() throws Exception
	{
		// select c.firstName FROM Customer c Group By c.firstName HAVING c.firstName = concat(:fname, :lname)
		IQuery namedQuery = namedQuery("Customer", "customer.name");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of c.firstName should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for c.firstName was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_Path_3() throws Exception
	{
		// select Dept.floorNumber from Dept Dept
		IQuery namedQuery = namedQuery("Dept", "dept.floorNumber");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of Dept.floorNumber should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for Dept.floorNumber was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ResultType_Schema_1() throws Exception
	{
		// SELECT c from Customer c where c.home.city IN(:city)
		IQuery namedQuery = namedQuery("Customer", "customer.city");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of c should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for c was retrieved",
			typeNamed(namedQuery, "jpql.query.Customer"),
			type
		);
	}

	@Test
	public void test_ResultType_Schema_2() throws Exception
	{
		// SELECT DISTINCT p From Product p where p.shelfLife.soldDate NOT BETWEEN :date1 AND :newdate
		IQuery namedQuery = namedQuery("Product", "product.date");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of p should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for p was retrieved",
			typeNamed(namedQuery, "jpql.query.Product"),
			type
		);
	}

	@Test
	public void test_ResultType_SelectWithResultVariable_1() throws Exception
	{
		// SELECT e.name AS n From Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.resultVariable1");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of e.name as n should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for e.name as n was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_SelectWithResultVariable_2() throws Exception
	{
		// SELECT e.name n From Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.resultVariable2");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of e.name as n should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for e.name as n was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_SelectWithResultVariable_3() throws Exception
	{
		// SELECT e.salary / 1000D n From Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.resultVariable3");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of e.salary / 1000D n as n should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for e.salary / 1000D n as n was retrieved",
			typeFor(namedQuery, Double.class),
			type
		);
	}

	@Test
	public void test_ResultType_Size_1() throws Exception
	{
		// SELECT SIZE(c) FROM Address a JOIN a.customerList c
		IQuery namedQuery = namedQuery("Address", "address.size");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of SIZE(c) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for SIZE(c) was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ResultType_Sqrt_1() throws Exception
	{
		// SELECT SQRT(o.totalPrice) FROM Order o
		IQuery namedQuery = namedQuery("Order", "order.sqrt");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of SQRT(o.totalPrice) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for SQRT(o.totalPrice) was retrieved",
			typeFor(namedQuery, Double.class),
			type
		);
	}

	@Test
	public void test_ResultType_Substring_1() throws Exception
	{
		// SELECT SUBSTRING(a.state, 0, 1) FROM Address a
		IQuery namedQuery = namedQuery("Address", "address.length");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of SUBSTRING(a.state, 0, 1) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for SUBSTRING(a.state, 0, 1) was retrieved",
			typeFor(namedQuery, Integer.class),
			type
		);
	}

	@Test
	public void test_ResultType_Sum_1() throws Exception
	{
		// SELECT SUM(e.salary) FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.sum");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of SUM(e.salary) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for SUM(p.quantity) was retrieved",
			typeFor(namedQuery, Long.class),
			type
		);
	}

	@Test
	public void test_ResultType_Sum_2() throws Exception
	{
		// SELECT SUM(o.totalPrice) FROM Order o
		IQuery namedQuery = namedQuery("Order", "order.sum1");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of SUM(o.totalPrice) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for SUM(o.totalPrice) was retrieved",
			typeFor(namedQuery, Double.class),
			type
		);
	}

	@Test
	public void test_ResultType_Sum_4() throws Exception
	{
		// SELECT SUM(o.totalPrice) FROM Order o
		IQuery namedQuery = namedQuery("Order", "order.sum2");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of SUM(o.price) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for SUM(o.price) was retrieved",
			typeFor(namedQuery, BigInteger.class),
			type
		);
	}

	@Test
	public void test_ResultType_Sum_5() throws Exception
	{
		// SELECT SUM(o.realPrice) FROM Order o
		IQuery namedQuery = namedQuery("Order", "order.sum3");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of SUM(o.realPrice) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for SUM(o.realPrice) was retrieved",
			typeFor(namedQuery, BigDecimal.class),
			type
		);
	}

	@Test
	public void test_ResultType_Trim_1() throws Exception
	{
		// SELECT TRIM(e.name) FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.trim");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of TRIM(e.name) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for TRIM(e.name) was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_Update_1() throws Exception
	{
		// UPDATE Employee e SET e.manager = ?1 WHERE e.department = ?2
		IQuery namedQuery = namedQuery("Employee", "employee.update.positional");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The Object type should have been returned",
			type
		);

		assertEquals
		(
			"The wrong type was retrieved",
			typeFor(namedQuery, Object.class),
			type
		);
	}

	@Test
	public void test_ResultType_Upper_1() throws Exception
	{
		// SELECT UPPER(e.name) FROM Employee e
		IQuery namedQuery = namedQuery("Employee", "employee.upper");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of UPPER(e.name) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for UPPER(e.name) was retrieved",
			typeFor(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_Value_1() throws Exception
	{
		// SELECT VALUE(v) FROM Alias a JOIN a.ids v
		IQuery namedQuery = namedQuery("Alias", "alias.value1");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of VALUE(v) should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for VALUE(v) was retrieved",
			typeFor(namedQuery, Date.class),
			type
		);
	}

	@Test
	public void test_ResultType_Value_2() throws Exception
	{
		// SELECT v FROM Alias a JOIN a.ids v
		IQuery namedQuery = namedQuery("Alias", "alias.value2");

		AbstractQueryHelper<T> helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();

		assertNotNull
		(
			"The type of v should have been found",
			type
		);

		assertEquals
		(
			"The wrong type for v was retrieved",
			typeFor(namedQuery, Date.class),
			type
		);
	}

	protected IType typeFor(IQuery query, Class<?> type)
	{
		return typeRepository(query).getType(type);
	}

	private IType typeNamed(IQuery query, String typeName)
	{
		return query.getProvider().getTypeRepository().getType(typeName);
	}

	protected ITypeRepository typeRepository(IQuery query)
	{
		return query.getProvider().getTypeRepository();
	}
}