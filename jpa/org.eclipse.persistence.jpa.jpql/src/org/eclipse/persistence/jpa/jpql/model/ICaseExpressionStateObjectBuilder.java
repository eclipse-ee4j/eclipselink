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

import org.eclipse.persistence.jpa.jpql.model.query.CaseExpressionStateObject;

/**
 * This builder is responsible to create a <code><b>CASE</b></code> expression.
 *
 * @see ISimpleSelectExpressionStateObjectBuilder#getCaseBuilder()
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface ICaseExpressionStateObjectBuilder extends IAbstractConditionalExpressionStateObjectBuilder<ICaseExpressionStateObjectBuilder> {

	/**
	 * Creates the actual state object based on the information this builder gathered.
	 *
	 * @return The newly created {@link CaseExpressionStateObject}
	 */
	CaseExpressionStateObject buildStateObject();

	/**
	 * Creates a single <code><b>WHEN</b></code> expression.
	 *
	 * @param when The <code><b>WHEN</b></code> expression
	 * @param then The <code><b>THEN</b></code> expression
	 * @return This {@link ICaseExpressionStateObjectBuilder builder}
	 */
	ICaseExpressionStateObjectBuilder when(ICaseExpressionStateObjectBuilder when,
	                                       ICaseExpressionStateObjectBuilder then);
}