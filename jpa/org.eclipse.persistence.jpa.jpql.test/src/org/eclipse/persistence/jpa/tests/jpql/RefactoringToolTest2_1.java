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

import static org.junit.Assert.*;

/**
 * The abstract definition of a unit-test that tests {@link org.eclipse.persistence.jpa.jpql.
 * RefactoringTool RefactoringTool} when the JPA version is 2.1.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class RefactoringToolTest2_1 extends AbstractRefactoringToolTest {

	private RefactoringTool buildRefactoringTool(String jpqlQuery) throws Exception {
		return new DefaultRefactoringTool(getPersistenceUnit(), getJPQLQueryBuilder(), jpqlQuery);
	}

	@Test
	public void test_RenameEntity_1() throws Exception {

		String jpqlQuery = "Select e fRoM Employee e JoiN TrEaT(e.projects aS LargeProject) lp";
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameEntityName("LargeProject", "SmallProject");

		String expected = "Select e fRoM Employee e JoiN TrEaT(e.projects aS SmallProject) lp";
		assertEquals(expected, refactoringTool.toActualText());
	}
}