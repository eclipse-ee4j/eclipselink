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
package org.eclipse.persistence.jpa.tests.jpql;

import org.eclipse.persistence.jpa.jpql.DefaultRefactoringTool;
import org.eclipse.persistence.jpa.jpql.RefactoringTool;
import org.junit.Test;

import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries.*;
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

		// SELECT TYPE(e) FROM Employee e WHERE TYPE(e) <> Exempt
		String jpqlQuery = query_211();
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameEntityName("Exempt", "Exemption");

		String expected = "SELECT TYPE(e) FROM Employee e WHERE TYPE(e) <> Exemption";
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
		// FROM Employee e
		// WHERE e.dept.name = 'Engineering'

		String jpqlQuery = query_206();
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		refactoringTool.renameEntityName("Exempt", "Exemption");
		String expected = "SELECT e.name, CASE TYPE(e) WHEN Exemption THEN 'Exempt' WHEN Contractor THEN 'Contractor' WHEN Intern THEN 'Intern' ELSE 'NonExempt' END FROM Employee e WHERE e.dept.name = 'Engineering'";
		assertEquals(expected, refactoringTool.toActualText());

		refactoringTool.renameEntityName("Contractor", "Manager");
		expected = "SELECT e.name, CASE TYPE(e) WHEN Exemption THEN 'Exempt' WHEN Manager THEN 'Contractor' WHEN Intern THEN 'Intern' ELSE 'NonExempt' END FROM Employee e WHERE e.dept.name = 'Engineering'";
		assertEquals(expected, refactoringTool.toActualText());
	}
}