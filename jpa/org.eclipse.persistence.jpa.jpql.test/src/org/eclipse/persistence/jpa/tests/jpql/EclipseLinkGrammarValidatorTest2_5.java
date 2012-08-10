/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.util.List;
import org.eclipse.persistence.jpa.jpql.AbstractGrammarValidator;
import org.eclipse.persistence.jpa.jpql.EclipseLinkGrammarValidator;
import org.eclipse.persistence.jpa.jpql.EclipseLinkVersion;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.junit.Test;

/**
 * The unit-test class used for testing a JPQL query grammatically when the JPA version is 2.1 and
 * EclipseLink is the persistence provider. The EclipseLink version supported is only 2.5.
 *
 * @see EclipseLinkGrammarValidator
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class EclipseLinkGrammarValidatorTest2_5 extends AbstractGrammarValidatorTest {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractGrammarValidator buildValidator() {
		return new EclipseLinkGrammarValidator(jpqlGrammar);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isJoinFetchIdentifiable() {
		return true;
	}

	protected boolean isNewerThanOrEqual(EclipseLinkVersion version) {
		EclipseLinkVersion currentVersion = EclipseLinkVersion.value(jpqlGrammar.getProviderVersion());
		return currentVersion.isNewerThanOrEqual(version);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isSubqueryAllowedAnywhere() {
		return isNewerThanOrEqual(EclipseLinkVersion.VERSION_2_4);
	}

	@Test
	public void test_HQL_Query_001() throws Exception {

		String jpqlQuery = "FROM Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testHasNoProblems(problems);
	}

	@Test
	public void test_HQL_Query_002() throws Exception {

		String jpqlQuery = "FROM Employee e WHERE e.name = 'JPQL'";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testHasNoProblems(problems);
	}
}