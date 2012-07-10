/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql;

import org.eclipse.persistence.jpa.jpql.BasicRefactoringTool;
import org.eclipse.persistence.jpa.jpql.DefaultBasicRefactoringTool;
import org.junit.Test;

import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries2_0.*;

/**
 * The abstract definition of a unit-test that tests {@link org.eclipse.persistence.jpa.jpql.
 * BasicRefactoringTool BasicRefactoringTool} when the JPA version is 2.0.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class BasicRefactoringToolTest2_0 extends AbstractBasicRefactoringToolTest {

	private BasicRefactoringTool buildRefactoringTool(String jpqlQuery) throws Exception {
		return new DefaultBasicRefactoringTool(jpqlQuery, getGrammar(), getPersistenceUnit());
	}

	@Test
	public void test_RenameEntityName_1() throws Exception {

		// SELECT TYPE(employee) FROM Employee employee WHERE TYPE(employee) <> Exempt
		String jpqlQuery = query_007();
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		int offset = "SELECT TYPE(employee) FROM Employee employee WHERE TYPE(employee) <> ".length();
		String oldValue = "Exempt";
		String newValue = "Exemption";
		refactoringTool.renameEntityName(oldValue, newValue);

		String expected = "SELECT TYPE(employee) FROM Employee employee WHERE TYPE(employee) <> Exemption";
		testChange(refactoringTool, jpqlQuery, expected, offset, oldValue, newValue);
	}

	@Test
	public void test_RenameEntityName_2() throws Exception {

		// SELECT e.name,
		//        CASE TYPE(e) WHEN Exempt THEN 'Exempt'
		//                     WHEN Contractor THEN 'Contractor'
		//                     WHEN Intern THEN 'Intern'
		//                     ELSE 'NonExempt'
		//        END
		// FROM Employee e, Contractor c
		// WHERE e.dept.name = 'Engineering'

		String jpqlQuery = query_002();
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		int offset1 = jpqlQuery.indexOf("Contractor THEN");
		int offset2 = jpqlQuery.indexOf("Contractor c");
		String oldValue1 = "Contractor";
		String newValue1 = "Manager";
		refactoringTool.renameEntityName(oldValue1, newValue1);

		int offset3 = jpqlQuery.indexOf("Exempt THEN");
		String oldValue2 = "Exempt";
		String newValue2 = "Exemption";
		refactoringTool.renameEntityName(oldValue2, newValue2);

		int offset4 = jpqlQuery.indexOf("Employee e");
		String oldValue3 = "Employee";
		String newValue3 = "Address";
		refactoringTool.renameEntityName(oldValue3, newValue3);

		String expected = "SELECT e.name, " +
		                  "       CASE TYPE(e) WHEN Exemption THEN 'Exempt' " +
		                  "                    WHEN Manager THEN 'Contractor' " +
		                  "                    WHEN Intern THEN 'Intern' " +
		                  "                    ELSE 'NonExempt' " +
		                  "       END " +
		                  "FROM Address e, Manager c " +
		                  "WHERE e.dept.name = 'Engineering'";

		testChanges(
			refactoringTool,
			jpqlQuery,
			expected,
			new int[]    {   offset2,   offset4,   offset1,   offset3 },
			new String[] { oldValue1, oldValue3, oldValue1, oldValue2 },
			new String[] { newValue1, newValue3, newValue1, newValue2 }
		);
	}

	@Test
	public void test_RenameEntityName_3() throws Exception {

		// SELECT e FROM Employee e WHERE TYPE(e) IN (Exempt, Contractor)
		String jpqlQuery = query_004();
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		int offset = "SELECT e FROM Employee e WHERE TYPE(e) IN (Exempt, ".length();
		String oldValue = "Contractor";
		String newValue = "GeneralContractor";
		refactoringTool.renameEntityName(oldValue, newValue);

		String expected = "SELECT e FROM Employee e WHERE TYPE(e) IN (Exempt, GeneralContractor)";
		testChange(refactoringTool, jpqlQuery, expected, offset, oldValue, newValue);
	}

	@Test
	public void test_RenameEntityName_4() throws Exception {

		// SELECT TYPE(employee) FROM Employee employee WHERE TYPE(employee) <> Exempt
		String jpqlQuery = query_007();
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		int offset = "SELECT TYPE(employee) FROM ".length();
		String oldValue = "Employee";
		String newValue = "Address";
		refactoringTool.renameEntityName(oldValue, newValue);

		String expected = "SELECT TYPE(employee) FROM Address employee WHERE TYPE(employee) <> Exempt";
		testChange(refactoringTool, jpqlQuery, expected, offset, oldValue, newValue);
	}

	@Test
	public void test_RenameEntityName_5() throws Exception {

		String jpqlQuery = "SELECT p FROM Project p WHERE TYPE(p) <> LargeProject";
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		int offset = "SELECT p FROM Project p WHERE TYPE(p) <> ".length();
		String oldValue = "LargeProject";
		String newValue = "LProject";
		refactoringTool.renameEntityName(oldValue, newValue);

		String expected = "SELECT p FROM Project p WHERE TYPE(p) <> LProject";
		testChange(refactoringTool, jpqlQuery, expected, offset, oldValue, newValue);
	}

	@Test
	public void test_RenameResultVariable_1() throws Exception {

		String jpqlQuery = "SELECT NEW java.util.Vector(a.employees) AS u FROM Address A ORDER BY u";
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		int offset1 = "SELECT NEW java.util.Vector(a.employees) AS ".length();
		int offset2 = "SELECT NEW java.util.Vector(a.employees) AS u FROM Address A ORDER BY ".length();
		String oldValue = "u";
		String newValue = "v";
		refactoringTool.renameResultVariable(oldValue, newValue);

		String expected = "SELECT NEW java.util.Vector(a.employees) AS v FROM Address A ORDER BY v";
		testChanges(refactoringTool, jpqlQuery, expected, oldValue, newValue, offset2, offset1);
	}

	@Test
	public void test_RenameResultVariable_2() throws Exception {

		String jpqlQuery = "SELECT e.name AS n FROM Employee e";
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		String oldValue = "u";
		String newValue = "v";
		refactoringTool.renameResultVariable(oldValue, newValue);

		testHasNoChanges(refactoringTool);
	}

	@Test
	public void test_RenameResultVariable_3() throws Exception {

		String jpqlQuery = "SELECT e.name AS n, 2 + 2 o FROM Address A";
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		int offset = "SELECT e.name AS n, 2 + 2 ".length();
		String oldValue = "o";
		String newValue = "value";
		refactoringTool.renameResultVariable(oldValue, newValue);

		String expected = "SELECT e.name AS n, 2 + 2 value FROM Address A";
		testChange(refactoringTool, jpqlQuery, expected, offset, oldValue, newValue);
	}

	@Test
	public void test_RenameResultVariable_4() throws Exception {

		String jpqlQuery = "SELECT e.name AS n, e.age a FROM Address A ORDER BY n";
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		int offset1 = "SELECT e.name AS ".length();
		int offset2 = "SELECT e.name AS n, e.age a FROM Address A ORDER BY ".length();
		String oldValue = "n";
		String newValue = "name";
		refactoringTool.renameResultVariable(oldValue, newValue);

		String expected = "SELECT e.name AS name, e.age a FROM Address A ORDER BY name";
		testChanges(refactoringTool, jpqlQuery, expected, oldValue, newValue, offset2, offset1);
	}

	@Test
	public void test_RenameResultVariable_5() throws Exception {

		String jpqlQuery = "SELECT e.name AS n, e.age a FROM Address A ORDER BY n, a";
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		int offset1 = "SELECT e.name AS ".length();
		int offset2 = "SELECT e.name AS n, e.age a FROM Address A ORDER BY ".length();
		String oldValue1 = "n";
		String newValue1 = "name";
		refactoringTool.renameResultVariable(oldValue1, newValue1);

		int offset3 = "SELECT e.name AS n, e.age ".length();
		int offset4 = "SELECT e.name AS n, e.age a FROM Address A ORDER BY n, ".length();
		String oldValue2 = "a";
		String newValue2 = "age";
		refactoringTool.renameResultVariable(oldValue2, newValue2);

		String expected = "SELECT e.name AS name, e.age age FROM Address A ORDER BY name, age";

		testChanges(
			refactoringTool,
			jpqlQuery,
			expected,
			new int[]    {   offset4,   offset2,   offset3,   offset1 },
			new String[] { oldValue2, oldValue1, oldValue2, oldValue1 },
			new String[] { newValue2, newValue1, newValue2, newValue1 }
		);
	}
}