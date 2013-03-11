/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tools.spi.java;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedType;
import static org.eclipse.persistence.jpa.jpql.tools.spi.IEclipseLinkMappingType.*;

/**
 * The concrete implementation of {@link org.eclipse.persistence.jpa.jpql.tools.spi.IMapping IMapping}
 * that is wrapping the runtime representation of an EclipseLink mapping that is represented by a
 * property.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class EclipseLinkPropertyMapping extends AbstractMethodMapping {

	/**
	 * Creates a new <code>EclipseLinkPropertyMapping</code>.
	 *
	 * @param parent The parent of this mapping
	 * @param method The Java {@link Method} wrapped by this mapping
	 */
	public EclipseLinkPropertyMapping(IManagedType parent, Method method) {
		super(parent, method);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int calculateMappingType(Annotation[] annotations) {

		if (hasAnnotation(annotations, "org.eclipse.persistence.annotations.BasicCollection")) {
			return BASIC_COLLECTION;
		}

		if (hasAnnotation(annotations, "org.eclipse.persistence.annotations.BasicMap")) {
			return BASIC_MAP;
		}

		if (hasAnnotation(annotations, "org.eclipse.persistence.annotations.Transformation")) {
			return TRANSFORMATION;
		}

		if (hasAnnotation(annotations, "org.eclipse.persistence.annotations.VariableOneToOne")) {
			return VARIABLE_ONE_TO_ONE;
		}

		return super.calculateMappingType(annotations);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCollection() {
		switch (getMappingType()) {
			case BASIC_COLLECTION:
			case BASIC_MAP: return true;
			default: return super.isCollection();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRelationship() {
		switch (getMappingType()) {
			case VARIABLE_ONE_TO_ONE: return true;
			default: return super.isRelationship();
		}
	}
}