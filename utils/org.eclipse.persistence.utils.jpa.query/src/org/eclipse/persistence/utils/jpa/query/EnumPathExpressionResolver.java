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

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.expressions.ConstantExpression;

/**
 * The {@link PathExpressionResolver} that wraps another one and checks if it's a enum constant.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class EnumPathExpressionResolver extends AbstractPathExpressionResolver {

	/**
	 * The fully qualified enum type, including the constant name.
	 */
	private final String path;

	/**
	 * The context used to query information about the application metadata.
	 */
	private QueryBuilderContext queryContext;

	/**
	 * Creates a new <code>EnumPathExpressionResolver</code>.
	 *
	 * @param parent The parent resolver of this one
	 * @param queryContext The context used to query information about the application metadata
	 * @param path The fully qualified enum type, including the constant name
	 */
	EnumPathExpressionResolver(PathExpressionResolver parent,
	                           QueryBuilderContext queryContext,
	                           String path) {
		super(parent);

		this.path         = path;
		this.queryContext = queryContext;
	}

	private Enum<?> buildEnumConstant(Class<?> type, String constant) {

		Object[] constants = type.getEnumConstants();

   	for (int index = constants.length; --index >= 0; ) {
   		Enum<?> enumConstant = (Enum<?>) constants[index];
   		if (constant.equals(enumConstant.name())) {
            return enumConstant;
         }
      }

      return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EnumPathExpressionResolver clone() {
		return new EnumPathExpressionResolver(getParent().clone(), queryContext, path);
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression getExpression() {

		// Retrieve the fully qualified enum type name
		Class<?> type = queryContext.getEnumJavaType(path);

		// If the type can be resolve then it's assume it's an enum type
		if ((type != null) && type.isEnum()) {
			int lastDotIndex = path.lastIndexOf(".");
			String constant = path.substring(lastDotIndex + 1);
			Object enumConstant = buildEnumConstant(type, constant);
			return new ConstantExpression(enumConstant, new ExpressionBuilder());
		}

		// The path is not an enum constant, then use the parent,
		// which will resolve the state field path expression
		return getParentExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression getExpression(String variableName) {
		return getParent().getExpression(variableName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setNullAllowed(boolean nullAllowed) {

		super.setNullAllowed(nullAllowed);

		// If the flag is changed, make sure the parent PathExpressionResolver is updated since
		// it's where it matters
		getParent().setNullAllowed(nullAllowed);
	}
}