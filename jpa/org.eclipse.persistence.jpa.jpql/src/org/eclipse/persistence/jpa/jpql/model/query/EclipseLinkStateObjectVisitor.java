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
package org.eclipse.persistence.jpa.jpql.model.query;

/**
 * The interface is used to traverse the {@link StateObject} hierarchy that represents a JPQL
 * query as well as what EclipseLink adds on top of the basic grammar.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface EclipseLinkStateObjectVisitor extends StateObjectVisitor {

	/**
	 * Visits the given {@link FuncExpressionStateObject}.
	 *
	 * @param stateObject The {@link FuncExpressionStateObject} to visit
	 */
	void visit(FuncExpressionStateObject stateObject);

	/**
	 * Visits the given {@link TreatExpressionStateObject}.
	 *
	 * @param stateObject The {@link TreatExpressionStateObject} to visit
	 */
	void visit(TreatExpressionStateObject stateObject);
}