/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql;

import java.util.Arrays;
import org.eclipse.persistence.jpa.jpql.parser.Expression;

/**
 * The default implementation of {@link JPQLQueryProblem}.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class DefaultJPQLQueryProblem implements JPQLQueryProblem {

	/**
	 * The position where the problem ends, inclusively.
	 */
	private int endIndex;

	/**
	 * The {@link Expression} that is either not following the BNF grammar or that has semantic problems.
	 */
	private Expression expression;

	/**
	 * The list of arguments that can be used to format the localized description of the problem.
	 */
	private String[] messageArguments;

	/**
	 * The key used to retrieve the localized message describing the problem.
	 */
	private String messageKey;

	/**
	 * The position where the problem ends.
	 */
	private int startIndex;

	/**
	 * Creates a new <code>DefaultJPQLQueryProblem</code>.
	 *
	 * @param expression The {@link Expression} that is either not following the BNF grammar or that
	 * has semantic problems
	 * @param startIndex The position where the problem was encountered
	 * @param endIndex The position where the problem ends, inclusively
	 * @param messageKey The key used to retrieve the localized message describing the problem
	 * @param messageArguments The list of arguments that can be used to format the localized
	 * description of the problem
	 */
	DefaultJPQLQueryProblem(Expression expression,
	                        int startIndex,
	                        int endIndex,
	                        String messageKey,
	                        String... messageArguments) {
		super();

		this.startIndex       = startIndex;
		this.endIndex         = endIndex;
		this.expression       = expression;
		this.messageKey       = messageKey;
		this.messageArguments = messageArguments;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getEndPosition() {
		return endIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression getExpression() {
		return expression;
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getMessageArguments() {
		return messageArguments;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getMessageKey() {
		return messageKey;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getStartPosition() {
		return startIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("messageKey=");
		sb.append(messageKey);
		sb.append(", messageArguments=");
		sb.append(Arrays.toString(messageArguments));
		sb.append(", position=[");
		sb.append(startIndex);
		sb.append(", ");
		sb.append(endIndex);
		sb.append("], query=");
		sb.append(expression);
		return sb.toString();
	}
}