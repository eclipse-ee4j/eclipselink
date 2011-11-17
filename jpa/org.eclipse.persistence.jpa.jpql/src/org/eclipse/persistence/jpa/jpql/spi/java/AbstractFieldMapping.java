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
package org.eclipse.persistence.jpa.jpql.spi.java;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;

/**
 * The abstract implementation of {@link IMapping} that is wrapping the runtime representation
 * of a persistent field.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractFieldMapping extends AbstractMapping {

	/**
	 * Creates a new <code>AbstractFieldMapping</code>.
	 *
	 * @param parent The parent of this mapping
	 * @param field The Java {@link Field} wrapped by this mapping
	 */
	protected AbstractFieldMapping(IManagedType parent, Field field) {
		super(parent, field);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field getMember() {
		return (Field) super.getMember();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Annotation[] getMemberAnnotations() {
		return getMember().getAnnotations();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Type getMemberGenericType() {
		return getMember().getGenericType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getMemberType() {
		return getMember().getType();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
		return getMember().isAnnotationPresent(annotationType);
	}
}