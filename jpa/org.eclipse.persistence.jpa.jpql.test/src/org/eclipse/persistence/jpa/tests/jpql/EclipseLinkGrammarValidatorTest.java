/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle. All rights reserved.
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
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_0;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_1;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_2;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_3;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.junit.Test;

/**
 * The unit-test class used for testing a JPQL query grammatically when the JPA version is 1.0 and
 * 2.0 and EclipseLink is the persistence provider. The EclipseLink version supported is 2.0, 2.1,
 * 2.2 and 2.3.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class EclipseLinkGrammarValidatorTest extends AbstractGrammarValidatorTest {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractGrammarValidator buildValidator() {
		return new EclipseLinkGrammarValidator(jpqlGrammar);
	}

	protected boolean isEclipseLink2_0() {
		return jpqlGrammar.getProviderVersion() == EclipseLinkJPQLGrammar2_0.VERSION;
	}

	protected boolean isEclipseLink2_1() {
		return jpqlGrammar.getProviderVersion() == EclipseLinkJPQLGrammar2_1.VERSION;
	}

	protected boolean isEclipseLink2_2() {
		return jpqlGrammar.getProviderVersion() == EclipseLinkJPQLGrammar2_2.VERSION;
	}

	protected boolean isEclipseLink2_3() {
		return jpqlGrammar.getProviderVersion() == EclipseLinkJPQLGrammar2_3.VERSION;
	}

	protected boolean isEclipseLink2_4() {
		return jpqlGrammar.getProviderVersion() == EclipseLinkJPQLGrammar2_4.VERSION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isJoinFetchIdentifiable() {
		return isEclipseLink2_4();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isSubqueryAllowedAnywhere() {
		return isEclipseLink2_4();
	}

	@Test
	public final void test_OrderByItem_InvalidPath_2() throws Exception {

		String query = "SELECT e FROM Employee e ORDER BY e.name 2";

		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			JPQLQueryProblemMessages.OrderByItem_InvalidPath
		);
	}
}