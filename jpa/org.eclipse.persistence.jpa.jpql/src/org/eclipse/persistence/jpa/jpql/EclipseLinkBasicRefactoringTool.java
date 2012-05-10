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
package org.eclipse.persistence.jpa.jpql;

import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;

/**
 * This utility class provides basic refactoring support. This version adds support for EclipseLink
 * specific extension over the default implementation of JPQL defined in the Java Persistence
 * functional specification and it does not changes the {@link org.eclipse.persistence.jpa.jpql.
 * model.query.StateObject StateObject} but rather gather the changes in {@link RefactoringDelta}
 * and it is the responsibility of the invoker to the actual change.
 * <p>
 * Provided functionality:
 * <ul>
 * <li>Renaming an identification variable;</li>
 * <li>Renaming a state field or collection-valued field;</li>
 * <li>Renaming an entity name;</li>
 * <li>Renaming a {@link Class} name (e.g.: in constructor expression);</li>
 * <li>Renaming an {@link Enum} constant.</li>
 * </ul>
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @see DefaultBasicRefactoringTool
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class EclipseLinkBasicRefactoringTool extends BasicRefactoringTool {

	/**
	 * Creates a new <code>EclipseLinkBasicRefactoringTool</code>.
	 *
	 * @param jpqlQuery The JPQL query to manipulate
	 * @param jpqlGrammar The {@link JPQLGrammar} that was used to parse the JPQL query
	 * @param managedTypeProvider The external form of a provider that gives access to the JPA metadata
	 */
	public EclipseLinkBasicRefactoringTool(CharSequence jpqlQuery,
	                                        JPQLGrammar jpqlGrammar,
	                                        IManagedTypeProvider managedTypeProvider) {

		super(jpqlQuery, jpqlGrammar, managedTypeProvider);
	}

	/**
	 * Creates a new <code>EclipseLinkBasicRefactoringTool</code>.
	 *
	 * @param jpqlFragment The JPQL query to manipulate or a single JPQL fragment, which is parsed
	 * using the JPQL query BNF identifier by the given ID
	 * @param jpqlGrammar The {@link JPQLGrammar} that was used to parse the JPQL fragment
	 * @param managedTypeProvider The external form of a provider that gives access to the JPA metadata
	 * @param jpqlQueryBNFId The unique identifier of the {@link org.eclipse.persistence.jpa.jpql.
	 * parser.JPQLQueryBNF JPQLQueryBNF} that determines how to parse the JPQL fragment
	 */
	public EclipseLinkBasicRefactoringTool(CharSequence jpqlFragment,
	                                        JPQLGrammar jpqlGrammar,
	                                        IManagedTypeProvider managedTypeProvider,
	                                        String jpqlQueryBNFId) {

		super(jpqlFragment, jpqlGrammar, managedTypeProvider, jpqlQueryBNFId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JPQLQueryContext buildJPQLQueryContext() {
		return new EclipseLinkJPQLQueryContext(getGrammar());
	}
}