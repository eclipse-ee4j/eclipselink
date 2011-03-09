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
package org.eclipse.persistence.jpa.jpql;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import org.eclipse.persistence.jpa.jpql.spi.IConstructor;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.spi.ITypeRepository;

/**
 * The concrete implementation of {@link IConstructor} that is wrapping a Java constructor.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class JavaConstructor implements IConstructor {

	/**
	 * The actual Java constructor.
	 */
	private Constructor<?> constructor;

	/**
	 * The cached {@link ITypeDeclaration parameter types}.
	 */
	private ITypeDeclaration[] parameterTypes;

	/**
	 * The declaring type of this constructor.
	 */
	private JavaType type;

	/**
	 * Creates a new <code>JavaConstructor</code>.
	 *
	 * @param type The declaring type of this constructor
	 * @param constructor The actual Java constructor
	 */
	JavaConstructor(JavaType type, Constructor<?> constructor) {
		super();
		this.type = type;
		this.constructor = constructor;
	}

	private ITypeDeclaration[] buildParameterTypes() {

		Class<?>[] types = constructor.getParameterTypes();
		Type[] genericTypes = constructor.getGenericParameterTypes();
		ITypeDeclaration[] typeDeclarations = new ITypeDeclaration[types.length];

		for (int index = 0, count = types.length; index < count; index++) {
			typeDeclarations[index] = buildTypeDeclaration(types[index], genericTypes[index]);
		}

		return typeDeclarations;
	}

	private ITypeDeclaration buildTypeDeclaration(Class<?> javaType, Type genericType) {
		ITypeRepository typeRepository = getTypeRepository();
		IType type = typeRepository.getType(javaType);
		return new JavaTypeDeclaration(typeRepository, type, genericType, javaType.isArray());
	}

	/**
	 * {@inheritDoc}
	 */
	public ITypeDeclaration[] getParameterTypes() {
		if (parameterTypes == null) {
			parameterTypes = buildParameterTypes();
		}
		return parameterTypes;
	}

	private ITypeRepository getTypeRepository() {
		return type.getTypeRepository();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return constructor.toGenericString();
	}
}