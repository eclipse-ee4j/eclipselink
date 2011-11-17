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
package org.eclipse.persistence.jpa.jpql.model;

import org.eclipse.persistence.jpa.jpql.model.query.AbstractConditionalClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.JPQLQueryStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SimpleSelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateObject;
import org.eclipse.persistence.jpa.jpql.model.query.UpdateItemStateObject;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;

/**
 * This builder is responsible to create an editable {@link StateObject} representation of a JPQL
 * query.
 * <p>
 * Some default implementations are available:
 * <ul>
 * <li>Generic JPA 1.0: {@link JPQLQueryBuilder1_0}</li>
 * <li>Generic JPA 2.0: {@link JPQLQueryBuilder2_0}</li>
 * <li>JPA 1.0/EclipseLink 1.x: {@link EclipseLinkJPQLQueryBuilder1}</li>
 * <li>JPA 2.0/EclipseLink 2.0: {@link EclipseLinkJPQLQueryBuilder2_0}</li>
 * <li>JPA 2.0/EclipseLink 2.1 or later: {@link EclipseLinkJPQLQueryBuilder2_1}</li>
 * </ul>
 *
 * @see IManagedTypeProvider
 * @see StateObject
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface IJPQLQueryBuilder {

	/**
	 * Creates a builder that can create a <code><b>CASE</b></code> expression programmatically. Once
	 * the expression is complete, {@link ICaseExpressionStateObjectBuilder#build()} will return the
	 * result.
	 *
	 * @param parent The {@link StateObject} that will be the parent of the newly created model
	 * @return The builder of a <code><b>CASE</b></code> expression
	 */
	ICaseExpressionStateObjectBuilder buildCaseExpressionStateObjectBuilder(StateObject parent);

	/**
	 * Creates a state model representation of a JPQL query that can be edited.
	 *
	 * @param provider The provider of managed types
	 * @param jpqlQuery The JPQL query to parse into a {@link StateObject} model
	 * @param tolerant Determines if the parsing system should be tolerant, meaning if it should try
	 * to parse invalid or incomplete queries
	 * @return The root of the {@link StateObject} model that represents the edited form of the JPQL query
	 */
	JPQLQueryStateObject buildStateObject(IManagedTypeProvider provider,
	                                      CharSequence jpqlQuery,
	                                      boolean tolerant);

	/**
	 * Creates a state model representation of a JPQL query that can be edited.
	 *
	 * @param provider The provider of managed types
	 * @param jpqlQuery The JPQL query to parse into a {@link StateObject} model
	 * @param queryBNFId The unique identifier of the query BNF that will be used to parse the fragment
	 * @param tolerant Determines if the parsing system should be tolerant, meaning if it should try
	 * to parse invalid or incomplete queries
	 * @return The root of the {@link StateObject} model that represents the edited form of the JPQL query
	 */
	JPQLQueryStateObject buildStateObject(IManagedTypeProvider provider,
	                                      CharSequence jpqlQuery,
	                                      String queryBNFId,
	                                      boolean tolerant);

	/**
	 * Creates a {@link StateObject} representation of the given JPQL fragment. In order to properly
	 * parse the fragment, the given unique identifier of the {@link org.eclipse.persistence.jpa.jpql.
	 * parser.JPQLQueryBNF JPQLQueryBNF} will determine how to parse it.
	 * <p>
	 * It is possible the given JPQL fragment has more than one expression, in this case, parsing
	 * should stop at the first comma (x, y) or space (x y) where x and y are two separate expressions.
	 *
	 * @param parent The {@link StateObject} that will be the parent of the newly created model
	 * @param jpqlFragment A portion of a JPQL query that will be parsed and the {@link StateObject}
	 * representation will be created
	 * @param queryBNFId The unique identifier of the query BNF that will be used to parse the fragment
	 * @return The {@link StateObject} representation of the given JPQL fragment
	 */
	StateObject buildStateObject(StateObject parent, CharSequence jpqlFragment, String queryBNFId);

	/**
	 * Creates a builder that can create a conditional expression programmatically. Once the
	 * expression is complete, {@link IConditionalStateObjectBuilder#commit()} will push the result
	 * onto the given state object.
	 *
	 * @param stateObject The clause for which a conditional expression can be created
	 * @return The builder of a conditional expression
	 */
	IConditionalExpressionStateObjectBuilder buildStateObjectBuilder(AbstractConditionalClauseStateObject stateObject);

	/**
	 * Creates a builder that can create a select expression programmatically. Once the expression is
	 * complete, {@link ISelectExpressionBuilder#commit()} will push the result onto the given state
	 * object.
	 *
	 * @param stateObject The clause for which one or many select expressions can be created
	 * @return The builder of a conditional expression
	 */
	ISelectExpressionStateObjectBuilder buildStateObjectBuilder(SelectClauseStateObject stateObject);

	/**
	 * Creates a builder that can create a single select expression programmatically. Once the
	 * expression is complete, {@link ISimpleSelectExpressionBuilder#commit()} will push the result
	 * onto the given state object.
	 *
	 * @param stateObject The clause for which a select expression can be created
	 * @return The builder of a conditional expression
	 */
	ISimpleSelectExpressionStateObjectBuilder buildStateObjectBuilder(SimpleSelectClauseStateObject stateObject);

	/**
	 * Creates a builder that can create a new value expression programmatically. Once the
	 * expression is complete, {@link INewValueStateObjectBuilder#commit()} will push the result
	 * onto the given state object.
	 *
	 * @param stateObject The parent for which a new value expression can be created
	 * @return The builder of a new value expression
	 */
	INewValueStateObjectBuilder buildStateObjectBuilder(UpdateItemStateObject stateObject);

	/**
	 * Returns the {@link JPQLGrammar} that is associated with this builder.
	 *
	 * @return The {@link JPQLGrammar} that was used to parse the JPQL query or JPQL fragments
	 */
	JPQLGrammar getGrammar();
}