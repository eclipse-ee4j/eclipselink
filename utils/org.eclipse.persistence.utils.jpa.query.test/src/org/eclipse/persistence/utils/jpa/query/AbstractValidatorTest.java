/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query;

import java.util.List;
import java.util.ListResourceBundle;
import java.util.ResourceBundle;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractValidator;
import org.eclipse.persistence.utils.jpa.query.parser.JPQLExpression;
import org.eclipse.persistence.utils.jpa.query.parser.JPQLTests;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public abstract class AbstractValidatorTest extends AbstractQueryTest
{
	abstract IQuery buildExternalQuery(String query) throws Exception;

	private CharSequence buildProblemMessages(List<QueryProblem> problems)
	{
		StringBuilder sb = new StringBuilder();

		for (QueryProblem problem : problems)
		{
			sb.append(problem.getMessageKey());
			sb.append(", ");
		}

		return sb;
	}

	abstract AbstractValidator buildValidationVisitor(IQuery query);

	private ResourceBundle loadResourceBundle()
	{
		return ListResourceBundle.getBundle(QueryProblemsResourceBundle.class.getName());
	}

	final void testDoesNotHaveProblem(List<QueryProblem> problems, String messageKey)
	{
		if (problems.isEmpty())
		{
			return;
		}

		for (QueryProblem problem : problems)
		{
			assertFalse
			(
				messageKey + " should not be part of the list problems",
				problem.getMessageKey() == messageKey
			);
		}
	}

	final void testHasNoProblems(List<QueryProblem> problems)
	{
		assertTrue
		(
			"The list of problems should be empty: " + buildProblemMessages(problems),
			problems.isEmpty()
		);
	}

	final void testHasOnlyOneProblem(List<QueryProblem> problems,
	                                 String messageKey,
	                                 int startPosition,
	                                 int endPosition)
	{
		assertFalse
		(
			"The problem " + messageKey + " was not added to the list",
			problems.isEmpty()
		);

		assertEquals
		(
			"More than one problem was found: " + buildProblemMessages(problems),
			1,
			problems.size()
		);

		QueryProblem problem = problems.get(0);

		assertSame
		(
			"The list of problems does not contain " + messageKey,
			messageKey,
			problem.getMessageKey()
		);

		assertEquals
		(
			"The start position was not calculating correctly",
			startPosition,
			problem.getStartPosition()
		);

		assertEquals
		(
			"The end position was not calculating correctly",
			endPosition,
			problem.getEndPosition()
		);

		assertTrue
		(
			messageKey + " was not added to the resource bundle",
			loadResourceBundle().containsKey(messageKey)
		);
	}

	@SuppressWarnings("null")
	final void testHasProblem(List<QueryProblem> problems,
	                          String messageKey,
	                          int startPosition,
	                          int endPosition)
	{
		QueryProblem problem = null;

		for (QueryProblem queryProblem : problems)
		{
			if (messageKey == queryProblem.getMessageKey())
			{
				problem = queryProblem;
			}
		}

		assertNotNull
		(
			"The problem was not added to the list: " + messageKey,
			problem
		);

		assertEquals
		(
			"The start position was not calculating correctly",
			startPosition,
			problem.getStartPosition()
		);

		assertEquals
		(
			"The end position was not calculating correctly",
			endPosition,
			problem.getEndPosition()
		);
	}

	final void testHasProblems(List<QueryProblem> problems,
	                           String[] messageKeys,
	                           int[] startPositions,
	                           int[] endPositions)
	{
		for (int index = 0; index < messageKeys.length; index++)
		{
			String messageKey = messageKeys[index];
			boolean found = false;

			for (QueryProblem problem : problems)
			{
				if (messageKey            == problem.getMessageKey()    &&
				    startPositions[index] == problem.getStartPosition() &&
				    endPositions[index]   == problem.getEndPosition())
				{
					found = true;
					break;
				}
			}

			if (!found)
			{
				for (QueryProblem problem : problems)
				{
					if (messageKey            == problem.getMessageKey() &&
					    startPositions[index] == problem.getStartPosition())
					{
						assertEquals
						(
							"The end position was not calculating correctly",
							endPositions[index],
							problem.getEndPosition()
						);
					}

					if (messageKey          == problem.getMessageKey() &&
					    endPositions[index] == problem.getEndPosition())
					{
						assertEquals
						(
							"The start position was not calculating correctly",
							startPositions[index],
							problem.getStartPosition()
						);
					}
				}
			}
		}
	}

	/**
	 * Validates the given named query and returns the list of {@link QueryProblem problems}.
	 *
	 * @param query The query to validate
	 * @return Either an empty list if validation didn't find any problem or the list of
	 * {@link QueryProblem problems}
	 */
	final List<QueryProblem> validate(String query) throws Exception
	{
		return validate(query, JPQLTests.QueryStringFormatter.DEFAULT);
	}

	/**
	 * Validates the given named query and returns the list of {@link QueryProblem problems}.
	 *
	 * @param query The query to validate
	 * @param formatter The formatter used to update the format of the query when validating
	 * the parsed tree
	 * @return Either an empty list if validation didn't find any problem or the list of
	 * {@link QueryProblem problems}
	 */
	final List<QueryProblem> validate(String query, JPQLTests.QueryStringFormatter formatter) throws Exception
	{
		IQuery externalQuery = buildExternalQuery(query);
		assertNotNull("IQuery could not be created for '" + query + "'", externalQuery);

		JPQLExpression jpqlExpression = JPQLTests.buildQuery(externalQuery.getExpression(), formatter);

		AbstractValidator visitor = buildValidationVisitor(externalQuery);
		jpqlExpression.accept(visitor);

		return visitor.problems();
	}
}