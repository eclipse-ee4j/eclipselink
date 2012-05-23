/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IMappingBuilder;

/**
 * A {@link IMappingBuilder} that creates the right instance of {@link IMappingBuilder} for a class'
 * {@link Member members}, which are either a persistent attribute or a property.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class JavaMappingBuilder implements IMappingBuilder<Member> {

	/**
	 * Creates a new <code>JavaMappingBuilder</code>.
	 */
	public JavaMappingBuilder() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public IMapping buildMapping(IManagedType parent, Member value) {

		if (value instanceof Field) {
			return new JavaFieldMapping(parent, (Field) value);
		}

		return new JavaPropertyMapping(parent, (Method) value);
	}
}