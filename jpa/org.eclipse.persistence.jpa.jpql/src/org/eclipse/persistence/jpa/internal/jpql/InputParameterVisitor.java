/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.internal.jpql;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractTraverseChildrenVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.InputParameter;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;

/**
 * This {@link org.eclipse.persistence.jpa.jpql.parser.ExpressionVisitor
 * ExpressionVisitor} is looking to find the {@link InputParameter} with a certain parameter name.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class InputParameterVisitor extends AbstractTraverseChildrenVisitor {

	/**
	 * The set of {@link InputParameter InputParameters} that was retrieved by traversing the parsed
	 * tree.
	 */
	public final Set<InputParameter> inputParameters;

	/**
	 * The name of the input parameter to search.
	 */
	private String parameterName;

	/**
	 * Creates a new <code>InputParameterVisitor</code>.
	 *
	 * @param parameterName The name of the input parameter to search
	 */
	public InputParameterVisitor(String parameterName) {
		super();

		checkParameterName(parameterName);

		this.inputParameters = new HashSet<InputParameter>();
		this.parameterName   = parameterName;
	}

	private void checkParameterName(String parameterName) {
		if (parameterName == null) {
			throw new IllegalArgumentException("The parameter name cannot be null");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(InputParameter expression) {

		String name = expression.getParameter();

		if (ExpressionTools.valuesAreEqual(parameterName, name)) {
			inputParameters.add(expression);
		}
	}
}