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
package org.eclipse.persistence.jpa.internal.jpql.parser;

@SuppressWarnings("nls")
final class JPQLQueries {

	private JPQLQueries() {
		super();
	}

	static String query_001() {
		return "SELECT e FROM Employee e";
	}

	static String query_002() {
		return "SELECT e\nFROM Employee e";
	}

	static String query_003() {
		return "SELECT e " +
		       "FROM Employee e " +
		       "WHERE e.department.name = 'NA42' AND " +
		       "      e.address.state IN ('NY', 'CA')";
	}

	static String query_004() {
		return "SELECT p.number " +
		       "FROM Employee e, Phone p " +
		       "WHERE e = p.employee AND " +
		       "      e.department.name = 'NA42' AND " +
		       "      p.type = 'Cell'";
	}

	static String query_005() {
		return "SELECT d, COUNT(e), MAX(e.salary), AVG(e.salary) " +
		       "FROM Department d JOIN d.employees e " +
		       "GROUP BY d " +
		       "HAVING COUNT(e) >= 5";
	}

	static String query_006() {
		return "SELECT e " +
		       "FROM Employee e " +
		       "WHERE e.department = ?1 AND " +
		       "      e.salary > ?2";
	}

	static String query_007() {
		return "SELECT e " +
		       "FROM Employee e " +
		       "WHERE e.department = :dept AND " +
		       "      e.salary > :base";
	}

	static String query_008() {
      return "SELECT e " +
		       "FROM Employee e " +
		       "WHERE e.department = 'NA65' AND " +
		       "      e.name = 'UNKNOWN'' OR e.name = ''Roberts'";
	}

	static String query_009() {
		return "SELECT e " +
		       "FROM Employee e " +
		       "WHERE e.startDate BETWEEN ?1 AND ?2";
	}

	static String query_010() {
		return "SELECT e " +
		       "FROM Employee e " +
		       "WHERE e.department = :dept AND " +
		       "      e.salary = (SELECT MAX(e.salary) " +
		       "                  FROM Employee e " +
		       "                  WHERE e.department = :dept)";
	}

	static String query_011() {
		return "SELECT e " +
		       "FROM Project p JOIN p.employees e " +
		       "WHERE p.name = ?1 " +
		       "ORDER BY e.name";
	}

	static String query_012() {
		return "SELECT e " +
		       "FROM Employee e " +
		       "WHERE e.projects IS EMPTY";
	}

	static String query_013() {
		return "SELECT e " +
		       "FROM Employee e " +
		       "WHERE e.projects IS NOT EMPTY";
	}

	static String query_014() {
		return "UPDATE Employee e " +
		       "SET e.manager = ?1 " +
		       "WHERE e.department = ?2";
	}

	static String query_015() {
		return "DELETE FROM Project p " +
		       "WHERE p.employees IS EMPTY";
	}

	static String query_016() {
		return "DELETE FROM Department d " +
		       "WHERE d.name IN ('CA13', 'CA19', 'NY30')";
	}

	static String query_017() {
		return "UPDATE Employee e " +
		       "SET e.department = null " +
		       "WHERE e.department.name IN ('CA13', 'CA19', 'NY30')";
	}

	static String query_018() {
		return "SELECT d " +
		       "FROM Department d " +
		       "WHERE d.name LIKE 'QA\\_%' ESCAPE '\\'";
	}

	static String query_019() {
		return "SELECT e " +
		       "FROM Employee e " +
		       "WHERE e.salary = (SELECT MAX(e2.salary) FROM Employee e2)";
	}

	static String query_020() {
		return "SELECT e " +
		       "FROM Employee e " +
		       "WHERE EXISTS (SELECT p FROM Phone p WHERE p.employee = e AND p.type = 'Cell')";
	}

	static String query_021() {
		return "SELECT e " +
		       "FROM Employee e " +
		       "WHERE EXISTS (SELECT p FROM e.phones p WHERE p.type = 'Cell')";
	}

	static String query_022() {
		return "SELECT e " +
		       "FROM Employee e " +
		       "WHERE e.department IN (SELECT DISTINCT d " +
		       "                       FROM Department d JOIN d.employees de JOIN de.projects p " +
		       "                       WHERE p.name LIKE 'QA%')";
	}

	static String query_023() {
		return "SELECT p " +
		       "FROM Phone p " +
		       "WHERE p.type NOT IN ('Office', 'Home')";
	}

	static String query_024() {
		return "SELECT m " +
		       "FROM Employee m " +
		       "WHERE (SELECT COUNT(e) " +
		       "       FROM Employee e " +
		       "       WHERE e.manager = m) > 0";
	}

	static String query_025() {
		return "SELECT e " +
		       "FROM Employee e " +
		       "WHERE e MEMBER OF e.directs";
	}

	static String query_026() {
		return "SELECT e " +
		       "FROM Employee e " +
		       "WHERE NOT EXISTS (SELECT p " +
		       "                  FROM e.phones p " +
		       "                  WHERE p.type = 'Cell')";
	}

	static String query_027() {
		return "SELECT e " +
		       "FROM Employee e " +
		       "WHERE e.directs IS NOT EMPTY AND " +
		       "      e.salary < ALL (SELECT d.salary " +
		       "                      FROM e.directs d)";
	}

	static String query_028() {
		return "SELECT e " +
		       "FROM Employee e " +
		       "WHERE e.department = ANY (SELECT DISTINCT d FROM Department d JOIN d.employees de JOIN de.projects p " +
		       "                          WHERE p.name LIKE 'QA%')";
	}

	static String query_029() {
		return "SELECT d " +
		       "FROM Department d " +
		       "WHERE SIZE(d.employees) = 2";
	}

	static String query_030() {
		return "SELECT d " +
		       "FROM Department d " +
		       "WHERE (SELECT COUNT(e) " +
		       "       FROM d.employees e) = 2";
	}

	static String query_031() {
		return "SELECT e " +
		       "FROM Employee e " +
		       "ORDER BY e.name DESC";
	}

	static String query_032() {
		return "SELECT e " +
		       "FROM Employee e JOIN e.department d " +
		       "ORDER BY d.name, e.name DESC";
	}

	static String query_033() {
		return "SELECT AVG(e.salary) " +
		       "FROM Employee e";
	}

	static String query_034() {
		return "SELECT d.name, AVG(e.salary) " +
		       "FROM Department d JOIN d.employees e " +
		       "GROUP BY d.name";
	}

	static String query_035() {
		return "SELECT d.name, AVG(e.salary) " +
		       "FROM Department d JOIN d.employees e " +
		       "WHERE e.directs IS EMPTY " +
		       "GROUP BY d.name";
	}

	static String query_036() {
		return "SELECT d.name, AVG(e.salary) " +
		       "FROM Department d JOIN d.employees e " +
		       "WHERE e.directs IS EMPTY " +
		       "GROUP BY d.name " +
		       "HAVING AVG(e.salary) > 50000";
	}

	static String query_037() {
		return "SELECT e, COUNT(p), COUNT(DISTINCT p.type) " +
		       "FROM Employee e JOIN e.phones p " +
		       "GROUP BY e";
	}

	static String query_038() {
		return "SELECT d.name, e.salary, COUNT(p) " +
		       "FROM Department d JOIN d.employees e JOIN e.projects p " +
		       "GROUP BY d.name, e.salary";
	}

	static String query_039() {
		return "SELECT e, COUNT(p) " +
		       "FROM Employee e JOIN e.projects p " +
		       "GROUP BY e " +
		       "HAVING COUNT(p) >= 2";
	}

	static String query_040() {
		return "UPDATE Employee e " +
		       "SET e.salary = 60000 " +
		       "WHERE e.salary = 55000";
	}

	static String query_041() {
		return "UPDATE Employee e " +
		       "SET e.salary = e.salary + 5000 " +
		       "WHERE EXISTS (SELECT p " +
		       "              FROM e.projects p " +
		       "              WHERE p.name = 'Release1')";
	}

	static String query_042() {
		return "UPDATE Phone p " +
		       "SET p.number = CONCAT('288', SUBSTRING(p.number, LOCATE(p.number, '-'), 4)), p.type = 'Business' " +
		       "WHERE p.employee.address.city = 'New York' AND p.type = 'Office'";
	}

	static String query_043() {
		return "DELETE FROM Employee e " +
		       "WHERE e.department IS NULL";
	}

	static String query_044() {
		return "Select Distinct object(c) " +
		       "From Customer c, In(c.orders) co " +
		       "Where co.totalPrice >= Some (Select o.totalPrice From Order o, In(o.lineItems) l Where l.quantity = 3)";
	}

	static String query_045() {
		return "SELECT DISTINCT object(c) " +
		       "FROM Customer c, IN(c.orders) co " +
		       "WHERE co.totalPrice <= SOME (Select o.totalPrice FROM Order o, IN(o.lineItems) l WHERE l.quantity = 3)";
	}

	static String query_046() {
		return "SELECT Distinct object(c) " +
		       "FROM Customer c, IN(c.orders) co " +
		       "WHERE co.totalPrice = ANY (Select MAX(o.totalPrice) FROM Order o)";
	}

	static String query_047() {
		return "SELECT Distinct object(c) " +
		       "FROM Customer c, IN(c.orders) co " +
		       "WHERE co.totalPrice < ANY (Select o.totalPrice FROM Order o, IN(o.lineItems) l WHERE l.quantity = 3)";
	}

	static String query_048() {
		return "SELECT Distinct object(c) " +
		       "FROM Customer c, IN(c.orders) co " +
		       "WHERE co.totalPrice > ANY (Select o.totalPrice FROM Order o, IN(o.lineItems) l WHERE l.quantity = 3)";
	}

	static String query_049() {
		return "SELECT Distinct object(c) " +
		       "FROM Customer c, IN(c.orders) co " +
		       "WHERE co.totalPrice <> ALL (Select MIN(o.totalPrice) FROM Order o)";
	}

	static String query_050() {
		return "SELECT Distinct object(c) " +
		       "FROM Customer c, IN(c.orders) co " +
		       "WHERE co.totalPrice >= ALL (Select o.totalPrice FROM Order o, IN(o.lineItems) l WHERE l.quantity >= 3)";
	}

	static String query_051() {
		return "SELECT Distinct object(c) " +
		       "FROM Customer c, IN(c.orders) co " +
		       "WHERE co.totalPrice <= ALL (Select o.totalPrice FROM Order o, IN(o.lineItems) l WHERE l.quantity > 3)";
	}

	static String query_052() {
		return "SELECT DISTINCT object(c) " +
		       "FROM Customer c, IN(c.orders) co " +
		       "WHERE co.totalPrice = ALL (Select MIN(o.totalPrice) FROM Order o)";
	}

	static String query_053() {
		return "SELECT DISTINCT object(c) " +
             "FROM Customer c, IN(c.orders) co " +
		       "WHERE co.totalPrice < ALL (Select o.totalPrice FROM Order o, IN(o.lineItems) l WHERE l.quantity > 3)";
	}

	static String query_054() {
		return "SELECT DISTINCT object(c) " +
		       "FROM Customer c, IN(c.orders) co " +
		       "WHERE co.totalPrice > ALL (Select o.totalPrice FROM Order o, IN(o.lineItems) l WHERE l.quantity > 3)";
	}

	static String query_055() {
		return "SELECT DISTINCT c " +
		       "FROM Customer c JOIN c.orders o " +
		       "WHERE EXISTS (SELECT l FROM o.lineItems l where l.quantity > 3)";
	}

	static String query_056() {
		return "SELECT DISTINCT c " +
		       "FROM Customer c JOIN c.orders o " +
		       "WHERE EXISTS (SELECT o FROM c.orders o where o.totalPrice BETWEEN 1000 AND 1200)";
	}

	static String query_057() {
		return "SELECT DISTINCT c " +
		       "from Customer c " +
		       "WHERE c.home.state IN(Select distinct w.state from c.work w where w.state = :state)";
	}

	static String query_058() {
		return "Select Object(o) " +
		       "from Order o " +
		       "WHERE EXISTS (Select c From o.customer c WHERE c.name LIKE '%Caruso')";
	}

	static String query_059() {
		return "SELECT DISTINCT c " +
		       "FROM Customer c " +
		       "WHERE EXISTS (SELECT o FROM c.orders o where o.totalPrice > 1500)";
	}

	static String query_060() {
		return "SELECT c " +
		       "FROM Customer c " +
		       "WHERE NOT EXISTS (SELECT o1 FROM c.orders o1)";
	}

	static String query_061() {
		return "select object(o) " +
		       "FROM Order o " +
		       "Where SQRT(o.totalPrice) > :doubleValue";
	}

	static String query_062() {
		return "select sum(o.totalPrice) " +
		       "FROM Order o " +
		       "GROUP BY o.totalPrice " +
		       "HAVING ABS(o.totalPrice) = :doubleValue";
	}

	static String query_063() {
		return "select c.name " +
		       "FROM Customer c " +
		       "Group By c.name " +
		       "HAVING trim(TRAILING from c.name) = ' David R. Vincent'";
	}

	static String query_064() {
		return "select c.name " +
		       "FROM  Customer c " +
		       "Group By c.name " +
		       "Having trim(LEADING from c.name) = 'David R. Vincent '";
	}

	static String query_065() {
		return "select c.name " +
		       "FROM  Customer c " +
		       "Group by c.name " +
		       "HAVING trim(BOTH from c.name) = 'David R. Vincent'";
	}

	static String query_066() {
		return "select c.name " +
		       "FROM  Customer c " +
		       "GROUP BY c.name " +
		       "HAVING LOCATE('Frechette', c.name) > 0";
	}

	static String query_067() {
		return "select a.city " +
		       "FROM  Customer c JOIN c.home a " +
		       "GROUP BY a.city " +
		       "HAVING LENGTH(a.city) = 10";
	}

	static String query_068() {
		return "select count(cc.country) " +
		       "FROM  Customer c JOIN c.country cc " +
		       "GROUP BY cc.country " +
		       "HAVING UPPER(cc.country) = 'ENGLAND'";
	}

	static String query_069() {
		return "select count(cc.country) " +
		       "FROM  Customer c JOIN c.country cc " +
		       "GROUP BY cc.code " +
		       "HAVING LOWER(cc.code) = 'gbr'";
	}

	static String query_070() {
		return "select c.name " +
		       "FROM  Customer c " +
		       "Group By c.name " +
		       "HAVING c.name = concat(:fmname, :lname)";
	}

	static String query_071() {
		return "select count(c) " +
		       "FROM  Customer c JOIN c.aliases a " +
		       "GROUP BY a.alias " +
		       "HAVING a.alias = SUBSTRING(:string1, :int1, :int2)";
	}

	static String query_072() {
		return "select c.country.country " +
		       "FROM  Customer c " +
		       "GROUP BY c.country.country";
	}

	static String query_073() {
		return "select Count(c) " +
		       "FROM  Customer c JOIN c.country cc " +
		       "GROUP BY cc.code " +
		       "HAVING cc.code IN ('GBR', 'CHA')";
	}

	static String query_074() {
		return "select c.name " +
		       "FROM  Customer c JOIN c.orders o " +
		       "WHERE o.totalPrice BETWEEN 90 AND 160 " +
		       "GROUP BY c.name";
	}

	static String query_075() {
		return "select Object(o) " +
		       "FROM Order AS o " +
		       "WHERE o.customer.id = '1001' OR o.totalPrice > 10000";
	}

	static String query_076() {
		return "select Distinct Object(o) " +
		       "FROM Order AS o " +
		       "WHERE o.customer.id = '1001' OR o.totalPrice < 1000";
	}

	static String query_077() {
		return "select Object(o) " +
		       "FROM Order AS o " +
		       "WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice > 10000";
	}

	static String query_078() {
		return "select DISTINCT o " +
		       "FROM Order AS o " +
		       "WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice > 5000";
	}

	static String query_079() {
		return "select Object(o) " +
		       "FROM Order AS o " +
		       "WHERE o.customer.id = '1001' AND o.totalPrice > 10000";
	}

	static String query_080() {
		return "select Object(o) " +
		       "FROM Order AS o " +
		       "WHERE o.customer.id = '1001' AND o.totalPrice < 1000";
	}

	static String query_081() {
		return "select Object(o) " +
		       "FROM Order AS o " +
		       "WHERE o.customer.name = 'Karen R. Tegan' AND o.totalPrice > 10000";
	}

	static String query_082() {
		return "select Object(o) " +
		       "FROM Order AS o " +
		       "WHERE o.customer.name = 'Karen R. Tegan' AND o.totalPrice > 500";
	}

	static String query_083() {
		return "SELECT DISTINCT p " +
		       "From Product p " +
		       "where p.shelfLife.soldDate NOT BETWEEN :date1 AND :newdate";
	}

	static String query_084() {
		return "SELECT DISTINCT o " +
		       "From Order o " +
		       "where o.totalPrice NOT BETWEEN 1000 AND 1200";
	}

	static String query_085() {
		return "SELECT DISTINCT p " +
		       "From Product p " +
		       "where p.shelfLife.soldDate BETWEEN :date1 AND :date6";
	}

	static String query_086() {
		return "SELECT DISTINCT a " +
		       "from Alias a LEFT JOIN FETCH a.customers " +
		       "where a.alias LIKE 'a%'";
	}

	static String query_087() {
		return "select Object(o) " +
		       "from Order o LEFT JOIN FETCH o.customer " +
		       "where o.customer.name LIKE '%Caruso'";
	}

	static String query_088() {
		return "select o " +
		       "from Order o LEFT JOIN FETCH o.customer " +
		       "where o.customer.home.city='Lawrence'";
	}

	static String query_089() {
		return "SELECT DISTINCT c " +
		       "from Customer c LEFT JOIN FETCH c.orders " +
		       "where c.home.state IN('NY','RI')";
	}

	static String query_090() {
		return "SELECT c " +
		       "from Customer c JOIN FETCH c.spouse";
	}

	static String query_091() {
		return "SELECT Object(c) " +
		       "from Customer c INNER JOIN c.aliases a " +
		       "where a.alias = :aName";
	}

	static String query_092() {
		return "SELECT Object(o) " +
		       "from Order o INNER JOIN o.customer cust " +
		       "where cust.name = ?1";
	}

	static String query_093() {
		return "SELECT DISTINCT object(c) " +
		       "from Customer c INNER JOIN c.creditCards cc " +
		       "where cc.type='VISA'";
	}

	static String query_094() {
		return "SELECT c " +
		       "from Customer c INNER JOIN c.spouse s";
	}

	static String query_095() {
		return "select cc.type " +
		       "FROM CreditCard cc JOIN cc.customer cust " +
		       "GROUP BY cc.type";
	}

	static String query_096() {
		return "select cc.code " +
		       "FROM Customer c JOIN c.country cc " +
		       "GROUP BY cc.code";
	}

	static String query_097() {
		return "select Object(c) " +
		       "FROM Customer c JOIN c.aliases a " +
		       "where LOWER(a.alias)='sjc'";
	}

	static String query_098() {
		return "select Object(c) " +
		       "FROM Customer c JOIN c.aliases a " +
		       "where UPPER(a.alias)='SJC'";
	}

	static String query_099() {
		return "SELECT c.id, a.alias " +
		       "from Customer c LEFT OUTER JOIN c.aliases a " +
		       "where c.name LIKE 'Ste%' " +
		       "ORDER BY a.alias, c.id";
	}

	static String query_100() {
		return "SELECT o.id, cust.id " +
		       "from Order o LEFT OUTER JOIN o.customer cust " +
		       "where cust.name=?1 " +
		       "ORDER BY o.id";
	}

	static String query_101() {
		return "SELECT DISTINCT c " +
		       "from Customer c LEFT OUTER JOIN c.creditCards cc " +
		       "where c.name LIKE '%Caruso'";
	}

	static String query_102() {
		return "SELECT Sum(p.quantity) " +
		       "FROM Product p";
	}

	static String query_103() {
		return "Select Count(c.home.city) " +
		       "from Customer c";
	}

	static String query_104() {
		return "SELECT Sum(p.price) " +
		       "FROM Product p";
	}

	static String query_105() {
		return "SELECT AVG(o.totalPrice) " +
		       "FROM Order o";
	}

	static String query_106() {
		return "SELECT DISTINCT MAX(l.quantity) " +
		       "FROM LineItem l";
	}

	static String query_107() {
		return "SELECT DISTINCT MIN(o.id) " +
		       "FROM Order o " +
		       "where o.customer.name = 'Robert E. Bissett'";
	}

	static String query_108() {
		return "SELECT NEW com.sun.ts.tests.ejb30.persistence.query.language.schema30.Customer(c.id, c.name) " +
		       "FROM Customer c " +
		       "where c.work.city = :workcity";
	}

	static String query_109() {
		return "SELECT DISTINCT c " +
		       "FROM Customer c " +
		       "WHERE SIZE(c.orders) > 100";
	}

	static String query_110() {
		return "SELECT DISTINCT c " +
		       "FROM Customer c " +
		       "WHERE SIZE(c.orders) >= 2";
	}

	static String query_111() {
		return "select Distinct c " +
		       "FROM Customer c LEFT OUTER JOIN c.work workAddress " +
		       "where workAddress.zip IS NULL";
	}

	static String query_112() {
		return "SELECT DISTINCT c " +
		       "FROM Customer c, IN(c.orders) o";
	}

	static String query_113() {
		return "Select Distinct Object(c) " +
		       "from Customer c " +
		       "where c.name is null";
	}

	static String query_114() {
		return "Select c.name " +
		       "from Customer c " +
		       "where c.home.street = '212 Edgewood Drive'";
	}

	static String query_115() {
		return "Select s.customer " +
		       "from Spouse s " +
		       "where s.id = '6'";
	}

	static String query_116() {
		return "Select c.work.zip " +
		       "from Customer c";
	}

	static String query_117() {
		return "SELECT Distinct Object(c) " +
		       "From Customer c, IN(c.home.phones) p " +
		       "where p.area LIKE :area";
	}

	static String query_118() {
		return "SELECT DISTINCT Object(c) " +
		       "from Customer c, in(c.aliases) a " +
		       "where NOT a.customerNoop IS NULL";
	}

	static String query_119() {
		return "select distinct object(c) " +
		       "fRoM Customer c, IN(c.aliases) a " +
		       "where c.name = :cName OR a.customerNoop IS NULL";
	}

	static String query_120() {
		return "select Distinct Object(c) " +
		       "from Customer c, in(c.aliases) a " +
		       "where c.name = :cName AND a.customerNoop IS NULL";
	}

	static String query_121() {
		return "sElEcT Distinct oBJeCt(c) " +
		       "FROM Customer c, IN(c.aliases) a " +
		       "WHERE a.customerNoop IS NOT NULL";
	}

	static String query_122() {
		return "select distinct Object(c) " +
		       "FROM Customer c, in(c.aliases) a " +
		       "WHERE a.alias LIKE '%\\_%' escape '\\'";
	}

	static String query_123() {
		return "Select Distinct Object(c) " +
		       "FROM Customer c, in(c.aliases) a " +
		       "WHERE a.customerNoop IS NULL";
	}

	static String query_124() {
		return "Select Distinct o.creditCard.balance " +
		       "from Order o " +
		       "ORDER BY o.creditCard.balance ASC";
	}

	static String query_125() {
		return "Select c.work.zip " +
		       "from Customer c " +
		       "where c.work.zip IS NOT NULL " +
		       "ORDER BY c.work.zip ASC";
	}

	static String query_126() {
		return "SELECT a.alias " +
		       "FROM Alias AS a " +
		       "WHERE (a.alias IS NULL AND :param1 IS NULL) OR a.alias = :param1";
	}

	static String query_127() {
		return "Select Object(c) " +
		       "from Customer c " +
		       "where c.aliasesNoop IS NOT EMPTY or c.id <> '1'";
	}

	static String query_128() {
		return "Select Distinct Object(p) " +
		       "from Product p " +
		       "where p.name = ?1";
	}

	static String query_129() {
		return "Select Distinct Object(p) " +
		       "from Product p " +
		       "where (p.quantity > (500 + :int1)) AND (p.partNumber IS NULL)";
	}

	static String query_130() {
		return "Select Distinct Object(o) " +
		       "from Order o " +
		       "where o.customer.name IS NOT NULL";
	}

	static String query_131() {
		return "Select DISTINCT Object(p) " +
		       "From Product p " +
		       "where (p.quantity < 10) OR (p.quantity > 20)";
	}

	static String query_132() {
		return "Select DISTINCT Object(p) " +
		       "From Product p " +
		       "where p.quantity NOT BETWEEN 10 AND 20";
	}

	static String query_133() {
		return "Select DISTINCT OBJECT(p) " +
		       "From Product p " +
		       "where (p.quantity >= 10) AND (p.quantity <= 20)";
	}

	static String query_134() {
		return "Select DISTINCT OBJECT(p) " +
		       "From Product p " +
		       "where p.quantity BETWEEN 10 AND 20";
	}

	static String query_135() {
		return "Select Distinct OBJECT(c) " +
		       "from Customer c, IN(c.creditCards) b " +
		       "where SQRT(b.balance) = :dbl";
	}

	static String query_136() {
		return "Select Distinct OBJECT(c) " +
		       "From Product p " +
		       "where MOD(550, 100) = p.quantity";
	}

	static String query_137() {
		return "SELECT DISTINCT Object(c) " +
		       "from Customer c " +
		       "WHERE (c.home.state = 'NH') OR (c.home.state = 'RI')";
	}

	static String query_138() {
		return "SELECT DISTINCT Object(c) " +
		       "from Customer c " +
		       "where c.home.state IN('NH', 'RI')";
	}

	static String query_139() {
		return "SELECT p " +
		       "FROM Employee e JOIN e.projects p " +
		       "WHERE e.id = :id AND INDEX(p) = 1";
	}

	static String query_140() {
		return "SELECT c " +
		       "from Customer c " +
		       "where c.home.city IN(:city)";
	}

	static String query_141() {
		return "Select Distinct Object(o) " +
		       "from Order o, in(o.lineItems) l " +
		       "where l.quantity NOT IN (1, 5)";
	}

	static String query_142() {
		return "Select Distinct Object(o) " +
		       "FROM Order o " +
		       "WHERE o.sampleLineItem MEMBER OF o.lineItems";
	}

	static String query_143() {
		return "Select Distinct Object(o) " +
		       "FROM Order o " +
		       "WHERE :param NOT MEMBER o.lineItems";
	}

	static String query_144() {
		return "Select Distinct Object(o) " +
		       "FROM Order o, LineItem l " +
		       "WHERE l MEMBER o.lineItems";
	}

	static String query_145() {
		return "select distinct Object(c) " +
		       "FROM Customer c, in(c.aliases) a " +
		       "WHERE a.alias LIKE 'sh\\_ll' escape '\\'";
	}

	static String query_146() {
		return "Select Distinct Object(a) " +
		       "FROM Alias a " +
		       "WHERE a.customerNoop NOT MEMBER OF a.customersNoop";
	}

	static String query_147() {
		return "Select Distinct Object(a) " +
		       "FROM Alias a " +
		       "WHERE a.customerNoop MEMBER OF a.customersNoop";
	}

	static String query_148() {
		return "Select Distinct Object(a) " +
		       "from Alias a " +
		       "where LOCATE('ev', a.alias) = 3";
	}

	static String query_149() {
		return "Select DISTINCT Object(o) " +
		       "From Order o " +
		       "WHERE o.totalPrice > ABS(:dbl)";
	}

	static String query_150() {
		return "Select Distinct OBjeCt(a) " +
		       "From Alias a " +
		       "WHERE LENGTH(a.alias) > 4";
	}

	static String query_151() {
		return "Select Distinct Object(a) " +
		       "From Alias a " +
		       "WHERE a.alias = SUBSTRING(:string1, :int2, :int3)";
	}

	static String query_152() {
		return "Select Distinct Object(a) " +
		       "From Alias a " +
		       "WHERE a.alias = CONCAT('ste', 'vie')";
	}

	static String query_153() {
		return "Select Distinct Object(c) " +
		       "FROM Customer c " +
		       "WHERE c.work.zip IS NOT NULL";
	}

	static String query_154() {
		return "sELEct dIsTiNcT oBjEcT(c) " +
		       "FROM Customer c " +
		       "WHERE c.work.zip IS NULL";
	}

	static String query_155() {
		return "Select Distinct Object(c) " +
		       "FROM Customer c " +
		       "WHERE c.aliases IS NOT EMPTY";
	}

	static String query_156() {
		return "Select Distinct Object(c) " +
		       "FROM Customer c " +
		       "WHERE c.aliases IS EMPTY";
	}

	static String query_157() {
		return "Select Distinct Object(c) " +
		       "FROM Customer c " +
		       "WHERE c.home.zip not like '%44_'";
	}

	static String query_158() {
		return "Select Distinct Object(c) " +
		       "FROM Customer c " +
		       "WHERE c.home.zip LIKE '%77'";
	}

	static String query_159() {
		return "Select Distinct Object(c) " +
		       "FROM Customer c Left Outer Join c.home h " +
		       "WHERE h.city Not iN ('Swansea', 'Brookline')";
	}

	static String query_160() {
		return "select distinct c " +
		       "FROM Customer c " +
		       "WHERE c.home.city IN ('Lexington')";
	}

	static String query_161() {
		return "sElEcT c " +
		       "FROM Customer c " +
		       "Where c.name = :cName";
	}

	static String query_162() {
		return "select distinct Object(o) " +
		       "From Order o " +
		       "WHERE o.creditCard.approved = FALSE";
	}

	static String query_163() {
		return "SELECT DISTINCT Object(o) " +
		       "From Order o " +
		       "where o.totalPrice NOT bETwEeN 1000 AND 1200";
	}

	static String query_164() {
		return "SELECT DISTINCT Object(o) " +
		       "From Order o " +
		       "where o.totalPrice BETWEEN 1000 AND 1200";
	}

	static String query_165() {
		return "SELECT DISTINCT Object(o) " +
		       "FROM Order o, in(o.lineItems) l " +
		       "WHERE l.quantity < 2 AND o.customer.name = 'Robert E. Bissett'";
	}

	static String query_166() {
		return "select distinct Object(o) " +
		       "FROM Order AS o, in(o.lineItems) l " +
		       "WHERE (l.quantity < 2) AND ((o.totalPrice < (3 + 54 * 2 + -8)) OR (o.customer.name = 'Robert E. Bissett'))";
	}

	static String query_167() {
		return "SeLeCt DiStInCt oBjEcT(o) " +
		       "FROM Order AS o " +
		       "WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice < 100";
	}

	static String query_168() {
		return "Select Distinct Object(o) " +
		       "FROM Order o " +
		       "WHERE NOT o.totalPrice < 4500";
	}

	static String query_169() {
		return "Select DISTINCT Object(P) " +
		       "From Product p";
	}

	static String query_170() {
		return "SELECT DISTINCT c " +
		       "from Customer c " +
		       "WHERE c.home.street = :street OR c.home.city = :city OR c.home.state = :state or c.home.zip = :zip";
	}

	static String query_171() {
		return "Select Distinct Object(c) " +
		       "fRoM Customer c, IN(c.creditCards) a " +
		       "where a.type = :ccard";
	}

	static String query_172() {
		return "SELECT c " +
		       "from Customer c " +
		       "WHERE c.home.street = :street AND c.home.city = :city AND c.home.state = :state and c.home.zip = :zip";
	}

	static String query_173() {
		return "Select Distinct Object(c) " +
		       "FrOm Customer c, In(c.aliases) a " +
		       "WHERE a.alias = :aName";
	}

	static String query_174() {
		return "Select Distinct Object(c) " +
		       "FROM Customer AS c";
	}

	static String query_175() {
		return "Select Distinct o " +
		       "from Order AS o " +
		       "WHERE o.customer.name = :name";
	}

	static String query_176() {
		return "UPDATE Customer c SET c.name = 'CHANGED' " +
		       "WHERE c.orders IS NOT EMPTY";
	}

	static String query_177() {
		return "UPDATE DateTime SET date = CURRENT_DATE";
	}

	static String query_178() {
		return "SELECT c FROM Customer c " +
		       "WHERE c.firstName = :first AND" +
		       "      c.lastName = :last";
	}

	static String query_179() {
		return "SELECT OBJECT ( c ) FROM Customer AS c";
	}

	static String query_180() {
		return "SELECT c.firstName, c.lastName " +
		       "FROM Customer AS c";
	}

	static String query_181() {
		return "SELECT c.address.city " +
		       "FROM Customer AS c";
	}

	static String query_182() {
		return "SELECT new com.titan.domain.Name(c.firstName, c.lastName) " +
		       "FROM Customer c";
	}

	static String query_183() {
		return "SELECT cbn.ship " +
		       "FROM Customer AS c, IN ( c.reservations ) r, IN ( r.cabins ) cbn";
	}

	static String query_184() {
		return "Select c.firstName, c.lastName, p.number " +
		       "From Customer c Left Join c.phoneNumbers p";
	}

	static String query_185() {
		return "SELECT r " +
		       "FROM Reservation AS r " +
		       "WHERE (r.amountPaid * .01) > 300.00";
	}

	static String query_186() {
		return "SELECT s " +
		       "FROM Ship AS s " +
		       "WHERE s.tonnage >= 80000.00 AND s.tonnage <= 130000.00";
	}

	static String query_187() {
		return "SELECT r " +
		       "FROM Reservation r, IN ( r.customers ) AS cust " +
		       "WHERE cust = :specificCustomer";
	}

	static String query_188() {
		return "SELECT s " +
		       "FROM Ship AS s " +
		       "WHERE s.tonnage BETWEEN 80000.00 AND 130000.00";
	}

	static String query_189() {
		return "SELECT s " +
		       "FROM Ship AS s " +
		       "WHERE s.tonnage NOT BETWEEN 80000.00 AND 130000.00";
	}

	static String query_190() {
		return "SELECT c " +
		       "FROM Customer AS c " +
		       "WHERE c.address.state IN ('FL', 'TX', 'MI', 'WI', 'MN')";
	}

	static String query_191() {
		return "SELECT cab " +
		       "FROM Cabin AS cab " +
		       "WHERE cab.deckLevel IN (1,3,5,7)";
	}

	static String query_192() {
		return "SELECT c " +
		       "FROM Customer c " +
		       "WHERE c.address.state IN(?1, ?2, ?3, 'WI', 'MN')";
	}

	static String query_193() {
		return "SELECT c " +
		       "FROM Customer c " +
		       "WHERE c.address IS NULL";
	}

	static String query_194() {
		return "SELECT c " +
		       "FROM Customer c " +
		       "WHERE c.address.state = 'TX' AND" +
		       "      c.lastName = 'Smith' AND" +
		       "      c.firstName = 'John'";
	}

	static String query_195() {
		return "SELECT crs " +
		       "FROM Cruise AS crs, IN(crs.reservations) AS res, Customer AS cust " +
		       "WHERE " +
		       " cust = :myCustomer " +
		       "   AND" +
		       " cust MEMBER OF res.customers";
	}

	static String query_196() {
		return "SELECT c " +
		       "FROM Customer AS c " +
		       "WHERE " +
		       " LENGTH(c.lastName) > 6 " +
		       "   AND" +
		       " LOCATE( c.lastName, 'Monson' ) > -1";
	}

	static String query_197() {
		return "SELECT c " +
		       "FROM Customer AS C " +
		       "ORDER BY c.lastName";
	}

	static String query_198() {
		return "SELECT c " +
		       "FROM Customer AS C " +
		       "WHERE c.address.city = 'Boston' AND c.address.state = 'MA' " +
		       "ORDER BY c.lastName DESC";
	}

	static String query_199() {
		return "SELECT cr.name, COUNT (res) " +
		       "FROM Cruise cr LEFT JOIN cr.reservations res " +
		       "GROUP BY cr.name";
	}

	static String query_200() {
		return "SELECT cr.name, COUNT (res) " +
		       "FROM Cruise cr LEFT JOIN cr.reservations res " +
		       "GROUP BY cr.name " +
		       "HAVING count(res) > 10";
	}

	static String query_201() {
		return "SELECT COUNT (res) " +
		       "FROM Reservation res " +
		       "WHERE res.amountPaid > " +
		       "      (SELECT avg(r.amountPaid) FROM Reservation r)";
	}

	static String query_202() {
		return "SELECT cr " +
		       "FROM Cruise cr " +
		       "WHERE 100000 < (" +
		       "   SELECT SUM(res.amountPaid) FROM cr.reservations res" +
		       ")";
	}

	static String query_203() {
		return "SELECT cr " +
		       "FROM Cruise cr " +
		       "WHERE 0 < ALL (" +
		       "   SELECT res.amountPaid from cr.reservations res" +
		       ")";
	}

	static String query_204() {
		return "UPDATE Reservation res " +
		       "SET res.name = 'Pascal' " +
		       "WHERE EXISTS (" +
		       "   SELECT c FROM res.customers c " +
		       "   WHERE c.firstName = 'Bill' AND c.lastName='Burke'" +
		       ")";
	}

	static String query_205() {
		return "UPDATE Employee e " +
		       "SET e.salary = " +
		       "    CASE WHEN e.rating = 1 THEN e.salary * 1.1 " +
		       "         WHEN e.rating = 2 THEN e.salary * 1.05 " +
		       "         ELSE e.salary * 1.01" +
		       "    END";
	}

	static String query_206() {
		return "SELECT e.name, " +
		       "       CASE TYPE(e) WHEN Exempt THEN 'Exempt' " +
		       "                    WHEN Contractor THEN 'Contractor' " +
		       "                    WHEN Intern THEN 'Intern' " +
		       "                    ELSE 'NonExempt' " +
		       "       END " +
		       "FROM Employee e " +
		       "WHERE e.dept.name = 'Engineering'";
	}

	static String query_207() {
		return "SELECT e.name, " +
		       "       f.name, " +
		       "       CONCAT(CASE WHEN f.annualMiles > 50000 THEN 'Platinum '" +
		       "                   WHEN f.annualMiles > 25000 THEN 'Gold ' " +
		       "                   ELSE '' " +
		       "              END, " +
		       "              'Frequent Flyer') " +
		       "FROM Employee e JOIN e.frequentFlierPlan f";
	}

	static String query_208() {
		return "SELECT e " +
		       "FROM Employee e " +
		       "WHERE TYPE(e) IN (Exempt, Contractor)";
	}

	static String query_209() {
		return "SELECT e " +
		       "FROM Employee e " +
		       "WHERE TYPE(e) IN (:empType1, :empType2)";
	}

	static String query_210() {
		return "SELECT e " +
		       "FROM Employee e " +
		       "WHERE TYPE(e) IN :empTypes";
	}

	static String query_211() {
		return "SELECT TYPE(e) " +
		       "FROM Employee e " +
		       "WHERE TYPE(e) <> Exempt";
	}

	static String query_212() {
		return "SELECT t " +
		       "FROM CreditCard c JOIN c.transactionHistory t " +
		       "WHERE c.holder.name = 'John Doe' AND INDEX(t) BETWEEN 0 AND 9";
	}

	static String query_213() {
		return "SELECT w.name " +
		       "FROM Course c JOIN c.studentWaitlist w " +
		       "WHERE c.name = 'Calculus' " +
		       "      AND " +
		       "      INDEX(w) = 0";
	}

	static String query_214() {
		return "UPDATE Employee e " +
		       "SET e.salary = CASE e.rating WHEN 1 THEN e.salary * 1.1 " +
		       "                             WHEN 2 THEN e.salary * 1.05 " +
		       "                             ELSE e.salary * 1.01 " +
		       "               END";
	}

	static String query_215() {
		return "SELECT o " +
		        "FROM Customer c JOIN c.orders o JOIN c.address a " +
		        "WHERE a.state = 'CA' " +
		        "ORDER BY o.quantity DESC, o.totalcost";
	}

	static String query_216() {
		return "SELECT o.quantity, a.zipcode " +
		       "FROM Customer c JOIN c.orders o JOIN c.address a " +
		       "WHERE a.state = 'CA' " +
		       "ORDER BY o.quantity, a.zipcode";
	}

	static String query_217() {
		return "SELECT o.quantity, o.cost*1.08 AS taxedCost, a.zipcode " +
		       "FROM Customer c JOIN c.orders o JOIN c.address a " +
		       "WHERE a.state = 'CA' AND a.county = 'Santa Clara' " +
		       "ORDER BY o.quantity, taxedCost, a.zipcode";
	}

	static String query_218() {
		return "SELECT AVG(o.quantity) as q, a.zipcode " +
		       "FROM Customer c JOIN c.orders o JOIN c.address a " +
		       "WHERE a.state = 'CA' " +
		       "GROUP BY a.zipcode " +
		       "ORDER BY q DESC";
	}

	static String query_219() {
		return "DELETE " +
		       "FROM Customer c " +
		       "WHERE c.status = 'inactive'";
	}

	static String query_220() {
		return "DELETE " +
		       "FROM Customer c " +
		       "WHERE c.status = 'inactive' " +
		       "      AND " +
		       "      c.orders IS EMPTY";
	}

	static String query_221() {
		return "UPDATE customer c " +
		       "SET c.status = 'outstanding' " +
		       "WHERE c.balance < 10000";
	}

	static String query_222() {
		return "SELECT e.salary / 1000D n " +
		       "From Employee e";
	}

	static String query_223() {
		return "SELECT MOD(a.id, 2) AS m " +
		       "FROM Address a JOIN FETCH a.customerList ORDER BY m";
	}
}