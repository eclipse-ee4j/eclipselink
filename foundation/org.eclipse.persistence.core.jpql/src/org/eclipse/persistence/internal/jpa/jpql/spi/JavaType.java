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
package org.eclipse.persistence.internal.jpa.jpql.spi;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.jpa.jpql.spi.ITypeRepository;

/**
 * The concrete implementation of {@link IType} that is wrapping a Java type.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
final class JavaType extends org.eclipse.persistence.jpa.jpql.spi.java.JavaType {

	/**
	 * The referenced descriptor that is used rather than the Java type.
	 */
	private ClassDescriptor descriptor;

	/**
	 * Creates a new <code>JavaType</code>.
	 *
	 * @param typeRepository The external form of a type repository
	 * @param type The actual Java type wrapped by this class
	 */
	JavaType(ITypeRepository typeRepository, Class<?> type) {
		super(typeRepository, type);
	}

	/**
	 * Creates a new <code>JavaType</code>.
	 *
	 * @param typeRepository The external form of a type repository
	 * @param descriptor The referenced descriptor that is used rather than the Java type
	 */
	JavaType(ITypeRepository typeRepository, ClassDescriptor descriptor) {
		this(typeRepository, descriptor.getJavaClass());
		this.descriptor = descriptor;
	}

	/**
	 * Creates a new <code>JavaType</code>.
	 *
	 * @param typeRepository The external form of a type repository
	 * @param typeName The fully qualified name of the Java type
	 */
	JavaType(ITypeRepository typeRepository, String typeName) {
		super(typeRepository, typeName);
	}

	/**
	 * Returns the encapsulated {@link ClassDescriptor}.
	 *
	 * @return The referenced descriptor that is used rather than the Java type, the type is retrieved
	 * from this descriptor
	 */
	ClassDescriptor getDescriptor() {
		return descriptor;
	}
}