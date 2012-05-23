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

import org.eclipse.persistence.jpa.jpql.DefaultRefactoringTool;
import org.eclipse.persistence.jpa.jpql.RefactoringTool;
import org.junit.Test;

import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries2_0.*;
import static org.junit.Assert.*;

/**
 * The abstract definition of a unit-test that tests {@link org.eclipse.persistence.jpa.jpql.
 * RefactoringTool RefactoringTool} when the JPA version is 2.0.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class RefactoringToolTest2_0 extends AbstractRefactoringToolTest {

	private RefactoringTool buildRefactoringTool(String jpqlQuery) throws Exception {
		return new DefaultRefactoringTool(getPersistenceUnit(), getJPQLQueryBuilder(), jpqlQuery);
	}

	@Test
	public void test_RenameEntityName_1() throws Exception {

		// SELECT TYPE(employee) FROM Employee employee WHERE TYPE(employee) <> Exempt
		String jpqlQuery = query_007();
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameEntityName("Exempt", "Exemption");

		String expected = "SELECT TYPE(employee) FROM Employee employee WHERE TYPE(employee) <> Exemption";
		assertEquals(expected, refactoringTool.toActualText());
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
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		refactoringTool.renameEntityName("Exempt", "Exemption");
		String expected = "SELECT e.name, CASE TYPE(e) WHEN Exemption THEN 'Exempt' WHEN Contractor THEN 'Contractor' WHEN Intern THEN 'Intern' ELSE 'NonExempt' END FROM Employee e, Contractor c WHERE e.dept.name = 'Engineering'";
		assertEquals(expected, refactoringTool.toActualText());

		refactoringTool.renameEntityName("Contractor", "Manager");
		expected = "SELECT e.name, CASE TYPE(e) WHEN Exemption THEN 'Exempt' WHEN Manager THEN 'Contractor' WHEN Intern THEN 'Intern' ELSE 'NonExempt' END FROM Employee e, Manager c WHERE e.dept.name = 'Engineering'";
		assertEquals(expected, refactoringTool.toActualText());
	}

	@Test
	public void test_RenameResultVariable_1() throws Exception {

		String jpqlQuery = "SELECT NEW java.util.Vector(a.employees) AS u FROM Address A";
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameResultVariable("u", "v");

		String expected = "SELECT NEW java.util.Vector(a.employees) AS v FROM Address A";
		assertEquals(expected, refactoringTool.toActualText());
	}

	@Test
	public void test_RenameResultVariable_2() throws Exception {

		String jpqlQuery = "SELECT e.name AS n FROM Employee e";
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameResultVariable("u", "v");

		assertEquals(jpqlQuery, refactoringTool.toActualText());
	}

	@Test
	public void test_RenameResultVariable_3() throws Exception {

		String jpqlQuery = "SELECT e.name AS n, 2 + 2 o FROM Address A";
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameResultVariable("o", "value");

		String expected = "SELECT e.name AS n, 2 + 2 value FROM Address A";
		assertEquals(expected, refactoringTool.toActualText());
	}

	@Test
	public void test_RenameResultVariable_4() throws Exception {

		String jpqlQuery = "SELECT e.name AS n, e.age a FROM Address A ORDER BY n";
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameResultVariable("n", "name");

		String expected = "SELECT e.name AS name, e.age a FROM Address A ORDER BY name";
		assertEquals(expected, refactoringTool.toActualText());
	}

	@Test
	public void test_RenameResultVariable_5() throws Exception {

		String jpqlQuery = "SELECT e.name AS n, e.age a FROM Address A ORDER BY n, a";
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameResultVariable("n", "name");
		refactoringTool.renameResultVariable("a", "age");

		String expected = "SELECT e.name AS name, e.age age FROM Address A ORDER BY name, age";
		assertEquals(expected, refactoringTool.toActualText());
	}
}