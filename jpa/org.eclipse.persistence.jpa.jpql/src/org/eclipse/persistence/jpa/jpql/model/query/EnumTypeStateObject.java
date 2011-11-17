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

import org.eclipse.persistence.jpa.jpql.parser.AbstractPathExpression;

/**
 * This {@link StateObject} wraps the name of an {@link Enum} constant.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class EnumTypeStateObject extends SimpleStateObject {

	/**
	 * Creates a new <code>EnumTypeStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public EnumTypeStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>EnumTypeStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param enumConstant The fully qualified name of the {@link Enum} type following by the constant
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public EnumTypeStateObject(StateObject parent, Enum<? extends Enum<?>> enumConstant) {
		super(parent, toString(enumConstant));
	}

	/**
	 * Creates a new <code>EnumTypeStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param enumConstant The fully qualified name of the {@link Enum} constant
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public EnumTypeStateObject(StateObject parent, String enumConstant) {
		super(parent, enumConstant);
	}

	private static String toString(Enum<? extends Enum<?>> enumConstant) {
		Class<?> enumType = enumConstant.getDeclaringClass();
		return enumType.getName() + "." + enumConstant.name();
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(StateObjectVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractPathExpression getExpression() {
		return (AbstractPathExpression) super.getExpression();
	}

	/**
	 * Keeps a reference of the {@link AbstractPathExpression parsed object} object, which should
	 * only be done when this object is instantiated during the conversion of a parsed JPQL query
	 * into {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link AbstractPathExpression parsed object} representing the enum type
	 */
	public void setExpression(AbstractPathExpression expression) {
		super.setExpression(expression);
	}
}