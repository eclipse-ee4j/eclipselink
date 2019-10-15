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
package org.eclipse.persistence.jpa.tests.jpql;

/**
 * This class provides a list of queries that are written against the JPQL 1.0 grammar.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class JPQLQueries1_0 {

    private JPQLQueries1_0() {
        super();
    }

    public static String query_001() {
        return "SELECT e FROM Employee e";
    }

    public static String query_002() {
        return "SELECT e\nFROM Employee e";
    }

    public static String query_003() {
        return "SELECT e " +
               "FROM Employee e " +
               "WHERE e.department.name = 'NA42' AND " +
               "      e.address.state IN ('NY', 'CA')";
    }

    public static String query_004() {
        return "SELECT p.number " +
               "FROM Employee e, Phone p " +
               "WHERE e = p.employee AND " +
               "      e.department.name = 'NA42' AND " +
               "      p.type = 'Cell'";
    }

    public static String query_005() {
        return "SELECT d, COUNT(e), MAX(e.salary), AVG(e.salary) " +
               "FROM Department d JOIN d.employees e " +
               "GROUP BY d " +
               "HAVING COUNT(e) >= 5";
    }

    public static String query_006() {
        return "SELECT e " +
               "FROM Employee e " +
               "WHERE e.department = ?1 AND " +
               "      e.salary > ?2";
    }

    public static String query_007() {
        return "SELECT e " +
               "FROM Employee e " +
               "WHERE e.department = :dept AND " +
               "      e.salary > :base";
    }

    public static String query_008() {
      return "SELECT e " +
               "FROM Employee e " +
               "WHERE e.department = 'NA65' AND " +
               "      e.name = 'UNKNOWN'' OR e.name = ''Roberts'";
    }

    public static String query_009() {
        return "SELECT e " +
               "FROM Employee e " +
               "WHERE e.startDate BETWEEN ?1 AND ?2";
    }

    public static String query_010() {
        return "SELECT e " +
               "FROM Employee e " +
               "WHERE e.department = :dept AND " +
               "      e.salary = (SELECT MAX(e.salary) " +
               "                  FROM Employee e " +
               "                  WHERE e.department = :dept)";
    }

    public static String query_011() {
        return "SELECT e " +
               "FROM Project p JOIN p.employees e " +
               "WHERE p.name = ?1 " +
               "ORDER BY e.name";
    }

    public static String query_012() {
        return "SELECT e " +
               "FROM Employee e " +
               "WHERE e.projects IS EMPTY";
    }

    public static String query_013() {
        return "SELECT e " +
               "FROM Employee e " +
               "WHERE e.projects IS NOT EMPTY";
    }

    public static String query_014() {
        return "UPDATE Employee e " +
               "SET e.manager = ?1 " +
               "WHERE e.department = ?2";
    }

    public static String query_015() {
        return "DELETE FROM Project p " +
               "WHERE p.employees IS EMPTY";
    }

    public static String query_016() {
        return "DELETE FROM Department d " +
               "WHERE d.name IN ('CA13', 'CA19', 'NY30')";
    }

    public static String query_017() {
        return "UPDATE Employee e " +
               "SET e.department = null " +
               "WHERE e.department.name IN ('CA13', 'CA19', 'NY30')";
    }

    public static String query_018() {
        return "SELECT d " +
               "FROM Department d " +
               "WHERE d.name LIKE 'QA\\_%' ESCAPE '\\'";
    }

    public static String query_019() {
        return "SELECT e " +
               "FROM Employee e " +
               "WHERE e.salary = (SELECT MAX(e2.salary) FROM Employee e2)";
    }

    public static String query_020() {
        return "SELECT e " +
               "FROM Employee e " +
               "WHERE EXISTS (SELECT p FROM Phone p WHERE p.employee = e AND p.type = 'Cell')";
    }

    public static String query_021() {
        return "SELECT e " +
               "FROM Employee e " +
               "WHERE EXISTS (SELECT p FROM e.phones p WHERE p.type = 'Cell')";
    }

    public static String query_022() {
        return "SELECT e " +
               "FROM Employee e " +
               "WHERE e.department IN (SELECT DISTINCT d " +
               "                       FROM Department d JOIN d.employees de JOIN de.projects p " +
               "                       WHERE p.name LIKE 'QA%')";
    }

    public static String query_023() {
        return "SELECT p " +
               "FROM Phone p " +
               "WHERE p.type NOT IN ('Office', 'Home')";
    }

    public static String query_024() {
        return "SELECT m " +
               "FROM Employee m " +
               "WHERE (SELECT COUNT(e) " +
               "       FROM Employee e " +
               "       WHERE e.manager = m) > 0";
    }

    public static String query_025() {
        return "SELECT e " +
               "FROM Employee e " +
               "WHERE e MEMBER OF e.directs";
    }

    public static String query_026() {
        return "SELECT e " +
               "FROM Employee e " +
               "WHERE NOT EXISTS (SELECT p " +
               "                  FROM e.phones p " +
               "                  WHERE p.type = 'Cell')";
    }

    public static String query_027() {
        return "SELECT e " +
               "FROM Employee e " +
               "WHERE e.directs IS NOT EMPTY AND " +
               "      e.salary < ALL (SELECT d.salary " +
               "                      FROM e.directs d)";
    }

    public static String query_028() {
        return "SELECT e " +
               "FROM Employee e " +
               "WHERE e.department = ANY (SELECT DISTINCT d FROM Department d JOIN d.employees de JOIN de.projects p " +
               "                          WHERE p.name LIKE 'QA%')";
    }

    public static String query_029() {
        return "SELECT d " +
               "FROM Department d " +
               "WHERE SIZE(d.employees) = 2";
    }

    public static String query_030() {
        return "SELECT d " +
               "FROM Department d " +
               "WHERE (SELECT COUNT(e) " +
               "       FROM d.employees e) = 2";
    }

    public static String query_031() {
        return "SELECT e " +
               "FROM Employee e " +
               "ORDER BY e.name DESC";
    }

    public static String query_032() {
        return "SELECT e " +
               "FROM Employee e JOIN e.department d " +
               "ORDER BY d.name, e.name DESC";
    }

    public static String query_033() {
        return "SELECT AVG(e.salary) " +
               "FROM Employee e";
    }

    public static String query_034() {
        return "SELECT d.name, AVG(e.salary) " +
               "FROM Department d JOIN d.employees e " +
               "GROUP BY d.name";
    }

    public static String query_035() {
        return "SELECT d.name, AVG(e.salary) " +
               "FROM Department d JOIN d.employees e " +
               "WHERE e.directs IS EMPTY " +
               "GROUP BY d.name";
    }

    public static String query_036() {
        return "SELECT d.name, AVG(e.salary) " +
               "FROM Department d JOIN d.employees e " +
               "WHERE e.directs IS EMPTY " +
               "GROUP BY d.name " +
               "HAVING AVG(e.salary) > 50000";
    }

    public static String query_037() {
        return "SELECT e, COUNT(p), COUNT(DISTINCT p.type) " +
               "FROM Employee e JOIN e.phones p " +
               "GROUP BY e";
    }

    public static String query_038() {
        return "SELECT d.name, e.salary, COUNT(p) " +
               "FROM Department d JOIN d.employees e JOIN e.projects p " +
               "GROUP BY d.name, e.salary";
    }

    public static String query_039() {
        return "SELECT e, COUNT(p) " +
               "FROM Employee e JOIN e.projects p " +
               "GROUP BY e " +
               "HAVING COUNT(p) >= 2";
    }

    public static String query_040() {
        return "UPDATE Employee e " +
               "SET e.salary = 60000 " +
               "WHERE e.salary = 55000";
    }

    public static String query_041() {
        return "UPDATE Employee e " +
               "SET e.salary = e.salary + 5000 " +
               "WHERE EXISTS (SELECT p " +
               "              FROM e.projects p " +
               "              WHERE p.name = 'Release1')";
    }

    public static String query_042() {
        return "UPDATE Phone p " +
               "SET p.number = CONCAT('288', SUBSTRING(p.number, LOCATE(p.number, '-'), 4)), p.type = 'Business' " +
               "WHERE p.employee.address.city = 'New York' AND p.type = 'Office'";
    }

    public static String query_043() {
        return "DELETE FROM Employee e " +
               "WHERE e.department IS NULL";
    }

    public static String query_044() {
        return "Select Distinct object(c) " +
               "From Customer c, In(c.orders) co " +
               "Where co.totalPrice >= Some (Select o.totalPrice From Order o, In(o.lineItems) l Where l.quantity = 3)";
    }

    public static String query_045() {
        return "SELECT DISTINCT object(c) " +
               "FROM Customer c, IN(c.orders) co " +
               "WHERE co.totalPrice <= SOME (Select o.totalPrice FROM Order o, IN(o.lineItems) l WHERE l.quantity = 3)";
    }

    public static String query_046() {
        return "SELECT Distinct object(c) " +
               "FROM Customer c, IN(c.orders) co " +
               "WHERE co.totalPrice = ANY (Select MAX(o.totalPrice) FROM Order o)";
    }

    public static String query_047() {
        return "SELECT Distinct object(c) " +
               "FROM Customer c, IN(c.orders) co " +
               "WHERE co.totalPrice < ANY (Select o.totalPrice FROM Order o, IN(o.lineItems) l WHERE l.quantity = 3)";
    }

    public static String query_048() {
        return "SELECT Distinct object(c) " +
               "FROM Customer c, IN(c.orders) co " +
               "WHERE co.totalPrice > ANY (Select o.totalPrice FROM Order o, IN(o.lineItems) l WHERE l.quantity = 3)";
    }

    public static String query_049() {
        return "SELECT Distinct object(c) " +
               "FROM Customer c, IN(c.orders) co " +
               "WHERE co.totalPrice <> ALL (Select MIN(o.totalPrice) FROM Order o)";
    }

    public static String query_050() {
        return "SELECT Distinct object(c) " +
               "FROM Customer c, IN(c.orders) co " +
               "WHERE co.totalPrice >= ALL (Select o.totalPrice FROM Order o, IN(o.lineItems) l WHERE l.quantity >= 3)";
    }

    public static String query_051() {
        return "SELECT Distinct object(c) " +
               "FROM Customer c, IN(c.orders) co " +
               "WHERE co.totalPrice <= ALL (Select o.totalPrice FROM Order o, IN(o.lineItems) l WHERE l.quantity > 3)";
    }

    public static String query_052() {
        return "SELECT DISTINCT object(c) " +
               "FROM Customer c, IN(c.orders) co " +
               "WHERE co.totalPrice = ALL (Select MIN(o.totalPrice) FROM Order o)";
    }

    public static String query_053() {
        return "SELECT DISTINCT object(c) " +
             "FROM Customer c, IN(c.orders) co " +
               "WHERE co.totalPrice < ALL (Select o.totalPrice FROM Order o, IN(o.lineItems) l WHERE l.quantity > 3)";
    }

    public static String query_054() {
        return "SELECT DISTINCT object(c) " +
               "FROM Customer c, IN(c.orders) co " +
               "WHERE co.totalPrice > ALL (Select o.totalPrice FROM Order o, IN(o.lineItems) l WHERE l.quantity > 3)";
    }

    public static String query_055() {
        return "SELECT DISTINCT c " +
               "FROM Customer c JOIN c.orders o " +
               "WHERE EXISTS (SELECT l FROM o.lineItems l where l.quantity > 3)";
    }

    public static String query_056() {
        return "SELECT DISTINCT c " +
               "FROM Customer c JOIN c.orders o " +
               "WHERE EXISTS (SELECT o FROM c.orders o where o.totalPrice BETWEEN 1000 AND 1200)";
    }

    public static String query_057() {
        return "SELECT DISTINCT c " +
               "from Customer c " +
               "WHERE c.home.state IN(Select distinct w.state from c.work w where w.state = :state)";
    }

    public static String query_058() {
        return "Select Object(o) " +
               "from Order o " +
               "WHERE EXISTS (Select c From o.customer c WHERE c.name LIKE '%Caruso')";
    }

    public static String query_059() {
        return "SELECT DISTINCT c " +
               "FROM Customer c " +
               "WHERE EXISTS (SELECT o FROM c.orders o where o.totalPrice > 1500)";
    }

    public static String query_060() {
        return "SELECT c " +
               "FROM Customer c " +
               "WHERE NOT EXISTS (SELECT o1 FROM c.orders o1)";
    }

    public static String query_061() {
        return "select object(o) " +
               "FROM Order o " +
               "Where SQRT(o.totalPrice) > :doubleValue";
    }

    public static String query_062() {
        return "select sum(o.totalPrice) " +
               "FROM Order o " +
               "GROUP BY o.totalPrice " +
               "HAVING ABS(o.totalPrice) = :doubleValue";
    }

    public static String query_063() {
        return "select c.name " +
               "FROM Customer c " +
               "Group By c.name " +
               "HAVING trim(TRAILING from c.name) = ' David R. Vincent'";
    }

    public static String query_064() {
        return "select c.name " +
               "FROM  Customer c " +
               "Group By c.name " +
               "Having trim(LEADING from c.name) = 'David R. Vincent '";
    }

    public static String query_065() {
        return "select c.name " +
               "FROM  Customer c " +
               "Group by c.name " +
               "HAVING trim(BOTH from c.name) = 'David R. Vincent'";
    }

    public static String query_066() {
        return "select c.name " +
               "FROM  Customer c " +
               "GROUP BY c.name " +
               "HAVING LOCATE('Frechette', c.name) > 0";
    }

    public static String query_067() {
        return "select a.city " +
               "FROM  Customer c JOIN c.home a " +
               "GROUP BY a.city " +
               "HAVING LENGTH(a.city) = 10";
    }

    public static String query_068() {
        return "select count(cc.country) " +
               "FROM  Customer c JOIN c.country cc " +
               "GROUP BY cc.country " +
               "HAVING UPPER(cc.country) = 'ENGLAND'";
    }

    public static String query_069() {
        return "select count(cc.country) " +
               "FROM  Customer c JOIN c.country cc " +
               "GROUP BY cc.code " +
               "HAVING LOWER(cc.code) = 'gbr'";
    }

    public static String query_070() {
        return "select c.name " +
               "FROM  Customer c " +
               "Group By c.name " +
               "HAVING c.name = concat(:fmname, :lname)";
    }

    public static String query_071() {
        return "select count(c) " +
               "FROM  Customer c JOIN c.aliases a " +
               "GROUP BY a.alias " +
               "HAVING a.alias = SUBSTRING(:string1, :int1, :int2)";
    }

    public static String query_072() {
        return "select c.country.country " +
               "FROM  Customer c " +
               "GROUP BY c.country.country";
    }

    public static String query_073() {
        return "select Count(c) " +
               "FROM  Customer c JOIN c.country cc " +
               "GROUP BY cc.code " +
               "HAVING cc.code IN ('GBR', 'CHA')";
    }

    public static String query_074() {
        return "select c.name " +
               "FROM  Customer c JOIN c.orders o " +
               "WHERE o.totalPrice BETWEEN 90 AND 160 " +
               "GROUP BY c.name";
    }

    public static String query_075() {
        return "select Object(o) " +
               "FROM Order AS o " +
               "WHERE o.customer.id = '1001' OR o.totalPrice > 10000";
    }

    public static String query_076() {
        return "select Distinct Object(o) " +
               "FROM Order AS o " +
               "WHERE o.customer.id = '1001' OR o.totalPrice < 1000";
    }

    public static String query_077() {
        return "select Object(o) " +
               "FROM Order AS o " +
               "WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice > 10000";
    }

    public static String query_078() {
        return "select DISTINCT o " +
               "FROM Order AS o " +
               "WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice > 5000";
    }

    public static String query_079() {
        return "select Object(o) " +
               "FROM Order AS o " +
               "WHERE o.customer.id = '1001' AND o.totalPrice > 10000";
    }

    public static String query_080() {
        return "select Object(o) " +
               "FROM Order AS o " +
               "WHERE o.customer.id = '1001' AND o.totalPrice < 1000";
    }

    public static String query_081() {
        return "select Object(o) " +
               "FROM Order AS o " +
               "WHERE o.customer.name = 'Karen R. Tegan' AND o.totalPrice > 10000";
    }

    public static String query_082() {
        return "select Object(o) " +
               "FROM Order AS o " +
               "WHERE o.customer.name = 'Karen R. Tegan' AND o.totalPrice > 500";
    }

    public static String query_083() {
        return "SELECT DISTINCT p " +
               "From Product p " +
               "where p.shelfLife.soldDate NOT BETWEEN :date1 AND :newdate";
    }

    public static String query_084() {
        return "SELECT DISTINCT o " +
               "From Order o " +
               "where o.totalPrice NOT BETWEEN 1000 AND 1200";
    }

    public static String query_085() {
        return "SELECT DISTINCT p " +
               "From Product p " +
               "where p.shelfLife.soldDate BETWEEN :date1 AND :date6";
    }

    public static String query_086() {
        return "SELECT DISTINCT a " +
               "from Alias a LEFT JOIN FETCH a.customers " +
               "where a.alias LIKE 'a%'";
    }

    public static String query_087() {
        return "select Object(o) " +
               "from Order o LEFT JOIN FETCH o.customer " +
               "where o.customer.name LIKE '%Caruso'";
    }

    public static String query_088() {
        return "select o " +
               "from Order o LEFT JOIN FETCH o.customer " +
               "where o.customer.home.city='Lawrence'";
    }

    public static String query_089() {
        return "SELECT DISTINCT c " +
               "from Customer c LEFT JOIN FETCH c.orders " +
               "where c.home.state IN('NY','RI')";
    }

    public static String query_090() {
        return "SELECT c " +
               "from Customer c JOIN FETCH c.spouse";
    }

    public static String query_091() {
        return "SELECT Object(c) " +
               "from Customer c INNER JOIN c.aliases a " +
               "where a.alias = :aName";
    }

    public static String query_092() {
        return "SELECT Object(o) " +
               "from Order o INNER JOIN o.customer cust " +
               "where cust.name = ?1";
    }

    public static String query_093() {
        return "SELECT DISTINCT object(c) " +
               "from Customer c INNER JOIN c.creditCards cc " +
               "where cc.type='VISA'";
    }

    public static String query_094() {
        return "SELECT c " +
               "from Customer c INNER JOIN c.spouse s";
    }

    public static String query_095() {
        return "select cc.type " +
               "FROM CreditCard cc JOIN cc.customer cust " +
               "GROUP BY cc.type";
    }

    public static String query_096() {
        return "select cc.code " +
               "FROM Customer c JOIN c.country cc " +
               "GROUP BY cc.code";
    }

    public static String query_097() {
        return "select Object(c) " +
               "FROM Customer c JOIN c.aliases a " +
               "where LOWER(a.alias)='sjc'";
    }

    public static String query_098() {
        return "select Object(c) " +
               "FROM Customer c JOIN c.aliases a " +
               "where UPPER(a.alias)='SJC'";
    }

    public static String query_099() {
        return "SELECT c.id, a.alias " +
               "from Customer c LEFT OUTER JOIN c.aliases a " +
               "where c.name LIKE 'Ste%' " +
               "ORDER BY a.alias, c.id";
    }

    public static String query_100() {
        return "SELECT o.id, cust.id " +
               "from Order o LEFT OUTER JOIN o.customer cust " +
               "where cust.name=?1 " +
               "ORDER BY o.id";
    }

    public static String query_101() {
        return "SELECT DISTINCT c " +
               "from Customer c LEFT OUTER JOIN c.creditCards cc " +
               "where c.name LIKE '%Caruso'";
    }

    public static String query_102() {
        return "SELECT Sum(p.quantity) " +
               "FROM Product p";
    }

    public static String query_103() {
        return "Select Count(c.home.city) " +
               "from Customer c";
    }

    public static String query_104() {
        return "SELECT Sum(p.price) " +
               "FROM Product p";
    }

    public static String query_105() {
        return "SELECT AVG(o.totalPrice) " +
               "FROM Order o";
    }

    public static String query_106() {
        return "SELECT DISTINCT MAX(l.quantity) " +
               "FROM LineItem l";
    }

    public static String query_107() {
        return "SELECT DISTINCT MIN(o.id) " +
               "FROM Order o " +
               "where o.customer.name = 'Robert E. Bissett'";
    }

    public static String query_108() {
        return "SELECT NEW com.sun.ts.tests.ejb30.persistence.query.language.schema30.Customer(c.id, c.name) " +
               "FROM Customer c " +
               "where c.work.city = :workcity";
    }

    public static String query_109() {
        return "SELECT DISTINCT c " +
               "FROM Customer c " +
               "WHERE SIZE(c.orders) > 100";
    }

    public static String query_110() {
        return "SELECT DISTINCT c " +
               "FROM Customer c " +
               "WHERE SIZE(c.orders) >= 2";
    }

    public static String query_111() {
        return "select Distinct c " +
               "FROM Customer c LEFT OUTER JOIN c.work workAddress " +
               "where workAddress.zip IS NULL";
    }

    public static String query_112() {
        return "SELECT DISTINCT c " +
               "FROM Customer c, IN(c.orders) o";
    }

    public static String query_113() {
        return "Select Distinct Object(c) " +
               "from Customer c " +
               "where c.name is null";
    }

    public static String query_114() {
        return "Select c.name " +
               "from Customer c " +
               "where c.home.street = '212 Edgewood Drive'";
    }

    public static String query_115() {
        return "Select s.customer " +
               "from Spouse s " +
               "where s.id = '6'";
    }

    public static String query_116() {
        return "Select c.work.zip " +
               "from Customer c";
    }

    public static String query_117() {
        return "SELECT Distinct Object(c) " +
               "From Customer c, IN(c.home.phones) p " +
               "where p.area LIKE :area";
    }

    public static String query_118() {
        return "SELECT DISTINCT Object(c) " +
               "from Customer c, in(c.aliases) a " +
               "where NOT a.customerNoop IS NULL";
    }

    public static String query_119() {
        return "select distinct object(c) " +
               "fRoM Customer c, IN(c.aliases) a " +
               "where c.name = :cName OR a.customerNoop IS NULL";
    }

    public static String query_120() {
        return "select Distinct Object(c) " +
               "from Customer c, in(c.aliases) a " +
               "where c.name = :cName AND a.customerNoop IS NULL";
    }

    public static String query_121() {
        return "sElEcT Distinct oBJeCt(c) " +
               "FROM Customer c, IN(c.aliases) a " +
               "WHERE a.customerNoop IS NOT NULL";
    }

    public static String query_122() {
        return "select distinct Object(c) " +
               "FROM Customer c, in(c.aliases) a " +
               "WHERE a.alias LIKE '%\\_%' escape '\\'";
    }

    public static String query_123() {
        return "Select Distinct Object(c) " +
               "FROM Customer c, in(c.aliases) a " +
               "WHERE a.customerNoop IS NULL";
    }

    public static String query_124() {
        return "Select Distinct o.creditCard.balance " +
               "from Order o " +
               "ORDER BY o.creditCard.balance ASC";
    }

    public static String query_125() {
        return "Select c.work.zip " +
               "from Customer c " +
               "where c.work.zip IS NOT NULL " +
               "ORDER BY c.work.zip ASC";
    }

    public static String query_126() {
        return "SELECT a.alias " +
               "FROM Alias AS a " +
               "WHERE (a.alias IS NULL AND :param1 IS NULL) OR a.alias = :param1";
    }

    public static String query_127() {
        return "Select Object(c) " +
               "from Customer c " +
               "where c.aliasesNoop IS NOT EMPTY or c.id <> '1'";
    }

    public static String query_128() {
        return "Select Distinct Object(p) " +
               "from Product p " +
               "where p.name = ?1";
    }

    public static String query_129() {
        return "Select Distinct Object(p) " +
               "from Product p " +
               "where (p.quantity > (500 + :int1)) AND (p.partNumber IS NULL)";
    }

    public static String query_130() {
        return "Select Distinct Object(o) " +
               "from Order o " +
               "where o.customer.name IS NOT NULL";
    }

    public static String query_131() {
        return "Select DISTINCT Object(p) " +
               "From Product p " +
               "where (p.quantity < 10) OR (p.quantity > 20)";
    }

    public static String query_132() {
        return "Select DISTINCT Object(p) " +
               "From Product p " +
               "where p.quantity NOT BETWEEN 10 AND 20";
    }

    public static String query_133() {
        return "Select DISTINCT OBJECT(p) " +
               "From Product p " +
               "where (p.quantity >= 10) AND (p.quantity <= 20)";
    }

    public static String query_134() {
        return "Select DISTINCT OBJECT(p) " +
               "From Product p " +
               "where p.quantity BETWEEN 10 AND 20";
    }

    public static String query_135() {
        return "Select Distinct OBJECT(c) " +
               "from Customer c, IN(c.creditCards) b " +
               "where SQRT(b.balance) = :dbl";
    }

    public static String query_136() {
        return "Select Distinct OBJECT(c) " +
               "From Product p " +
               "where MOD(550, 100) = p.quantity";
    }

    public static String query_137() {
        return "SELECT DISTINCT Object(c) " +
               "from Customer c " +
               "WHERE (c.home.state = 'NH') OR (c.home.state = 'RI')";
    }

    public static String query_138() {
        return "SELECT DISTINCT Object(c) " +
               "from Customer c " +
               "where c.home.state IN('NH', 'RI')";
    }

    public static String query_139() {
        return "SELECT o " +
                "FROM Customer c JOIN c.orders o JOIN c.address a " +
                "WHERE a.state = 'CA' " +
                "ORDER BY o.quantity DESC, o.totalcost";
    }

    public static String query_140() {
        return "SELECT c " +
               "from Customer c " +
               "where c.home.city IN(:city)";
    }

    public static String query_141() {
        return "Select Distinct Object(o) " +
               "from Order o, in(o.lineItems) l " +
               "where l.quantity NOT IN (1, 5)";
    }

    public static String query_142() {
        return "Select Distinct Object(o) " +
               "FROM Order o " +
               "WHERE o.sampleLineItem MEMBER OF o.lineItems";
    }

    public static String query_143() {
        return "Select Distinct Object(o) " +
               "FROM Order o " +
               "WHERE :param NOT MEMBER o.lineItems";
    }

    public static String query_144() {
        return "Select Distinct Object(o) " +
               "FROM Order o, LineItem l " +
               "WHERE l MEMBER o.lineItems";
    }

    public static String query_145() {
        return "select distinct Object(c) " +
               "FROM Customer c, in(c.aliases) a " +
               "WHERE a.alias LIKE 'sh\\_ll' escape '\\'";
    }

    public static String query_146() {
        return "Select Distinct Object(a) " +
               "FROM Alias a " +
               "WHERE a.customerNoop NOT MEMBER OF a.customersNoop";
    }

    public static String query_147() {
        return "Select Distinct Object(a) " +
               "FROM Alias a " +
               "WHERE a.customerNoop MEMBER OF a.customersNoop";
    }

    public static String query_148() {
        return "Select Distinct Object(a) " +
               "from Alias a " +
               "where LOCATE('ev', a.alias) = 3";
    }

    public static String query_149() {
        return "Select DISTINCT Object(o) " +
               "From Order o " +
               "WHERE o.totalPrice > ABS(:dbl)";
    }

    public static String query_150() {
        return "Select Distinct OBjeCt(a) " +
               "From Alias a " +
               "WHERE LENGTH(a.alias) > 4";
    }

    public static String query_151() {
        return "Select Distinct Object(a) " +
               "From Alias a " +
               "WHERE a.alias = SUBSTRING(:string1, :int2, :int3)";
    }

    public static String query_152() {
        return "Select Distinct Object(a) " +
               "From Alias a " +
               "WHERE a.alias = CONCAT('ste', 'vie')";
    }

    public static String query_153() {
        return "Select Distinct Object(c) " +
               "FROM Customer c " +
               "WHERE c.work.zip IS NOT NULL";
    }

    public static String query_154() {
        return "sELEct dIsTiNcT oBjEcT(c) " +
               "FROM Customer c " +
               "WHERE c.work.zip IS NULL";
    }

    public static String query_155() {
        return "Select Distinct Object(c) " +
               "FROM Customer c " +
               "WHERE c.aliases IS NOT EMPTY";
    }

    public static String query_156() {
        return "Select Distinct Object(c) " +
               "FROM Customer c " +
               "WHERE c.aliases IS EMPTY";
    }

    public static String query_157() {
        return "Select Distinct Object(c) " +
               "FROM Customer c " +
               "WHERE c.home.zip not like '%44_'";
    }

    public static String query_158() {
        return "Select Distinct Object(c) " +
               "FROM Customer c " +
               "WHERE c.home.zip LIKE '%77'";
    }

    public static String query_159() {
        return "Select Distinct Object(c) " +
               "FROM Customer c Left Outer Join c.home h " +
               "WHERE h.city Not iN ('Swansea', 'Brookline')";
    }

    public static String query_160() {
        return "select distinct c " +
               "FROM Customer c " +
               "WHERE c.home.city IN ('Lexington')";
    }

    public static String query_161() {
        return "sElEcT c " +
               "FROM Customer c " +
               "Where c.name = :cName";
    }

    public static String query_162() {
        return "select distinct Object(o) " +
               "From Order o " +
               "WHERE o.creditCard.approved = FALSE";
    }

    public static String query_163() {
        return "SELECT DISTINCT Object(o) " +
               "From Order o " +
               "where o.totalPrice NOT bETwEeN 1000 AND 1200";
    }

    public static String query_164() {
        return "SELECT DISTINCT Object(o) " +
               "From Order o " +
               "where o.totalPrice BETWEEN 1000 AND 1200";
    }

    public static String query_165() {
        return "SELECT DISTINCT Object(o) " +
               "FROM Order o, in(o.lineItems) l " +
               "WHERE l.quantity < 2 AND o.customer.name = 'Robert E. Bissett'";
    }

    public static String query_166() {
        return "select distinct Object(o) " +
               "FROM Order AS o, in(o.lineItems) l " +
               "WHERE (l.quantity < 2) AND ((o.totalPrice < (3 + 54 * 2 + -8)) OR (o.customer.name = 'Robert E. Bissett'))";
    }

    public static String query_167() {
        return "SeLeCt DiStInCt oBjEcT(o) " +
               "FROM Order AS o " +
               "WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice < 100";
    }

    public static String query_168() {
        return "Select Distinct Object(o) " +
               "FROM Order o " +
               "WHERE NOT o.totalPrice < 4500";
    }

    public static String query_169() {
        return "Select DISTINCT Object(P) " +
               "From Product p";
    }

    public static String query_170() {
        return "SELECT DISTINCT c " +
               "from Customer c " +
               "WHERE    c.home.street = :street " +
               "      OR c.home.city   = :city " +
               "      OR c.home.state  = :state " +
               "      or c.home.zip    = :zip";
    }

    public static String query_171() {
        return "Select Distinct Object(c) " +
               "fRoM Customer c, IN(c.creditCards) a " +
               "where a.type = :ccard";
    }

    public static String query_172() {
        return "SELECT c " +
               "from Customer c " +
               "WHERE     c.home.street = :street " +
               "      AND c.home.city   = :city " +
               "      AND c.home.state  = :state " +
               "      and c.home.zip    = :zip";
    }

    public static String query_173() {
        return "Select Distinct Object(c) " +
               "FrOm Customer c, In(c.aliases) a " +
               "WHERE a.alias = :aName";
    }

    public static String query_174() {
        return "Select Distinct Object(c) " +
               "FROM Customer AS c";
    }

    public static String query_175() {
        return "Select Distinct o " +
               "from Order AS o " +
               "WHERE o.customer.name = :name";
    }

    public static String query_176() {
        return "UPDATE Customer c SET c.name = 'CHANGED' " +
               "WHERE c.orders IS NOT EMPTY";
    }

    public static String query_177() {
        return "UPDATE DateTime SET date = CURRENT_DATE";
    }

    public static String query_178() {
        return "SELECT c FROM Customer c " +
               "WHERE c.firstName = :first AND" +
               "      c.lastName = :last";
    }

    public static String query_179() {
        return "SELECT OBJECT ( c ) FROM Customer AS c";
    }

    public static String query_180() {
        return "SELECT c.firstName, c.lastName " +
               "FROM Customer AS c";
    }

    public static String query_181() {
        return "SELECT c.address.city " +
               "FROM Customer AS c";
    }

    public static String query_182() {
        return "SELECT new com.titan.domain.Name(c.firstName, c.lastName) " +
               "FROM Customer c";
    }

    public static String query_183() {
        return "SELECT cbn.ship " +
               "FROM Customer AS c, IN ( c.reservations ) r, IN ( r.cabins ) cbn";
    }

    public static String query_184() {
        return "Select c.firstName, c.lastName, p.number " +
               "From Customer c Left Join c.phoneNumbers p";
    }

    public static String query_185() {
        return "SELECT r " +
               "FROM Reservation AS r " +
               "WHERE (r.amountPaid * .01) > 300.00";
    }

    public static String query_186() {
        return "SELECT s " +
               "FROM Ship AS s " +
               "WHERE s.tonnage >= 80000.00 AND s.tonnage <= 130000.00";
    }

    public static String query_187() {
        return "SELECT r " +
               "FROM Reservation r, IN ( r.customers ) AS cust " +
               "WHERE cust = :specificCustomer";
    }

    public static String query_188() {
        return "SELECT s " +
               "FROM Ship AS s " +
               "WHERE s.tonnage BETWEEN 80000.00 AND 130000.00";
    }

    public static String query_189() {
        return "SELECT s " +
               "FROM Ship AS s " +
               "WHERE s.tonnage NOT BETWEEN 80000.00 AND 130000.00";
    }

    public static String query_190() {
        return "SELECT c " +
               "FROM Customer AS c " +
               "WHERE c.address.state IN ('FL', 'TX', 'MI', 'WI', 'MN')";
    }

    public static String query_191() {
        return "SELECT cab " +
               "FROM Cabin AS cab " +
               "WHERE cab.deckLevel IN (1,3,5,7)";
    }

    public static String query_192() {
        return "SELECT c " +
               "FROM Customer c " +
               "WHERE c.address.state IN(?1, ?2, ?3, 'WI', 'MN')";
    }

    public static String query_193() {
        return "SELECT c " +
               "FROM Customer c " +
               "WHERE c.address IS NULL";
    }

    public static String query_194() {
        return "SELECT c " +
               "FROM Customer c " +
               "WHERE c.address.state = 'TX' AND" +
               "      c.lastName = 'Smith' AND" +
               "      c.firstName = 'John'";
    }

    public static String query_195() {
        return "SELECT crs " +
               "FROM Cruise AS crs, IN(crs.reservations) AS res, Customer AS cust " +
               "WHERE " +
               " cust = :myCustomer " +
               "   AND" +
               " cust MEMBER OF res.customers";
    }

    public static String query_196() {
        return "SELECT c " +
               "FROM Customer AS c " +
               "WHERE " +
               " LENGTH(c.lastName) > 6 " +
               "   AND" +
               " LOCATE( c.lastName, 'Monson' ) > -1";
    }

    public static String query_197() {
        return "SELECT c " +
               "FROM Customer AS C " +
               "ORDER BY c.lastName";
    }

    public static String query_198() {
        return "SELECT c " +
               "FROM Customer AS C " +
               "WHERE c.address.city = 'Boston' AND c.address.state = 'MA' " +
               "ORDER BY c.lastName DESC";
    }

    public static String query_199() {
        return "SELECT cr.name, COUNT (res) " +
               "FROM Cruise cr LEFT JOIN cr.reservations res " +
               "GROUP BY cr.name";
    }

    public static String query_200() {
        return "SELECT cr.name, COUNT (res) " +
               "FROM Cruise cr LEFT JOIN cr.reservations res " +
               "GROUP BY cr.name " +
               "HAVING count(res) > 10";
    }

    public static String query_201() {
        return "SELECT COUNT (res) " +
               "FROM Reservation res " +
               "WHERE res.amountPaid > " +
               "      (SELECT avg(r.amountPaid) FROM Reservation r)";
    }

    public static String query_202() {
        return "SELECT cr " +
               "FROM Cruise cr " +
               "WHERE 100000 < (" +
               "   SELECT SUM(res.amountPaid) FROM cr.reservations res" +
               ")";
    }

    public static String query_203() {
        return "SELECT cr " +
               "FROM Cruise cr " +
               "WHERE 0 < ALL (" +
               "   SELECT res.amountPaid from cr.reservations res" +
               ")";
    }

    public static String query_204() {
        return "UPDATE Reservation res " +
               "SET res.name = 'Pascal' " +
               "WHERE EXISTS (" +
               "   SELECT c FROM res.customers c " +
               "   WHERE c.firstName = 'Bill' AND c.lastName='Burke'" +
               ")";
    }

    public static String query_205() {
        return "SELECT o.quantity, a.zipcode " +
               "FROM Customer c JOIN c.orders o JOIN c.address a " +
               "WHERE a.state = 'CA' " +
               "ORDER BY o.quantity, a.zipcode";
    }

    public static String query_206() {
        return "DELETE " +
               "FROM Customer c " +
               "WHERE c.status = 'inactive'";
    }

    public static String query_207() {
        return "DELETE " +
               "FROM Customer c " +
               "WHERE c.status = 'inactive' " +
               "      AND " +
               "      c.orders IS EMPTY";
    }

    public static String query_208() {
        return "UPDATE customer c " +
               "SET c.status = 'outstanding' " +
               "WHERE c.balance < 10000";
    }

    public static String query_209() {
        return "Select e " +
               "from Employee e join e.phoneNumbers p " +
               "where     e.firstName = 'Bob' " +
               "      and e.lastName like 'Smith%' " +
               "      and e.address.city = 'Toronto' " +
               "      and p.areaCode <> '2'";
    }

    public static String query_210() {
        return "Select e " +
               "From Employee e " +
               "Where Exists(Select a From e.address a Where a.zipCode = 27519)";
    }

    public static String query_211() {
        return "Select e " +
               "From Employee e " +
               "Where Exists(Select e.name From In e.phoneNumbers Where e.zipCode = 27519)";
    }

    public static String query_212() {
        return "UPDATE Employee e SET e.salary = e.salary*(1+(:percent/100)) " +
               "WHERE EXISTS (SELECT p " +
               "              FROM e.projects p " +
               "              WHERE p.name LIKE :projectName)";
    }

    public static String query_213() {
        return "select e_0 " +
               "from Sellexpect e_0 " +
               "where e_0.iSellexpectnr IN (select e_1.iSellexpectnr " +
               "                            from Sellexpectline e_1 " +
               "                            where e_1.iStandversionnr IN (select e_2.iStandversionnr " +
               "                                                          from Standversion e_2 " +
               "                                                          where e_2.iStandnr IN (select e_3.iStandnr " +
               "                                                                                 from Stand e_3 " +
               "                                                                                 where lower(e_3.iStandid) like :e_3_iStandid" +
               "                                                                                )" +
               "                                                         )" +
               "                           )";
    }

    public static String query_214() {
        return "SELECT r " +
               "FROM RuleCondition r " +
               "WHERE     r.ruleType = :ruleType " +
               "      AND r.operator = :operator " +
               "      AND (SELECT Count(rcc) FROM r.components rcc ) = :componentCount  " +
               "      AND (SELECT Count(rc2) FROM r.components rc2 WHERE rc2.componentId IN :componentIds) = :componentCount";
    }

    public static String query_215() {
        return "SELECT a.UUID " +
               "FROM AnyType a LEFT JOIN a.groupUUIDs group39db547fe413463e96c740b6dd6ae178 " +
               "WHERE " +
               "          ((a.UUID IN('2b7667fa-57d0-42ff-964b-fe16b96936d1')))" +
               "      AND " +
               "          ((" +
               "               (" +
               "                    a.groupUUIDs IS EMPTY " +
               "                AND " +
               "                    (a.UUID NOT IN(SELECT nongov0group39db547fe413463e96c740b6dd6ae178t.UUID" +
               "                                   FROM core_AnyEntityType_v1 nongov0group39db547fe413463e96c740b6dd6ae178t " +
               "                                   WHERE     ((nongov0group39db547fe413463e96c740b6dd6ae178t.typeUUID IN('681ee316-a263-4a67-ad35-9fb3d5ae61e6', " +
               "                                                                                                         '67f6d771-d3fd-40c8-860c-f6dfa958fbc1', " +
               "                                                                                                         '8481d0d1-41b7-4b7e-93dd-683a5e17f348', " +
               "                                                                                                         '48cfb0d6-0f81-4cd7-bbf4-585f7285b9bc')))) " +
               "                                         AND " +
               "                                             (a.UUID NOT IN(SELECT nongov0group39db547fe413463e96c740b6dd6ae178p.UUID " +
               "                                                            FROM policy_Policy_v1 nongov0group39db547fe413463e96c740b6dd6ae178p " +
               "                                                            WHERE (nongov0group39db547fe413463e96c740b6dd6ae178p.policyType NOT IN('STATIC_CODE', " +
               "                                                                                                                                   'METADATA', " +
               "                                                                                                                                   'SECURITY', " +
               "                                                                                                                                   'ACTION'))" +
               "                                                           )" +
               "                                             )" +
               "                    )" +
               "               ) " +
               "            OR " +
               "               (((group39db547fe413463e96c740b6dd6ae178 IN(SELECT grant0_g.UUID " +
               "                                                           FROM governance_Governable_v1 grant0_g " +
               "                                                           WHERE 1 = 0" +
               "                                                          )" +
               "               )))" +
               "          ))";
    }
}
