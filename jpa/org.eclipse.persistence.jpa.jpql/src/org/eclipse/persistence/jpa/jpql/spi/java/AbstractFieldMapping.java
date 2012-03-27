/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle. All rights reserved.
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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;

/**
 * The abstract implementation of {@link org.eclipse.persistence.jpa.jpql.spi.IMapping IMapping}
 * that is wrapping the runtime representation of a persistent field.
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

		Field field = getMember();

		// One to One
		OneToOne oneToOne = field.getAnnotation(OneToOne.class);
		if (oneToOne != null) {
			Class<?> targetEntity = oneToOne.targetEntity();
			if (targetEntity != void.class) {
				return targetEntity;
			}
		}

		// Many to One
		ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
		if (manyToOne != null) {
			Class<?> targetEntity = manyToOne.targetEntity();
			if (targetEntity != void.class) {
				return targetEntity;
			}
		}

		// Many to Many
		ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
		if (manyToMany != null) {
			Class<?> targetEntity = manyToMany.targetEntity();
			if (targetEntity != void.class) {
				return targetEntity;
			}
		}

		// One to Many
		OneToMany oneToMany = field.getAnnotation(OneToMany.class);
		if (oneToMany != null) {
			Class<?> targetEntity = oneToMany.targetEntity();
			if (targetEntity != void.class) {
				return targetEntity;
			}
		}

		return field.getType();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
		return getMember().isAnnotationPresent(annotationType);
	}
}