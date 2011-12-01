/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql.model;

import org.junit.Test;

import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries.*;

/**
 * This tests the automatic creation by the builder of a {@link StateObject} by converting the
 * parsed representation of a JPQL query using the JPQL grammar defined in JPA 1.0.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public final class StateObjectTest1_0 extends AbstractStateObjectTest1_0 {

	@Test
	public void test_Query_001() throws Exception {
		// SELECT e FROM Employee e
		testQuery(query_001(), stateObject_001());
	}

	@Test
	public void test_Query_002() throws Exception {
		// SELECT e\nFROM Employee e
		testQuery(query_002(), stateObject_002());
	}

	@Test
	public void test_Query_003() throws Exception {

		// SELECT e
      // FROM Employee e
      // WHERE e.department.name = 'NA42' AND
      //       e.address.state IN ('NY', 'CA')

		testQuery(query_003(), stateObject_003());
	}

	@Test
	public void test_Query_004() throws Exception {

		// SELECT p.number
		// FROM Employee e, Phone p
		// WHERE     e = p.employee
		//       AND e.department.name = 'NA42'
		//       AND p.type = 'Cell'

		testQuery(query_004(), stateObject_004());
	}

	@Test
	public void test_Query_005() throws Exception {

		// SELECT d, COUNT(e), MAX(e.salary), AVG(e.salary)
		// FROM Department d JOIN d.employees e
		// GROUP BY d
		// HAVING COUNT(e) >= 5

		testQuery(query_005(), stateObject_005());
	}

	@Test
	public void test_Query_006() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE     e.department = ?1
		//       AND e.salary > ?2

		testQuery(query_006(), stateObject_006());
	}

	@Test
	public void test_Query_007() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE     e.department = :dept
		//       AND e.salary > :base

		testQuery(query_007(), stateObject_007());
	}

	@Test
	public void test_Query_008() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE     e.department = 'NA65'
		//       AND e.name = 'UNKNOWN'' OR e.name = ''Roberts'

		testQuery(query_008(), stateObject_008());
	}

	@Test
	public void test_Query_009() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE e.startDate BETWEEN ?1 AND ?2

		testQuery(query_009(), stateObject_009());
	}

	@Test
	public void test_Query_010() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE e.department = :dept AND
		//      e.salary = (SELECT MAX(e.salary)
		//                  FROM Employee e
		//                  WHERE e.department = :dept)

		testQuery(query_010(), stateObject_010());
	}

	@Test
	public void test_Query_011() throws Exception {

		// SELECT e
		// FROM Project p JOIN p.employees e
		// WHERE p.name = ?1
		// ORDER BY e.name

		testQuery(query_011(), stateObject_011());
	}

	@Test
	public void test_Query_012() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE e.projects IS EMPTY";

		testQuery(query_012(), stateObject_012());
	}

	@Test
	public void test_Query_013() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE e.projects IS NOT EMPTY";

		testQuery(query_013(), stateObject_013());
	}

	@Test
	public void test_Query_014() throws Exception {

		// UPDATE Employee e
		// SET e.manager = ?1
		// WHERE e.department = ?2

		testQuery(query_014(), stateObject_014());
	}

	@Test
	public void test_Query_015() throws Exception {

		// DELETE FROM Project p
      // WHERE p.employees IS EMPTY

		testQuery(query_015(), stateObject_015());
	}

	@Test
	public void test_Query_016() throws Exception {

		// DELETE FROM Department d
		// WHERE d.name IN ('CA13', 'CA19', 'NY30')

		testQuery(query_016(), stateObject_016());
	}

	@Test
	public void test_Query_017() throws Exception {

		// UPDATE Employee e
		// SET e.department = null
		// WHERE e.department.name IN ('CA13', 'CA19', 'NY30')

		testQuery(query_017(), stateObject_017());
	}

	@Test
	public void test_Query_018() throws Exception {

		// SELECT d
		// FROM Department d
		// WHERE d.name LIKE 'QA\\_%' ESCAPE '\\'

		testQuery(query_018(), stateObject_018());
	}

	@Test
	public void test_Query_019() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE e.salary = (SELECT MAX(e2.salary) FROM Employee e2)

		testQuery(query_019(), stateObject_019());
	}

	@Test
	public void test_Query_020() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE EXISTS (SELECT p FROM Phone p WHERE p.employee = e AND p.type = 'Cell')

		testQuery(query_020(), stateObject_020());
	}

	@Test
	public void test_Query_021() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE EXISTS (SELECT p FROM e.phones p WHERE p.type = 'Cell')

		testQuery(query_021(), stateObject_021());
	}

	@Test
	public void test_Query_022() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE e.department IN (SELECT DISTINCT d
		//                        FROM Department d JOIN d.employees de JOIN de.projects p
		//                        WHERE p.name LIKE 'QA%')

		testQuery(query_022(), stateObject_022());
	}

	@Test
	public void test_Query_023() throws Exception {

		// SELECT p
		// FROM Phone p
		// WHERE p.type NOT IN ('Office', 'Home')

		testQuery(query_023(), stateObject_023());
	}

	@Test
	public void test_Query_024() throws Exception {

		// SELECT m
		// FROM Employee m
		// WHERE (SELECT COUNT(e)
		//        FROM Employee e
		//        WHERE e.manager = m) > 0

		testQuery(query_024(), stateObject_024());
	}

	@Test
	public void test_Query_025() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE e MEMBER OF e.directs

		testQuery(query_025(), stateObject_025());
	}

	@Test
	public void test_Query_026() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE NOT EXISTS (SELECT p
		//                   FROM e.phones p
		//                   WHERE p.type = 'Cell')

		testQuery(query_026(), stateObject_026());
	}

	@Test
	public void test_Query_027() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE e.directs IS NOT EMPTY AND
		//       e.salary < ALL (SELECT d.salary FROM e.directs d)

		testQuery(query_027(), stateObject_027());
	}

	@Test
	public void test_Query_028() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE e.department = ANY (SELECT DISTINCT d FROM Department d JOIN d.employees de JOIN de.projects p
		//                           WHERE p.name LIKE 'QA%')

		testQuery(query_028(), stateObject_028());
	}

	@Test
	public void test_Query_029() throws Exception {

		// SELECT d
		// FROM Department d
		// WHERE SIZE(d.employees) = 2

		testQuery(query_029(), stateObject_029());
	}

	@Test
	public void test_Query_030() throws Exception {

		// SELECT d
      // FROM Department d
      // WHERE (SELECT COUNT(e)
      //        FROM d.employees e) = 2

		testQuery(query_030(), stateObject_030());
	}

	@Test
	public void test_Query_031() throws Exception {

		// SELECT e
		// FROM Employee e
		// ORDER BY e.name DESC

		testQuery(query_031(), stateObject_031());
	}

	@Test
	public void test_Query_032() throws Exception {

		// SELECT e
      // FROM Employee e JOIN e.department d
      // ORDER BY d.name, e.name DESC

		testQuery(query_032(), stateObject_032());
	}

	@Test
	public void test_Query_033() throws Exception {
		// SELECT AVG(e.salary) FROM Employee e
		testQuery(query_033(), stateObject_033());
	}

	@Test
	public void test_Query_034() throws Exception {

		// SELECT d.name, AVG(e.salary)
		// FROM Department d JOIN d.employees e
		// GROUP BY d.name

		testQuery(query_034(), stateObject_034());
	}

	@Test
	public void test_Query_035() throws Exception {

		// SELECT d.name, AVG(e.salary)
      // FROM Department d JOIN d.employees e
      // WHERE e.directs IS EMPTY
      // GROUP BY d.name

		testQuery(query_035(), stateObject_035());
	}

	@Test
	public void test_Query_036() throws Exception {

		// SELECT d.name, AVG(e.salary)
      // FROM Department d JOIN d.employees e
      // WHERE e.directs IS EMPTY
      // GROUP BY d.name
      // HAVING AVG(e.salary) > 50000

		testQuery(query_036(), stateObject_036());
	}

	@Test
	public void test_Query_037() throws Exception {

		// SELECT e, COUNT(p), COUNT(DISTINCT p.type)
		// FROM Employee e JOIN e.phones p
		// GROUP BY e

		testQuery(query_037(), stateObject_037());
	}

	@Test
	public void test_Query_038() throws Exception {

		// SELECT d.name, e.salary, COUNT(p)
		// FROM Department d JOIN d.employees e JOIN e.projects p
		// GROUP BY d.name, e.salary

		testQuery(query_038(), stateObject_038());
	}

	@Test
	public void test_Query_039() throws Exception {

		// SELECT e, COUNT(p)
		// FROM Employee e JOIN e.projects p
		// GROUP BY e
		// HAVING COUNT(p) >= 2

		testQuery(query_039(), stateObject_039());
	}

	@Test
	public void test_Query_040() throws Exception {

		// UPDATE Employee e
		// SET e.salary = 60000
		// WHERE e.salary = 55000

		testQuery(query_040(), stateObject_040());
	}

	@Test
	public void test_Query_041() throws Exception {

		// UPDATE Employee e
		// SET e.salary = e.salary + 5000
		// WHERE EXISTS (SELECT p
		//               FROM e.projects p
		//               WHERE p.name = 'Release1')

		testQuery(query_041(), stateObject_041());
	}

	@Test
	public void test_Query_042() throws Exception {

		// UPDATE Phone p
		// SET p.number = CONCAT('288', SUBSTRING(p.number, LOCATE(p.number, '-'), 4)),
		//     p.type = 'Business'
		// WHERE p.employee.address.city = 'New York' AND p.type = 'Office'

		testQuery(query_042(), stateObject_042());
	}

	@Test
	public void test_Query_043() throws Exception {

		// DELETE FROM Employee e
		// WHERE e.department IS NULL";

		testQuery(query_043(), stateObject_043());
	}

	@Test
	public void test_Query_044() throws Exception {

		// Select Distinct object(c)
		// From Customer c, In(c.orders) co
		// Where co.totalPrice >= Some (Select o.totalPrice
		//                              From Order o, In(o.lineItems) l
		//                              Where l.quantity = 3)

		testQuery(query_044(), stateObject_044());
	}

	@Test
	public void test_Query_045() throws Exception {

		// SELECT DISTINCT object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice <= SOME (Select o.totalPrice
		//                              FROM Order o, IN(o.lineItems) l
		//                              WHERE l.quantity = 3)

		testQuery(query_045(), stateObject_045());
	}

	@Test
	public void test_Query_046() throws Exception {

		// SELECT Distinct object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice = ANY (Select MAX(o.totalPrice) FROM Order o)

		testQuery(query_046(), stateObject_046());
	}

	@Test
	public void test_Query_047() throws Exception {

		// SELECT Distinct object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice < ANY (Select o.totalPrice
		//                            FROM Order o, IN(o.lineItems) l
		//                            WHERE l.quantity = 3)

		testQuery(query_047(), stateObject_047());
	}

	@Test
	public void test_Query_048() throws Exception {

		// SELECT Distinct object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice > ANY (Select o.totalPrice
		//                            FROM Order o, IN(o.lineItems) l
		//                            WHERE l.quantity = 3)

		testQuery(query_048(), stateObject_048());
	}

	@Test
	public void test_Query_049() throws Exception {

		// SELECT Distinct object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice <> ALL (Select MIN(o.totalPrice) FROM Order o)

		testQuery(query_049(), stateObject_049());
	}

	@Test
	public void test_Query_050() throws Exception {

		// SELECT Distinct object(c)
      // FROM Customer c, IN(c.orders) co
      // WHERE co.totalPrice >= ALL (Select o.totalPrice
		//                             FROM Order o, IN(o.lineItems) l
		//                             WHERE l.quantity >= 3)

		testQuery(query_050(), stateObject_050());
	}

	@Test
	public void test_Query_051() throws Exception {

		// SELECT Distinct object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice <= ALL (Select o.totalPrice
		//                             FROM Order o, IN(o.lineItems) l
		//                             WHERE l.quantity > 3)

		testQuery(query_051(), stateObject_051());
	}

	@Test
	public void test_Query_052() throws Exception {

		// SELECT DISTINCT object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice = ALL (Select MIN(o.totalPrice) FROM Order o)

		testQuery(query_052(), stateObject_052());
	}

	@Test
	public void test_Query_053() throws Exception {

		// SELECT DISTINCT object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice < ALL (Select o.totalPrice
		//                            FROM Order o, IN(o.lineItems) l
		//                            WHERE l.quantity > 3)

		testQuery(query_053(), stateObject_053());
	}

	@Test
	public void test_Query_054() throws Exception {

		// SELECT DISTINCT object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice > ALL (Select o.totalPrice
		//                            FROM Order o, IN(o.lineItems) l
		//                            WHERE l.quantity > 3)

		testQuery(query_054(), stateObject_054());
	}

	@Test
	public void test_Query_055() throws Exception {

		// SELECT DISTINCT c
		// FROM Customer c JOIN c.orders o
		// WHERE EXISTS (SELECT l
		//               FROM o.lineItems l
		//               where l.quantity > 3)

		testQuery(query_055(), stateObject_055());
	}

	@Test
	public void test_Query_056() throws Exception {

		// SELECT DISTINCT c
		// FROM Customer c JOIN c.orders o
		// WHERE EXISTS (SELECT o
		//               FROM c.orders o
		//               where o.totalPrice BETWEEN 1000 AND 1200)

		testQuery(query_056(), stateObject_056());
	}

	@Test
	public void test_Query_057() throws Exception {

		// SELECT DISTINCT c
		// from Customer c
		// WHERE c.home.state IN(Select distinct w.state
		//                       from c.work w
		//                       where w.state = :state)

		testQuery(query_057(), stateObject_057());
	}

	@Test
	public void test_Query_058() throws Exception {

		// Select Object(o)
		// from Order o
		// WHERE EXISTS (Select c
		//               From o.customer c
		//               WHERE c.name LIKE '%Caruso')

		testQuery(query_058(), stateObject_058());
	}

	@Test
	public void test_Query_059() throws Exception {

		// SELECT DISTINCT c
		// FROM Customer c
		// WHERE EXISTS (SELECT o
		//               FROM c.orders o
		//               where o.totalPrice > 1500)

		testQuery(query_059(), stateObject_059());
	}

	@Test
	public void test_Query_060() throws Exception {

		// SELECT c
		// FROM Customer c
		// WHERE NOT EXISTS (SELECT o1 FROM c.orders o1)

		testQuery(query_060(), stateObject_060());
	}

	@Test
	public void test_Query_061() throws Exception {

		// select object(o)
		// FROM Order o
		// Where SQRT(o.totalPrice) > :doubleValue

		testQuery(query_061(), stateObject_061());
	}

	@Test
	public void test_Query_062() throws Exception {

		// select sum(o.totalPrice)
		// FROM Order o
		// GROUP BY o.totalPrice
		// HAVING ABS(o.totalPrice) = :doubleValue

		testQuery(query_062(), stateObject_062());
	}

	@Test
	public void test_Query_063() throws Exception {

		// select c.name
		// FROM Customer c
		// Group By c.name
		// HAVING trim(TRAILING from c.name) = ' David R. Vincent'

		testQuery(query_063(), stateObject_063());
	}

	@Test
	public void test_Query_064() throws Exception {

		// select c.name
		// FROM  Customer c
		// Group By c.name
		// Having trim(LEADING from c.name) = 'David R. Vincent '

		testQuery(query_064(), stateObject_064());
	}

	@Test
	public void test_Query_065() throws Exception {

		// select c.name
		// FROM  Customer c
		// Group by c.name
		// HAVING trim(BOTH from c.name) = 'David R. Vincent'

		testQuery(query_065(), stateObject_065());
	}

	@Test
	public void test_Query_066() throws Exception {

		// select c.name
		// FROM  Customer c
		// GROUP BY c.name
		// HAVING LOCATE('Frechette', c.name) > 0

		testQuery(query_066(), stateObject_066());
	}

	@Test
	public void test_Query_067() throws Exception {

		// select a.city
		// FROM  Customer c JOIN c.home a
		// GROUP BY a.city
		// HAVING LENGTH(a.city) = 10

		testQuery(query_067(), stateObject_067());
	}

	@Test
	public void test_Query_068() throws Exception {

		// select count(cc.country)
		// FROM  Customer c JOIN c.country cc
		// GROUP BY cc.country
		// HAVING UPPER(cc.country) = 'ENGLAND'

		testQuery(query_068(), stateObject_068());
	}

	@Test
	public void test_Query_069() throws Exception {

		// select count(cc.country)
		// FROM  Customer c JOIN c.country cc
		// GROUP BY cc.code
		// HAVING LOWER(cc.code) = 'gbr'

		testQuery(query_069(), stateObject_069());
	}

	@Test
	public void test_Query_070() throws Exception {

		// select c.name
		// FROM  Customer c
		// Group By c.name
		// HAVING c.name = concat(:fmname, :lname)

		testQuery(query_070(), stateObject_070());
	}

	@Test
	public void test_Query_071() throws Exception {

		// select count(c)
		// FROM  Customer c JOIN c.aliases a
		// GROUP BY a.alias
		// HAVING a.alias = SUBSTRING(:string1, :int1, :int2)

		testQuery(query_071(), stateObject_071());
	}

	@Test
	public void test_Query_072() throws Exception {

		// select c.country.country
		// FROM  Customer c
		// GROUP BY c.country.country

		testQuery(query_072(), stateObject_072());
	}

	@Test
	public void test_Query_073() throws Exception {

		// select Count(c)
		// FROM  Customer c JOIN c.country cc
		// GROUP BY cc.code
		// HAVING cc.code IN ('GBR', 'CHA')

		testQuery(query_073(), stateObject_073());
	}

	@Test
	public void test_Query_074() throws Exception {

		// select c.name
		// FROM  Customer c JOIN c.orders o
		// WHERE o.totalPrice BETWEEN 90 AND 160
		// GROUP BY c.name

		testQuery(query_074(), stateObject_074());
	}

	@Test
	public void test_Query_075() throws Exception {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.id = '1001' OR o.totalPrice > 10000

		testQuery(query_075(), stateObject_075());
	}

	@Test
	public void test_Query_076() throws Exception {

		// select Distinct Object(o)
		// FROM Order AS o
		// WHERE o.customer.id = '1001' OR o.totalPrice < 1000

		testQuery(query_076(), stateObject_076());
	}

	@Test
	public void test_Query_077() throws Exception {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice > 10000

		testQuery(query_077(), stateObject_077());
	}

	@Test
	public void test_Query_078() throws Exception {

		// select DISTINCT o
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice > 5000

		testQuery(query_078(), stateObject_078());
	}

	@Test
	public void test_Query_079() throws Exception {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.id = '1001' AND o.totalPrice > 10000

		testQuery(query_079(), stateObject_079());
	}

	@Test
	public void test_Query_080() throws Exception {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.id = '1001' AND o.totalPrice < 1000

		testQuery(query_080(), stateObject_080());
	}

	@Test
	public void test_Query_081() throws Exception {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' AND o.totalPrice > 10000

		testQuery(query_081(), stateObject_081());
	}

	@Test
	public void test_Query_082() throws Exception {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' AND o.totalPrice > 500

		testQuery(query_082(), stateObject_082());
	}

	@Test
	public void test_Query_083() throws Exception {

		// SELECT DISTINCT p
		// From Product p
		// where p.shelfLife.soldDate NOT BETWEEN :date1 AND :newdate

		testQuery(query_083(), stateObject_083());
	}

	@Test
	public void test_Query_084() throws Exception {

		// SELECT DISTINCT o
		// From Order o
		// where o.totalPrice NOT BETWEEN 1000 AND 1200

		testQuery(query_084(), stateObject_084());
	}

	@Test
	public void test_Query_085() throws Exception {

		// SELECT DISTINCT p
		// From Product p
		// where p.shelfLife.soldDate BETWEEN :date1 AND :date6

		testQuery(query_085(), stateObject_085());
	}

	@Test
	public void test_Query_086() throws Exception {

		// SELECT DISTINCT a
		// from Alias a LEFT JOIN FETCH a.customers
		// where a.alias LIKE 'a%'

		testQuery(query_086(), stateObject_086());
	}

	@Test
	public void test_Query_087() throws Exception {

		// select Object(o)
		// from Order o LEFT JOIN FETCH o.customer
		// where o.customer.name LIKE '%Caruso'

		testQuery(query_087(), stateObject_087());
	}

	@Test
	public void test_Query_088() throws Exception {

		// select o
		// from Order o LEFT JOIN FETCH o.customer
		// where o.customer.home.city='Lawrence'

		testQuery(query_088(), stateObject_088());
	}

	@Test
	public void test_Query_089() throws Exception {

		// SELECT DISTINCT c
		// from Customer c LEFT JOIN FETCH c.orders
		// where c.home.state IN('NY','RI')

		testQuery(query_089(), stateObject_089());
	}

	@Test
	public void test_Query_090() throws Exception {

		// SELECT c
		// from Customer c JOIN FETCH c.spouse

		testQuery(query_090(), stateObject_090());
	}

	@Test
	public void test_Query_091() throws Exception {

		// SELECT Object(c)
		// from Customer c INNER JOIN c.aliases a
		// where a.alias = :aName

		testQuery(query_091(), stateObject_091());
	}

	@Test
	public void test_Query_092() throws Exception {

		// SELECT Object(o)
		// from Order o INNER JOIN o.customer cust
		// where cust.name = ?1

		testQuery(query_092(), stateObject_092());
	}

	@Test
	public void test_Query_093() throws Exception {

		// SELECT DISTINCT object(c)
		// from Customer c INNER JOIN c.creditCards cc
		// where cc.type='VISA'

		testQuery(query_093(), stateObject_093());
	}

	@Test
	public void test_Query_094() throws Exception {

		// SELECT c
		// from Customer c INNER JOIN c.spouse s

		testQuery(query_094(), stateObject_094());
	}

	@Test
	public void test_Query_095() throws Exception {

		// select cc.type
		// FROM CreditCard cc JOIN cc.customer cust
		// GROUP BY cc.type

		testQuery(query_095(), stateObject_095());
	}

	@Test
	public void test_Query_096() throws Exception {

		// select cc.code
		// FROM Customer c JOIN c.country cc
		// GROUP BY cc.code

		testQuery(query_096(), stateObject_096());
	}

	@Test
	public void test_Query_097() throws Exception {

		// select Object(c)
		// FROM Customer c JOIN c.aliases a
		// where LOWER(a.alias)='sjc'

		testQuery(query_097(), stateObject_097());
	}

	@Test
	public void test_Query_098() throws Exception {

		// select Object(c)
		// FROM Customer c JOIN c.aliases a
		// where UPPER(a.alias)='SJC'

		testQuery(query_098(), stateObject_098());
	}

	@Test
	public void test_Query_099() throws Exception {

		// SELECT c.id, a.alias
		// from Customer c LEFT OUTER JOIN c.aliases a
		// where c.name LIKE 'Ste%'
		// ORDER BY a.alias, c.id

		testQuery(query_099(), stateObject_099());
	}

	@Test
	public void test_Query_100() throws Exception {

		// SELECT o.id, cust.id
		// from Order o LEFT OUTER JOIN o.customer cust
		// where cust.name=?1
		// ORDER BY o.id

		testQuery(query_100(), stateObject_100());
	}

	@Test
	public void test_Query_101() throws Exception {

		// SELECT DISTINCT c
		// from Customer c LEFT OUTER JOIN c.creditCards cc
		// where c.name LIKE '%Caruso'

		testQuery(query_101(), stateObject_101());
	}

	@Test
	public void test_Query_102() throws Exception {

		// SELECT Sum(p.quantity)
		// FROM Product p

		testQuery(query_102(), stateObject_102());
	}

	@Test
	public void test_Query_103() throws Exception {

		// Select Count(c.home.city)
		// from Customer c

		testQuery(query_103(), stateObject_103());
	}

	@Test
	public void test_Query_104() throws Exception {

		// SELECT Sum(p.price)
		// FROM Product p

		testQuery(query_104(), stateObject_104());
	}

	@Test
	public void test_Query_105() throws Exception {

		// SELECT AVG(o.totalPrice)
		// FROM Order o

		testQuery(query_105(), stateObject_105());
	}

	@Test
	public void test_Query_106() throws Exception {

		// SELECT DISTINCT MAX(l.quantity)
		// FROM LineItem l

		testQuery(query_106(), stateObject_106());
	}

	@Test
	public void test_Query_107() throws Exception {

		// SELECT DISTINCT MIN(o.id)
		// FROM Order o
		// where o.customer.name = 'Robert E. Bissett'

		testQuery(query_107(), stateObject_107());
	}

	@Test
	public void test_Query_108() throws Exception {

		// SELECT NEW com.sun.ts.tests.ejb30.persistence.query.language.schema30.Customer(c.id, c.name)
		// FROM Customer c
		// where c.work.city = :workcity

		testQuery(query_108(), stateObject_108());
	}

	@Test
	public void test_Query_109() throws Exception {

		// SELECT DISTINCT c
		// FROM Customer c
		// WHERE SIZE(c.orders) > 100

		testQuery(query_109(), stateObject_109());
	}

	@Test
	public void test_Query_110() throws Exception {

		// SELECT DISTINCT c
		// FROM Customer c
		// WHERE SIZE(c.orders) >= 2

		testQuery(query_110(), stateObject_110());
	}

	@Test
	public void test_Query_111() throws Exception {

		// select Distinct c
		// FROM Customer c LEFT OUTER JOIN c.work workAddress
		// where workAddress.zip IS NULL

		testQuery(query_111(), stateObject_111());
	}

	@Test
	public void test_Query_112() throws Exception {

		// SELECT DISTINCT c
		// FROM Customer c, IN(c.orders) o

		testQuery(query_112(), stateObject_112());
	}

	@Test
	public void test_Query_113() throws Exception {

		// Select Distinct Object(c)
		// from Customer c
		// where c.name is null

		testQuery(query_113(), stateObject_113());
	}

	@Test
	public void test_Query_114() throws Exception {

		// Select c.name
		// from Customer c
		// where c.home.street = '212 Edgewood Drive'

		testQuery(query_114(), stateObject_114());
	}

	@Test
	public void test_Query_115() throws Exception {

		// Select s.customer
		// from Spouse s
		// where s.id = '6'

		testQuery(query_115(), stateObject_115());
	}

	@Test
	public void test_Query_116() throws Exception {

		// Select c.work.zip
		// from Customer c

		testQuery(query_116(), stateObject_116());
	}

	@Test
	public void test_Query_117() throws Exception {

		// SELECT Distinct Object(c)
		// From Customer c, IN(c.home.phones) p
		// where p.area LIKE :area

		testQuery(query_117(), stateObject_117());
	}

	@Test
	public void test_Query_118() throws Exception {

		// SELECT DISTINCT Object(c)
		// from Customer c, in(c.aliases) a
		// where NOT a.customerNoop IS NULL

		testQuery(query_118(), stateObject_118());
	}

	@Test
	public void test_Query_119() throws Exception {

		// select distinct object(c)
		// fRoM Customer c, IN(c.aliases) a
		// where c.name = :cName OR a.customerNoop IS NULL

		testQuery(query_119(), stateObject_119());
	}

	@Test
	public void test_Query_120() throws Exception {

		// select Distinct Object(c)
		// from Customer c, in(c.aliases) a
		// where c.name = :cName AND a.customerNoop IS NULL

		testQuery(query_120(), stateObject_120());
	}

	@Test
	public void test_Query_121() throws Exception {

		// sElEcT Distinct oBJeCt(c)
		// FROM Customer c, IN(c.aliases) a
		// WHERE a.customerNoop IS NOT NULL

		testQuery(query_121(), stateObject_121());
	}

	@Test
	public void test_Query_122() throws Exception {

		// select distinct Object(c)
		// FROM Customer c, in(c.aliases) a
		// WHERE a.alias LIKE '%\\_%' escape '\\'

		testQuery(query_122(), stateObject_122());
	}

	@Test
	public void test_Query_123() throws Exception {

		// Select Distinct Object(c)
		// FROM Customer c, in(c.aliases) a
		// WHERE a.customerNoop IS NULL

		testQuery(query_123(), stateObject_123());
	}

	@Test
	public void test_Query_124() throws Exception {

		// Select Distinct o.creditCard.balance
		// from Order o
		// ORDER BY o.creditCard.balance ASC

		testQuery(query_124(), stateObject_124());
	}

	@Test
	public void test_Query_125() throws Exception {

		// Select c.work.zip
		// from Customer c
		// where c.work.zip IS NOT NULL
		// ORDER BY c.work.zip ASC

		testQuery(query_125(), stateObject_125());
	}

	@Test
	public void test_Query_126() throws Exception {

		// SELECT a.alias
		// FROM Alias AS a
		// WHERE (a.alias IS NULL AND :param1 IS NULL) OR a.alias = :param1

		testQuery(query_126(), stateObject_126());
	}

	@Test
	public void test_Query_127() throws Exception {

		// Select Object(c)
		// from Customer c
		// where c.aliasesNoop IS NOT EMPTY or c.id <> '1'

		testQuery(query_127(), stateObject_127());
	}

	@Test
	public void test_Query_128() throws Exception {

		// Select Distinct Object(p)
		// from Product p
		// where p.name = ?1

		testQuery(query_128(), stateObject_128());
	}

	@Test
	public void test_Query_129() throws Exception {

		// Select Distinct Object(p)
		// from Product p
		// where (p.quantity > (500 + :int1)) AND (p.partNumber IS NULL)

		testQuery(query_129(), stateObject_129());
	}

	@Test
	public void test_Query_130() throws Exception {

		// Select Distinct Object(o)
		// from Order o
		// where o.customer.name IS NOT NULL

		testQuery(query_130(), stateObject_130());
	}

	@Test
	public void test_Query_131() throws Exception {

		// Select DISTINCT Object(p)
		// From Product p
		// where (p.quantity < 10) OR (p.quantity > 20)

		testQuery(query_131(), stateObject_131());
	}

	@Test
	public void test_Query_132() throws Exception {

		// Select DISTINCT Object(p)
		// From Product p
		// where p.quantity NOT BETWEEN 10 AND 20

		testQuery(query_132(), stateObject_132());
	}

	@Test
	public void test_Query_133() throws Exception {

		// Select DISTINCT OBJECT(p)
		// From Product p
		// where (p.quantity >= 10) AND (p.quantity <= 20)

		testQuery(query_133(), stateObject_133());
	}

	@Test
	public void test_Query_134() throws Exception {

		// Select DISTINCT OBJECT(p)
		// From Product p
		// where p.quantity BETWEEN 10 AND 20

		testQuery(query_134(), stateObject_134());
	}

	@Test
	public void test_Query_135() throws Exception {

		// Select Distinct OBJECT(c)
		// from Customer c, IN(c.creditCards) b
		// where SQRT(b.balance) = :dbl

		testQuery(query_135(), stateObject_135());
	}

	@Test
	public void test_Query_136() throws Exception {

		// Select Distinct OBJECT(c)
		// From Product p
		// where MOD(550, 100) = p.quantity

		testQuery(query_136(), stateObject_136());
	}

	@Test
	public void test_Query_137() throws Exception {

		// SELECT DISTINCT Object(c)
		// from Customer c
		// WHERE (c.home.state = 'NH') OR (c.home.state = 'RI')

		testQuery(query_137(), stateObject_137());
	}

	@Test
	public void test_Query_138() throws Exception {

		// SELECT DISTINCT Object(c)
		// from Customer c
		// where c.home.state IN('NH', 'RI')

		testQuery(query_138(), stateObject_138());
	}

	@Test
	public void test_Query_140() throws Exception {

		// SELECT c
		// from Customer c
		// where c.home.city IN(:city)

		testQuery(query_140(), stateObject_140());
	}

	@Test
	public void test_Query_141() throws Exception {

		// Select Distinct Object(o)
		// from Order o, in(o.lineItems) l
		// where l.quantity NOT IN (1, 5)

		testQuery(query_141(), stateObject_141());
	}

	@Test
	public void test_Query_142() throws Exception {

		// Select Distinct Object(o)
		// FROM Order o
		// WHERE o.sampleLineItem MEMBER OF o.lineItems

		testQuery(query_142(), stateObject_142());
	}

	@Test
	public void test_Query_143() throws Exception {

		// Select Distinct Object(o)
		// FROM Order o
		// WHERE :param NOT MEMBER o.lineItems

		testQuery(query_143(), stateObject_143());
	}

	@Test
	public void test_Query_144() throws Exception {

		// Select Distinct Object(o)
		// FROM Order o, LineItem l
		// WHERE l MEMBER o.lineItems

		testQuery(query_144(), stateObject_144());
	}

	@Test
	public void test_Query_145() throws Exception {

		// select distinct Object(c)
		// FROM Customer c, in(c.aliases) a
		// WHERE a.alias LIKE 'sh\\_ll' escape '\\'

		testQuery(query_145(), stateObject_145());
	}

	@Test
	public void test_Query_146() throws Exception {

		// Select Distinct Object(a)
		// FROM Alias a
		// WHERE a.customerNoop NOT MEMBER OF a.customersNoop

		testQuery(query_146(), stateObject_146());
	}

	@Test
	public void test_Query_147() throws Exception {

		// Select Distinct Object(a)
		// FROM Alias a
		// WHERE a.customerNoop MEMBER OF a.customersNoop

		testQuery(query_147(), stateObject_147());
	}

	@Test
	public void test_Query_148() throws Exception {

		// Select Distinct Object(a)
		// from Alias a
		// where LOCATE('ev', a.alias) = 3

		testQuery(query_148(), stateObject_148());
	}

	@Test
	public void test_Query_149() throws Exception {

		// Select DISTINCT Object(o)
		// From Order o
		// WHERE o.totalPrice > ABS(:dbl)

		testQuery(query_149(), stateObject_149());
	}

	@Test
	public void test_Query_150() throws Exception {

		// Select Distinct OBjeCt(a)
		// From Alias a
		// WHERE LENGTH(a.alias) > 4

		testQuery(query_150(), stateObject_150());
	}

	@Test
	public void test_Query_151() throws Exception {

		// Select Distinct Object(a)
		// From Alias a
		// WHERE a.alias = SUBSTRING(:string1, :int2, :int3)

		testQuery(query_151(), stateObject_151());
	}

	@Test
	public void test_Query_152() throws Exception {

		// Select Distinct Object(a)
		// From Alias a
		// WHERE a.alias = CONCAT('ste', 'vie')

		testQuery(query_152(), stateObject_152());
	}

	@Test
	public void test_Query_153() throws Exception {

		// Select Distinct Object(c)
		// FROM Customer c
		// WHERE c.work.zip IS NOT NULL

		testQuery(query_153(), stateObject_153());
	}

	@Test
	public void test_Query_154() throws Exception {

		// sELEct dIsTiNcT oBjEcT(c)
		// FROM Customer c
		// WHERE c.work.zip IS NULL

		testQuery(query_154(), stateObject_154());
	}

	@Test
	public void test_Query_155() throws Exception {

		// Select Distinct Object(c)
		// FROM Customer c
		// WHERE c.aliases IS NOT EMPTY

		testQuery(query_155(), stateObject_155());
	}

	@Test
	public void test_Query_156() throws Exception {

		// Select Distinct Object(c)
		// FROM Customer c
		// WHERE c.aliases IS EMPTY

		testQuery(query_156(), stateObject_156());
	}

	@Test
	public void test_Query_157() throws Exception {

		// Select Distinct Object(c)
		// FROM Customer c
		// WHERE c.home.zip not like '%44_'

		testQuery(query_157(), stateObject_157());
	}

	@Test
	public void test_Query_158() throws Exception {

		// Select Distinct Object(c)
		// FROM Customer c
		// WHERE c.home.zip LIKE '%77'"

		testQuery(query_158(), stateObject_158());
	}

	@Test
	public void test_Query_159() throws Exception {

		// Select Distinct Object(c)
		// FROM Customer c Left Outer Join c.home h
		// WHERE h.city Not iN ('Swansea', 'Brookline')

		testQuery(query_159(), stateObject_159());
	}

	@Test
	public void test_Query_160() throws Exception {

		// select distinct c
		// FROM Customer c
		// WHERE c.home.city IN ('Lexington')

		testQuery(query_160(), stateObject_160());
	}

	@Test
	public void test_Query_161() throws Exception {

		// sElEcT c
		// FROM Customer c
		// Where c.name = :cName

		testQuery(query_161(), stateObject_161());
	}

	@Test
	public void test_Query_162() throws Exception {

		// select distinct Object(o)
		// From Order o
		// WHERE o.creditCard.approved = FALSE

		testQuery(query_162(), stateObject_162());
	}

	@Test
	public void test_Query_163() throws Exception {

		// SELECT DISTINCT Object(o)
		// From Order o
		// where o.totalPrice NOT bETwEeN 1000 AND 1200

		testQuery(query_163(), stateObject_163());
	}

	@Test
	public void test_Query_164() throws Exception {

		// SELECT DISTINCT Object(o)
		// From Order o
		// where o.totalPrice BETWEEN 1000 AND 1200

		testQuery(query_164(), stateObject_164());
	}

	@Test
	public void test_Query_165() throws Exception {

		// SELECT DISTINCT Object(o)
		// FROM Order o, in(o.lineItems) l
		// WHERE l.quantity < 2 AND o.customer.name = 'Robert E. Bissett'

		testQuery(query_165(), stateObject_165());
	}

	@Test
	public void test_Query_166() throws Exception {

		// select distinct Object(o)
		// FROM Order AS o, in(o.lineItems) l
		// WHERE (l.quantity < 2) AND ((o.totalPrice < (3 + 54 * 2 + -8)) OR (o.customer.name = 'Robert E. Bissett'))

		testQuery(query_166(), stateObject_166());
	}

	@Test
	public void test_Query_167() throws Exception {

		// SeLeCt DiStInCt oBjEcT(o)
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice < 100

		testQuery(query_167(), stateObject_167());
	}

	@Test
	public void test_Query_168() throws Exception {

		// Select Distinct Object(o)
		// FROM Order o
		// WHERE NOT o.totalPrice < 4500

		testQuery(query_168(), stateObject_168());
	}

	@Test
	public void test_Query_169() throws Exception {

		// Select DISTINCT Object(P)
		// From Product p

		testQuery(query_169(), stateObject_169());
	}

	@Test
	public void test_Query_170() throws Exception {

		// SELECT DISTINCT c
		// from Customer c
		// WHERE c.home.street = :street OR c.home.city = :city OR c.home.state = :state or c.home.zip = :zip

		testQuery(query_170(), stateObject_170());
	}

	@Test
	public void test_Query_171() throws Exception {

		// SELECT c
		// from Customer c
		// WHERE c.home.street = :street AND c.home.city = :city AND c.home.state = :state and c.home.zip = :zip

		testQuery(query_172(), stateObject_172());
	}

	@Test
	public void test_Query_172() throws Exception {

		// SELECT c
		// from Customer c
      // WHERE c.home.street = :street AND c.home.city = :city AND c.home.state = :state and c.home.zip = :zip

		testQuery(query_172(), stateObject_172());
	}

	@Test
	public void test_Query_173() throws Exception {

		// Select Distinct Object(c)
		// FrOm Customer c, In(c.aliases) a
		// WHERE a.alias = :aName

		testQuery(query_173(), stateObject_173());
	}

	@Test
	public void test_Query_174() throws Exception {

		// Select Distinct Object(c)
		// FROM Customer AS c

		testQuery(query_174(), stateObject_174());
	}

	@Test
	public void test_Query_175() throws Exception {

		// Select Distinct o
		// from Order AS o
		// WHERE o.customer.name = :name

		testQuery(query_175(), stateObject_175());
	}

	@Test
	public void test_Query_176() throws Exception {

		// UPDATE Customer c SET c.name = 'CHANGED'
		// WHERE c.orders IS NOT EMPTY

		testQuery(query_176(), stateObject_176());
	}

	@Test
	public void test_Query_177() throws Exception {

		// UPDATE DateTime SET date = CURRENT_DATE

		testQuery(query_177(), stateObject_177());
	}

	@Test
	public void test_Query_178() throws Exception {

		// SELECT c
		// FROM Customer c
		// WHERE c.firstName = :first AND
		//     c.lastName = :last

		testQuery(query_178(), stateObject_178());
	}

	@Test
	public void test_Query_179() throws Exception {

		// SELECT OBJECT ( c ) FROM Customer AS c

		testQuery(query_179(), stateObject_179());
	}

	@Test
	public void test_Query_180() throws Exception {

		// SELECT c.firstName, c.lastName
		// FROM Customer AS c

		testQuery(query_180(), stateObject_180());
	}

	@Test
	public void test_Query_181() throws Exception {

		// SELECT c.address.city
		// FROM Customer AS c

		testQuery(query_181(), stateObject_181());
	}

	@Test
	public void test_Query_182() throws Exception {

		// SELECT new com.titan.domain.Name(c.firstName, c.lastName)
		// FROM Customer c

		testQuery(query_182(), stateObject_182());
	}

	@Test
	public void test_Query_183() throws Exception {

		// SELECT cbn.ship
		// FROM Customer AS c, IN ( c.reservations ) r, IN ( r.cabins ) cbn

		testQuery(query_183(), stateObject_183());
	}

	@Test
	public void test_Query_184() throws Exception {

		// Select c.firstName, c.lastName, p.number
		// From Customer c Left Join c.phoneNumbers p

		testQuery(query_184(), stateObject_184());
	}

	@Test
	public void test_Query_185() throws Exception {

		// SELECT r
		// FROM Reservation AS r
		// WHERE (r.amountPaid * .01) > 300.00

		testQuery(query_185(), stateObject_185());
	}

	@Test
	public void test_Query_186() throws Exception {

		// SELECT s
		// FROM Ship AS s
		// WHERE s.tonnage >= 80000.00 AND s.tonnage <= 130000.00

		testQuery(query_186(), stateObject_186());
	}

	@Test
	public void test_Query_187() throws Exception {

		// SELECT r
		// FROM Reservation r, IN ( r.customers ) AS cust
		// WHERE cust = :specificCustomer

		testQuery(query_187(), stateObject_187());
	}

	@Test
	public void test_Query_188() throws Exception {

		// SELECT s
		// FROM Ship AS s
		// WHERE s.tonnage BETWEEN 80000.00 AND 130000.00

		testQuery(query_188(), stateObject_188());
	}

	@Test
	public void test_Query_189() throws Exception {

		// SELECT s
		// FROM Ship AS s
		// WHERE s.tonnage NOT BETWEEN 80000.00 AND 130000.00

		testQuery(query_189(), stateObject_189());
	}

	@Test
	public void test_Query_190() throws Exception {

		// SELECT c
		// FROM Customer AS c
		//  WHERE c.address.state IN ('FL', 'TX', 'MI', 'WI', 'MN')

		testQuery(query_190(), stateObject_190());
	}

	@Test
	public void test_Query_191() throws Exception {

		// SELECT cab
		// FROM Cabin AS cab
		// WHERE cab.deckLevel IN (1,3,5,7)

		testQuery(query_191(), stateObject_191());
	}

	@Test
	public void test_Query_192() throws Exception {

		// SELECT c
		// FROM Customer c
		// WHERE c.address.state IN(?1, ?2, ?3, 'WI', 'MN')

		testQuery(query_192(), stateObject_192());
	}

	@Test
	public void test_Query_193() throws Exception {

		// SELECT c
		// FROM Customer c
		// WHERE c.address IS NULL

		testQuery(query_193(), stateObject_193());
	}

	@Test
	public void test_Query_194() throws Exception {

		// SELECT c
		// FROM Customer c
		// WHERE c.address.state = 'TX' AND
		//       c.lastName = 'Smith' AND
		//       c.firstName = 'John'

		testQuery(query_194(), stateObject_194());
	}

	@Test
	public void test_Query_195() throws Exception {

		// SELECT crs
		// FROM Cruise AS crs, IN(crs.reservations) AS res, Customer AS cust
		// WHERE
		//  cust = :myCustomer
		//  AND
		//  cust MEMBER OF res.customers

		testQuery(query_195(), stateObject_195());
	}

	@Test
	public void test_Query_196() throws Exception {

		// SELECT c
		// FROM Customer AS c
		// WHERE    LENGTH(c.lastName) > 6
		//       AND
		//          LOCATE( c.lastName, 'Monson' ) > -1

		testQuery(query_196(), stateObject_196());
	}

	@Test
	public void test_Query_197() throws Exception {

		// SELECT c
		// FROM Customer AS C
		// ORDER BY c.lastName

		testQuery(query_197(), stateObject_197());
	}

	@Test
	public void test_Query_198() throws Exception {

		// SELECT c
		// FROM Customer AS C
      // WHERE c.address.city = 'Boston' AND c.address.state = 'MA'
		// ORDER BY c.lastName DESC

		testQuery(query_198(), stateObject_198());
	}

	@Test
	public void test_Query_199() throws Exception {

		// SELECT cr.name, COUNT (res)
		// FROM Cruise cr LEFT JOIN cr.reservations res
		// GROUP BY cr.name

		testQuery(query_199(), stateObject_199());
	}

	@Test
	public void test_Query_200() throws Exception {

		// SELECT cr.name, COUNT (res)
		// FROM Cruise cr LEFT JOIN cr.reservations res
		// GROUP BY cr.name
		// HAVING count(res) > 10

		testQuery(query_200(), stateObject_200());
	}

	@Test
	public void test_Query_201() throws Exception {

		// SELECT COUNT (res)
		// FROM Reservation res
		// WHERE res.amountPaid >
		//       (SELECT avg(r.amountPaid) FROM Reservation r)

		testQuery(query_201(), stateObject_201());
	}

	@Test
	public void test_Query_202() throws Exception {

		// SELECT cr
		// FROM Cruise cr
		// WHERE 100000 < (
		//    SELECT SUM(res.amountPaid) FROM cr.reservations res
		// )

		testQuery(query_202(), stateObject_202());
	}

	@Test
	public void test_Query_203() throws Exception {

		// SELECT cr
		// FROM Cruise cr
		// WHERE 0 < ALL (
		//   SELECT res.amountPaid from cr.reservations res
		// )

		testQuery(query_203(), stateObject_203());
	}

	@Test
	public void test_Query_204() throws Exception {

		// UPDATE Reservation res
		// SET res.name = 'Pascal'
		// WHERE EXISTS (
		//    SELECT c
		//    FROM res.customers c
		//    WHERE c.firstName = 'Bill' AND c.lastName='Burke'
		// )

		testQuery(query_204(), stateObject_204());
	}

	@Test
	public void test_Query_215() throws Exception {

		// SELECT o
		// FROM Customer c JOIN c.orders o JOIN c.address a
		// WHERE a.state = 'CA'
		// ORDER BY o.quantity DESC, o.totalcost

		testQuery(query_215(), stateObject_215());
	}

	@Test
	public void test_Query_216() throws Exception {

		// SELECT o.quantity, a.zipcode
		// FROM Customer c JOIN c.orders o JOIN c.address a
		// WHERE a.state = 'CA'
		// ORDER BY o.quantity, a.zipcode

		testQuery(query_216(), stateObject_216());
	}

	@Test
	public void test_Query_219() throws Exception {

		// DELETE
		// FROM Customer c
		// WHERE c.status = 'inactive'

		testQuery(query_219(), stateObject_219());
	}

	@Test
	public void test_Query_220() throws Exception {

		// DELETE
		// FROM Customer c
		// WHERE c.status = 'inactive'
		//       AND
		//       c.orders IS EMPTY

		testQuery(query_220(), stateObject_220());
	}

	@Test
	public void test_Query_221() throws Exception {

		// UPDATE customer c
		// SET c.status = 'outstanding'
		// WHERE c.balance < 10000

		testQuery(query_221(), stateObject_221());
	}

	@Test
	public void test_Query_228() throws Exception {

		// Select e
		// from Employee e join e.phoneNumbers p
		// where    e.firstName = 'Bob'
		//      and e.lastName like 'Smith%'
		//      and e.address.city = 'Toronto'
		//      and p.areaCode <> '2'

		testQuery(query_228(), stateObject_228());
	}

	@Test
	public void test_Query_229() throws Exception {

		// Select e
		// From Employee e
		// Where Exists(Select a From e.address a Where a.zipCode = 27519)

		testQuery(query_229(), stateObject_229());
	}

	@Test
	public void test_Query_230() throws Exception {

		// Select e
		// From Employee e
		// Where Exists(Where Exists(Select e.name From In e.phoneNumbers Where e.zipCode = 27519))

		testQuery(query_230(), stateObject_230());
	}
}