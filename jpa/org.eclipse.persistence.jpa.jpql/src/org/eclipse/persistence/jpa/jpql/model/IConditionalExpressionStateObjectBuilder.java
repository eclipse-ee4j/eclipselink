/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
 * This builder can be used to easily create a conditional expression without having to create each
 * object manually. The builder is associated with {@link org.eclipse.persistence.jpa.jpql.model.
 * query.AbstractConditionalClauseStateObject AbstractConditionalClauseStateObject}.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface IConditionalExpressionStateObjectBuilder extends IAbstractConditionalExpressionStateObjectBuilder<IConditionalExpressionStateObjectBuilder> {

	/**
	 * Pushes the changes created by this builder to the state object.
	 */
	void commit();
}