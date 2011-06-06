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

import org.eclipse.persistence.jpa.internal.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;

/**
 * A problem describes an issue found in a JPQL query because it is either grammatically or
 * semantically incorrect.
 *
 * @see AbstractJPQLQueryHelper#validate()
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public interface JPQLQueryProblem {

	/**
	 * Returns the position from where the problem ends, inclusively.
	 *
	 * @return The position of the last character that was found as problematic within the JPQL query,
	 * inclusively
	 */
	int getEndPosition();

	/**
	 * Returns the parsed tree representing the JPQL query.
	 *
	 * @return The parsed tree representing the JPQL query
	 */
	Expression getExpression();

	/**
	 * Returns the arguments associate with the problem's message.
	 *
	 * @return A non-<code>null</code> list of arguments that can be used to format the localized
	 * message
	 */
	String[] getMessageArguments();

	/**
	 * Returns the resource bundle key used to retrieve the localized message.
	 *
	 * @return The key used to retrieve the localized message
	 */
	String getMessageKey();

	/**
	 * Returns the named query for which this problem was created.
	 *
	 * @return The named query that was validated
	 */
	IQuery getQuery();

	/**
	 * Returns the position from where the problem starts.
	 *
	 * @return The position of the first character that was found as problematic within the JPQL
	 * query, inclusively
	 */
	int getStartPosition();
}