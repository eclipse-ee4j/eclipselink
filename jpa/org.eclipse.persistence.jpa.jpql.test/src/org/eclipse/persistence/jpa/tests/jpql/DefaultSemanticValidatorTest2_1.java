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

import java.util.List;
import org.eclipse.persistence.jpa.jpql.AbstractSemanticValidator;
import org.eclipse.persistence.jpa.jpql.DefaultJPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.DefaultSemanticValidator;
import org.eclipse.persistence.jpa.jpql.JPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages;
import org.junit.Test;

/**
 * The unit-test class used for testing a JPQL query semantically when the JPA version is 2.1.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class DefaultSemanticValidatorTest2_1 extends AbstractSemanticValidatorTest {

	@Override
	protected JPQLQueryContext buildQueryContext() {
		return new DefaultJPQLQueryContext(jpqlGrammar);
	}

	@Override
	protected AbstractSemanticValidator buildValidator() {
		return new DefaultSemanticValidator(buildSemanticValidatorHelper());
	}

	@Override
	protected boolean isPathExpressionToCollectionMappingAllowed() {
		return false;
	}

	@Test
	public final void test_EntityTypeLiteral_NotResolvable_1() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e TREAT(e.phoneNumbers AS Phone) AS ee";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testDoesNotHaveProblem(
			problems,
			JPQLQueryProblemMessages.EntityTypeLiteral_NotResolvable
		);
	}

	@Test
	public final void test_EntityTypeLiteral_NotResolvable_2() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e JOIN TREAT(e.phoneNumbers AS Phone2) AS ee";
		int startPosition = "SELECT e FROM Employee e JOIN TREAT(e.phoneNumbers AS ".length();
		int endPosition   = "SELECT e FROM Employee e JOIN TREAT(e.phoneNumbers AS Phone2".length();

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.EntityTypeLiteral_NotResolvable,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ComplextPathExpression_01() throws Exception {

		String jpqlQuery = "SELECT TREAT(p.project LargeProject) FROM Product p";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_ComplextPathExpression_02() throws Exception {

		String jpqlQuery = "SELECT TREAT(TREAT(p.project LargeProject).parent AS LargeProject).endDate FROM Product p";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testHasNoProblems(problems);
	}
}