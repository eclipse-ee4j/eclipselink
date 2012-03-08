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
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface IEclipseLinkSelectExpressionStateObjectBuilder extends ISelectExpressionStateObjectBuilder {

	/**
	 * {@inheritDoc}
	 */
	IEclipseLinkSelectExpressionStateObjectBuilder append();

	/**
	 * {@inheritDoc}
	 */
	IEclipseLinkSelectExpressionStateObjectBuilder new_(String className,
	                                                    ISelectExpressionStateObjectBuilder... parameters);

	/**
	 * {@inheritDoc}
	 */
	IEclipseLinkSelectExpressionStateObjectBuilder object(String identificationVariable);

	/**
	 * {@inheritDoc}
	 */
	IEclipseLinkSelectExpressionStateObjectBuilder resultVariable(String resultVariable);

	/**
	 * {@inheritDoc}
	 */
	IEclipseLinkSelectExpressionStateObjectBuilder resultVariableAs(String resultVariable);

	/**
	 * {@inheritDoc}
	 */
	IEclipseLinkSelectExpressionStateObjectBuilder variable(String variable);
}