/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle. All rights reserved.
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
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages;
import org.junit.Test;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class DefaultSemanticValidatorTest extends AbstractSemanticValidatorTest {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean shouldValidateType() {
		return true;
	}

	@Test
	public void test_NullComparisonExpression_InvalidType_2() throws Exception {

		String query = "SELECT a FROM Address a WHERE a.zip IS NOT NULL";
		int startPosition = "SELECT a FROM Address a WHERE ".length();
		int endPosition   = "SELECT a FROM Address a WHERE a.zip".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.NullComparisonExpression_InvalidType,
			startPosition,
			endPosition
		);
	}
}