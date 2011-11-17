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
package org.eclipse.persistence.jpa.jpql;

import org.eclipse.persistence.jpa.jpql.parser.AbstractExpression;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;

/**
 * This {@link Resolver} retrieves the type for an enum constant.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class EnumLiteralResolver extends Resolver {

	/**
	 * The fully qualified name of the enum constant.
	 */
	private String enumLiteral;

	/**
	 * The cached {@link IType} that was already created elsewhere.
	 */
	private IType type;

	/**
	 * Creates a new <code>EnumLiteralResolver</code>.
	 *
	 * @param parent The parent of this resolver, which is never <code>null</code>
	 * @param type The {@link IType} of the enum type
	 * @param enumLiteral The fully qualified name of the enum constant
	 * @exception NullPointerException If the parent is <code>null</code>
	 */
	public EnumLiteralResolver(Resolver parent, IType type, String enumLiteral) {
		super(parent);
		this.type        = type;
		this.enumLiteral = enumLiteral;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(ResolverVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IType buildType() {
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ITypeDeclaration buildTypeDeclaration() {
		return type.getTypeDeclaration();
	}

	/**
	 * Returns the name of the constant constant.
	 *
	 * @return The name of the constant without the fully qualified enum type
	 */
	public String getConstantName() {
		int index = enumLiteral.lastIndexOf(AbstractExpression.DOT);
		return enumLiteral.substring(index + 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return enumLiteral;
	}
}