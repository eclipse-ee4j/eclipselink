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

/**
 * This builder can be used to easily create a select expression defined for a subquery without
 * having to create each object manually. The builder is associated with {@link org.eclipse.
 * persistence.jpa.jpql.model.query.SimpleClauseStateObject SimpleClauseStateObject}.
 *
 * @see ISelectExpressionBuilder
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface ISimpleSelectExpressionStateObjectBuilder extends IScalarExpressionStateObjectBuilder<ISimpleSelectExpressionStateObjectBuilder> {

	/**
	 * Pushes the changes created by this builder to the state object.
	 */
	void commit();

	/**
	 * Creates the expression representing an identification variable.
	 *
	 * @param variable The identification variable
	 * @return This {@link ISimpleSelectExpressionStateObjectBuilder builder}
	 */
	ISimpleSelectExpressionStateObjectBuilder variable(String variable);
}