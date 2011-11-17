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

/**
 * This validator is responsible to gather the problems found in a JPQL query by validating it
 * against the JPQL grammar. The semantic is not validated by this visitor.
 *
 * @see DefaultSemanticValidator
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class DefaultGrammarValidator extends AbstractGrammarValidator {

	/**
	 * Creates a new <code>DefaultGrammarValidator</code>.
	 *
	 * @param context The context used to query information about the JPQL query
	 * @exception AssertException The {@link JPQLQueryContext} cannot be <code>null</code>
	 */
	public DefaultGrammarValidator(JPQLQueryContext context) {
		super(context);
	}
}