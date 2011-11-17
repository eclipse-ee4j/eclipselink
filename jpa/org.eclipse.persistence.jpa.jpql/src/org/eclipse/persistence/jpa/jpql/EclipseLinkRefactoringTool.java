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
package org.eclipse.persistence.jpa.jpql;

import org.eclipse.persistence.jpa.jpql.model.EclipseLinkActualJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.model.IJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.model.IJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.model.query.EclipseLinkStateObjectVisitor;
import org.eclipse.persistence.jpa.jpql.model.query.FuncExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.TreatExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;

/**
 * This refactoring tool add support for EclipseLink specific extension over the default
 * implementation of JPQL defined in the Java Persistence functional specification.
 * <p>
 * Provided functionality:
 * <ul>
 * <li>Renaming an identification variable;</li>
 * <li>Renaming a state field or collection-valued field;</li>
 * <li>Renaming an entity name;</li>
 * <li>Renaming a {@link Class} name (e.g.: in constructor expression);</li>
 * <li>Renaming an {@link Enum} constant.</li>
 * </ul>
 *
 * @see DefaultRefactoringTools
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class EclipseLinkRefactoringTool extends RefactoringTool {

	/**
	 * Creates a new <code>EclipseLinkRefactoringTool</code>.
	 *
	 * @param managedTypeProvider The external form of a provider that gives access to the JPA metadata
	 * @param jpqlQueryBuilder The builder that creates the {@link StateObject} representation of the
	 * JPQL query
	 * @param jpqlQuery The JPQL query to manipulate
	 */
	public EclipseLinkRefactoringTool(IManagedTypeProvider managedTypeProvider,
	                                  IJPQLQueryBuilder jpqlQueryBuilder,
	                                  CharSequence jpqlQuery) {

		super(managedTypeProvider, jpqlQueryBuilder, jpqlQuery);
	}

	/**
	 * Creates a new <code>EclipseLinkRefactoringTool</code>.
	 *
	 * @param managedTypeProvider The external form of a provider that gives access to the JPA metadata
	 * @param jpqlQueryBuilder The builder that creates the {@link StateObject} representation of the
	 * JPQL query
	 * @param jpqlFragment The JPQL query to manipulate or a single JPQL fragment, which is parsed
	 * using the JPQL query BNF identifier by the given ID
	 * @param jpqlQueryBNFId The unique identifier of the {@link org.eclipse.persistence.jpa.jpql.
	 * parser.JPQLQueryBNF JPQLQueryBNF} that determines how to parse the JPQL fragment
	 */
	public EclipseLinkRefactoringTool(IManagedTypeProvider managedTypeProvider,
	                                  IJPQLQueryBuilder jpqlQueryBuilder,
	                                  CharSequence jpqlFragment,
	                                  String jpqlQueryBNFId) {

		super(managedTypeProvider, jpqlQueryBuilder, jpqlFragment, jpqlQueryBNFId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EntityNameRenamer buildEntityNameRenamer(String oldEntityName, String newEntityName) {
		return new EclipseLinkEntityNameRenamer(oldEntityName, newEntityName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IJPQLQueryFormatter buildFormatter() {
		return new EclipseLinkActualJPQLQueryFormatter(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JPQLQueryContext buildJPQLQueryContext() {
		return new EclipseLinkJPQLQueryContext(getGrammar());
	}

	protected class EclipseLinkEntityNameRenamer extends EntityNameRenamer
	                                             implements EclipseLinkStateObjectVisitor {

		/**
		 * Creates a new <code>EclipseLinkEntityNameRenamer</code>.
		 *
		 * @param oldEntityName The current name of the entity to rename
		 * @param newEntityName The new name of the entity
		 */
		public EclipseLinkEntityNameRenamer(String oldEntityName, String newEntityName) {
			super(oldEntityName, newEntityName);
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(FuncExpressionStateObject stateObject) {
			// Nothing to do
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(TreatExpressionStateObject stateObject) {
			if (oldEntityName.equals(stateObject.getEntityTypeName())) {
				stateObject.setEntityTypeName(newEntityName);
			}
		}
	}
}