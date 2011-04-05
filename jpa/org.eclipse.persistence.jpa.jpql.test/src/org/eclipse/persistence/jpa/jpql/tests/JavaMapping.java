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
package org.eclipse.persistence.jpa.jpql.tests;

import org.eclipse.persistence.jpa.jpql.TypeHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IMappingType;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.spi.ITypeRepository;

/**
 * The concrete implementation of {@link IMapping} that is wrapping the runtime representation of a
 * mapping.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class JavaMapping implements IMapping {

	private Field field;
	private IMappingType mappingType;
	private IManagedType parent;
	private IType type;
	private ITypeDeclaration typeDeclaration;

	/**
	 * Creates a new <code>JavaMapping</code>.
	 *
	 * @param parent
	 * @param field
	 */
	JavaMapping(IManagedType parent, Field field) {
		super();
		this.parent = parent;
		this.field  = field;
	}

	private ITypeDeclaration buildTypeDeclaration() {
		return new JavaTypeDeclaration(
			parent.getProvider().getTypeRepository(),
			getType(),
			field.getGenericType(),
			field.getType().isArray()
		);
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(IMapping mapping) {
		return getName().compareTo(mapping.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	public IMappingType getMappingType() {
		if (mappingType == null) {
			mappingType = mappingType();
		}
		return mappingType;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return field.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedType getParent() {
		return parent;
	}

	/**
	 * {@inheritDoc}
	 */
	public IType getType() {
		if (type == null) {
			type = getTypeRepository().getType(field.getType());
		}
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITypeDeclaration getTypeDeclaration() {
		if (typeDeclaration == null) {
			typeDeclaration = buildTypeDeclaration();
		}
		return typeDeclaration;
	}

	private ITypeRepository getTypeRepository() {
		return parent.getProvider().getTypeRepository();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
		return field.isAnnotationPresent(annotationType);
	}

	private IMappingType mappingType() {

//		if (hasAnnotation(BasicCollection.class)) {
//			return IMappingType.BASIC_COLLECTION;
//		}

//		if (hasAnnotation(BasicMap.class)) {
//			return IMappingType.BASIC_MAP;
//		}

		if (hasAnnotation(ElementCollection.class)) {
			return IMappingType.ELEMENT_COLLECTION;
		}

		if (hasAnnotation(Embedded.class)) {
			return IMappingType.EMBEDDED;
		}

		if (hasAnnotation(EmbeddedId.class)) {
			return IMappingType.EMBEDDED_ID;
		}

		if (hasAnnotation(Id.class)) {
			return IMappingType.ID;
		}

		if (hasAnnotation(ManyToMany.class)) {
			return IMappingType.MANY_TO_MANY;
		}

		if (hasAnnotation(ManyToOne.class)) {
			return IMappingType.MANY_TO_ONE;
		}

		if (hasAnnotation(OneToMany.class)) {
			return IMappingType.ONE_TO_MANY;
		}

		if (hasAnnotation(OneToOne.class)) {
			return IMappingType.ONE_TO_ONE;
		}

//		if (hasAnnotation(Transformation.class)) {
//			return IMappingType.TRANSFORMATION;
//		}

		if (hasAnnotation(Transient.class)) {
			return IMappingType.TRANSIENT;
		}

//		if (hasAnnotation(VariableOneToOne.class)) {
//			return IMappingType.VARIABLE_ONE_TO_ONE;
//		}

		if (hasAnnotation(Version.class)) {
			return IMappingType.VERSION;
		}

		// Default
		IType type = getType();
		TypeHelper typeHelper = getTypeRepository().getTypeHelper();

		if (typeHelper.isCollectionType(type) ||
		    typeHelper.isMapType(type)) {

			return IMappingType.ONE_TO_MANY;
		}
		else if (parent.getProvider().getManagedType(type) != null) {
			return IMappingType.ONE_TO_ONE;
		}

		return IMappingType.BASIC;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getName();
	}
}