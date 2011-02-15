/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query;

import org.eclipse.persistence.utils.jpa.query.parser.AbstractTraverseChildrenVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.ExpressionTools;
import org.eclipse.persistence.utils.jpa.query.parser.InputParameter;

/**
 * This {@link org.eclipse.persistence.utils.jpa.query.parser.ExpressionVisitor
 * ExpressionVisitor} is looking to find the {@link InputParameter} with a
 * certain parameter name.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class InputParameterVisitor extends AbstractTraverseChildrenVisitor
{
	/**
	 * The {@link InputParameter} object that this visitor was trying to find or
	 * <code>null</code> none has the same parameter name.
	 */
	private InputParameter inputParameter;

	/**
	 * The name of the input parameter to search.
	 */
	private String parameterName;

	/**
	 * Creates a new <code>InputParameterVisitor</code>.
	 *
	 * @param parameterName The name of the input parameter to search
	 */
	InputParameterVisitor(String parameterName)
	{
		super();

		checkParameterName(parameterName);
		this.parameterName = parameterName;
	}

	private void checkParameterName(String parameterName)
	{
		if (parameterName == null)
		{
			throw new IllegalArgumentException("The parameter name cannot be null");
		}
	}

	/**
	 * Returns the {@link InputParameter} that has the parameter name used for
	 * the search.
	 *
	 * @return The {@link InputParameter} object that this visitor was trying to
	 * find or <code>null</code> none has the same parameter name
	 */
	InputParameter inputParameter()
	{
		return inputParameter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(InputParameter expression)
	{
		String name = expression.getParameter();

		if (ExpressionTools.valuesAreEqual(parameterName, name))
		{
			this.inputParameter = expression;
		}
	}
}