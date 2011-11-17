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
package org.eclipse.persistence.jpa.jpql.spi.java;

import java.lang.reflect.Member;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeVisitor;
import org.eclipse.persistence.jpa.jpql.spi.IMappedSuperclass;
import org.eclipse.persistence.jpa.jpql.spi.IMappingBuilder;

/**
 * The concrete implementation of {@link IMappedSuperclass} that is wrapping the runtime
 * representation of a JPA mapped superclass.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public class JavaMappedSuperclass extends JavaManagedType
                                  implements IMappedSuperclass {

	/**
	 * Creates a new <code>JavaMappedSuperclass</code>.
	 *
	 * @param provider The provider of JPA managed types
	 * @param type The {@link IType} wrapping the Java type
	 * @param mappingBuilder The builder that is responsible to create the {@link IMapping} wrapping
	 * a persistent attribute or property
	 */
	public JavaMappedSuperclass(IManagedTypeProvider provider,
	                            JavaType type,
	                            IMappingBuilder<Member> mappingBuilder) {

		super(provider, type, mappingBuilder);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(IManagedTypeVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getType().getName();
	}
}