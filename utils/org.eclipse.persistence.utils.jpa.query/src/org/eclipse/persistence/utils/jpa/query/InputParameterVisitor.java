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
package org.eclipse.persistence.utils.jpa.query;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractTraverseChildrenVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.ExpressionTools;
import org.eclipse.persistence.utils.jpa.query.parser.InputParameter;

/**
 * This {@link org.eclipse.persistence.utils.jpa.query.parser.ExpressionVisitor
 * ExpressionVisitor} is looking to find the {@link InputParameter} with a certain parameter name.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class InputParameterVisitor extends AbstractTraverseChildrenVisitor {

	/**
	 * The set of {@link InputParameter InputParameters} that was retrieved by traversing the parsed
	 * tree.
	 */
	final Set<InputParameter> inputParameters;

	/**
	 * The name of the input parameter to search.
	 */
	private String parameterName;

	/**
	 * Creates a new <code>InputParameterVisitor</code>.
	 *
	 * @param parameterName The name of the input parameter to search
	 */
	InputParameterVisitor(String parameterName) {
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