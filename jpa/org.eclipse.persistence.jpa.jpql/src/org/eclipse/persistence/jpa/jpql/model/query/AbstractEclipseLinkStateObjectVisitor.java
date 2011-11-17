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
 * The abstract definition of {@link IEclipseLinkStateObjectVisitor}, which implements all the
 * methods but does nothing. It can be subclassed so that only the required methods are overridden.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractEclipseLinkStateObjectVisitor extends AbstractStateObjectVisitor
                                                            implements EclipseLinkStateObjectVisitor {

	/**
	 * {@inheritDoc}
	 */
	public void visit(FuncExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TreatExpressionStateObject stateObject) {
	}
}