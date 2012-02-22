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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;
import org.eclipse.persistence.internal.descriptors.InstanceVariableAttributeAccessor;
import org.eclipse.persistence.internal.descriptors.MethodAttributeAccessor;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.jpa.jpql.spi.IEclipseLinkMappingType;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.spi.ITypeRepository;
import org.eclipse.persistence.jpa.jpql.spi.java.JavaType;
import org.eclipse.persistence.jpa.jpql.spi.java.JavaTypeDeclaration;
import org.eclipse.persistence.mappings.AggregateMapping;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;

/**
 * The concrete implementation of {@link IMapping} that is wrapping the runtime representation of a
 * mapping.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class JavaMapping extends AbstractMapping {

	/**
	 * The EclipseLink {@link DatabaseMapping} wrapped by this class.
	 */
	private DatabaseMapping mapping;

	/**
	 * The cached {@link ITypeDeclaration} of this mapping.
	 */
	private ITypeDeclaration typeDeclaration;

	/**
	 * Creates a new <code>JavaMapping</code>.
	 *
	 * @param parent The parent of this mapping
	 * @param mapping The EclipseLink {@link DatabaseMapping} wrapped by this class
	 */
	JavaMapping(IManagedType parent, DatabaseMapping mapping) {
		super(parent);
		this.mapping = mapping;
	}

	@SuppressWarnings("unchecked")
	private ITypeDeclaration buildTypeDeclaration() {

		// For aggregate mappings (@Embedded and @EmbeddedId), we need to use the descriptor
		// because its mappings have to be retrieve from this one and not from the descriptor
		// returned when querying it with a Java type
		if (mapping.isAggregateMapping()) {
			ClassDescriptor descriptor = ((AggregateMapping) mapping).getReferenceDescriptor();
			if (descriptor != null) {
				return buildTypeDeclaration(descriptor);
			}
		}

		if (mapping.isForeignReferenceMapping()) {
			ForeignReferenceMapping referenceMapping = (ForeignReferenceMapping) mapping;

			// Map<K, V>
			if (mapping.isDirectMapMapping()) {
				DirectMapMapping directMapMapping = (DirectMapMapping) mapping;

				Class<?> key = directMapMapping.getKeyClass();
				Class<?> value = directMapMapping.getValueClass();

				if (key == null) {
					MapContainerPolicy mapPolicy = (MapContainerPolicy) mapping.getContainerPolicy();
					Object keyValue = mapPolicy.getKeyType();
					if (keyValue instanceof Class<?>) {
						key = (Class<?>) keyValue;
					}
					else if (keyValue instanceof ClassDescriptor) {
						ClassDescriptor descriptor = (ClassDescriptor) keyValue;
						key = descriptor.getJavaClass();
					}
				}

				if (value == null) {
					value = mapping.getAttributeClassification();
				}

				IType type = getTypeRepository().getType(mapping.getContainerPolicy().getContainerClass());
				IType keyType = getTypeRepository().getType(key);
				IType valueType = getTypeRepository().getType(value);
				return buildTypeDeclaration(type, new IType[] { keyType, valueType });
			}

			if (mapping.isCollectionMapping()) {
				ContainerPolicy containerPolicy = mapping.getContainerPolicy();

				// Collection<T>
				if (containerPolicy.isCollectionPolicy()) {
					Class<?> containerClass = containerPolicy.getContainerClass();
					Class<?> propertyType = referenceMapping.getReferenceClass();
					if (propertyType == null) {
						propertyType = mapping.getAttributeClassification();
					}
					IType type = getTypeRepository().getType(containerClass);
					return buildTypeDeclaration(type, propertyType);
				}

				if (containerPolicy.isMapPolicy()) {
					MapContainerPolicy mapPolicy = (MapContainerPolicy) containerPolicy;
					IType type = getTypeRepository().getType(mapPolicy.getContainerClass());
					IType valueType = getTypeRepository().getType(mapPolicy.getElementClass());
					Object key = mapPolicy.getKeyType();
					IType keyType;

					if (key instanceof Class<?>) {
						keyType = getTypeRepository().getType((Class<?>) key);
					}
					else if (key instanceof ClassDescriptor) {
						ClassDescriptor descriptor = (ClassDescriptor) key;
						keyType = getTypeRepository().getType(descriptor.getJavaClass());
					}
					else {
						keyType = null;
					}

					return buildTypeDeclaration(type, new IType[] { keyType, valueType });
				}
			}

			// T
			return buildTypeDeclaration(referenceMapping.getReferenceDescriptor());
		}

		AttributeAccessor accessor = mapping.getAttributeAccessor();

		// Attribute
		if (accessor.isInstanceVariableAttributeAccessor()) {
			InstanceVariableAttributeAccessor attributeAccessor = (InstanceVariableAttributeAccessor) accessor;
			Field field = attributeAccessor.getAttributeField();

			if (field == null) {
				try {
					field = mapping.getDescriptor().getJavaClass().getDeclaredField(attributeAccessor.getAttributeName());
				}
				catch (Exception e) {}
			}

			return buildTypeDeclaration(field);
		}

		// Property
		if (accessor.isMethodAttributeAccessor()) {
			MethodAttributeAccessor methodAccessor = (MethodAttributeAccessor) accessor;
			Method method = methodAccessor.getGetMethod();

			if (method == null) {
				try {
					method = mapping.getDescriptor().getJavaClass().getDeclaredMethod(methodAccessor.getGetMethodName());
				}
				catch (Exception e) {}
			}

			return buildTypeDeclaration(method);
		}

		// Anything else
		Class<?> attributeType = accessor.getAttributeClass();
		IType type = getTypeRepository().getType(attributeType);
		return new JavaTypeDeclaration(getTypeRepository(), type, null, attributeType.isArray());
	}

	private ITypeDeclaration buildTypeDeclaration(ClassDescriptor descriptor) {
		JavaType type = new JavaType(getTypeRepository(), descriptor.getJavaClass());
		return new JavaTypeDeclaration(getTypeRepository(), type, null, false);
	}

	private ITypeDeclaration buildTypeDeclaration(Field field) {

		ITypeRepository typeRepository = getTypeRepository();
		Class<?> fieldType = field.getType();
		Type genericType = field.getGenericType();

		if (fieldType == genericType) {
			genericType = null;
		}

		IType type = typeRepository.getType(fieldType);
		return new JavaTypeDeclaration(typeRepository, type, genericType, fieldType.isArray());
	}

	private ITypeDeclaration buildTypeDeclaration(IType type, Object genericType) {
		return new JavaTypeDeclaration(getTypeRepository(), type, genericType, false);
	}

	private ITypeDeclaration buildTypeDeclaration(Method method) {

		ITypeRepository typeRepository = getTypeRepository();
		Class<?> returnType = method.getReturnType();
		Type genericType = method.getGenericReturnType();

		if (returnType == genericType) {
			genericType = null;
		}

		IType type = typeRepository.getType(returnType);
		return new JavaTypeDeclaration(typeRepository, type, genericType, returnType.isArray());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	int calculateMappingType() {

		// Basic
		// Id
		// Version
		if (mapping.isDirectToFieldMapping()) {

			if (mapping.isJPAId()) {
				return IEclipseLinkMappingType.ID;
			}

			AbstractDirectMapping directToFieldMapping = (AbstractDirectMapping) mapping;

			if (directToFieldMapping.getDescriptor().getOptimisticLockingPolicy() instanceof VersionLockingPolicy) {
				VersionLockingPolicy policy = (VersionLockingPolicy) directToFieldMapping.getDescriptor().getOptimisticLockingPolicy();
				if (policy.getVersionMapping() == mapping) {
					return IEclipseLinkMappingType.VERSION;
				}
			}

			return IEclipseLinkMappingType.BASIC;
		}

		// Element Collection (deprecated Basic Map, Basic Collection)
		if (mapping.isDirectCollectionMapping() ||
		    mapping.isAggregateCollectionMapping()) {

			return IEclipseLinkMappingType.ELEMENT_COLLECTION;
		}

		// Embedded
		// Embedded Id
		if (mapping.isAggregateObjectMapping()) {
			if (((AggregateObjectMapping) mapping).isPrimaryKeyMapping()) {
				return IEclipseLinkMappingType.EMBEDDED_ID;
			}
			return IEclipseLinkMappingType.EMBEDDED;
		}

		// 1:M
		if (mapping.isOneToManyMapping()) {
			return IEclipseLinkMappingType.ONE_TO_MANY;
		}

		// M:M
		// 1:M
		if (mapping.isManyToManyMapping()) {
			if (((ManyToManyMapping) mapping).isDefinedAsOneToManyMapping()) {
				return IEclipseLinkMappingType.ONE_TO_MANY;
			}
			return IEclipseLinkMappingType.MANY_TO_MANY;
		}

		// M:1
		if (mapping.isManyToOneMapping()) {
			return IEclipseLinkMappingType.MANY_TO_ONE;
		}

		// 1:1
		if (mapping.isOneToOneMapping()) {
			return IEclipseLinkMappingType.ONE_TO_ONE;
		}

		// Transformation
		if (mapping.isTransformationMapping()) {
			return IEclipseLinkMappingType.TRANSFORMATION;
		}

		// Variable 1:1
		if (mapping.isVariableOneToOneMapping()) {
			return IEclipseLinkMappingType.VARIABLE_ONE_TO_ONE;
		}

		return IEclipseLinkMappingType.TRANSIENT;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(IMapping mapping) {
		return getName().compareTo(mapping.getName());
	}

	/**
	 * Returns the encapsulated {@link DatabaseMapping}, which is the actual mapping.
	 *
	 * @return The actual mapping wrapped by this external form
	 */
	DatabaseMapping getMapping() {
		return mapping;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return mapping.getAttributeName();
	}

	/**
	 * {@inheritDoc}
	 */
	public IType getType() {
		return getTypeDeclaration().getType();
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

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public boolean hasAnnotation(Class<? extends Annotation> annotationType) {

		AttributeAccessor accessor = mapping.getAttributeAccessor();

		// Attribute
		if (accessor.isInstanceVariableAttributeAccessor()) {
			InstanceVariableAttributeAccessor attributeAccessor = (InstanceVariableAttributeAccessor) accessor;
			Field field = attributeAccessor.getAttributeField();

			if (field == null) {
				try {
					field = mapping.getDescriptor().getJavaClass().getDeclaredField(attributeAccessor.getAttributeName());
				}
				catch (Exception e) {}
			}

			return (field == null) ? false : field.isAnnotationPresent(annotationType);
		}

		// Property
		if (accessor.isMethodAttributeAccessor()) {
			MethodAttributeAccessor methodAccessor = (MethodAttributeAccessor) accessor;
			Method method = methodAccessor.getGetMethod();

			if (method == null) {
				try {
					method = mapping.getDescriptor().getJavaClass().getDeclaredMethod(methodAccessor.getGetMethodName());
				}
				catch (Exception e) {}
			}

			return (method == null) ? false : method.isAnnotationPresent(annotationType);
		}

		// Anything else
		return accessor.getAttributeClass().isAnnotationPresent(annotationType);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isCollection() {
		return mapping.isCollectionMapping();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isProperty() {
		return mapping.isDirectToFieldMapping();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRelationship() {
		return mapping.isForeignReferenceMapping()
		        || mapping.isAbstractCompositeCollectionMapping()
                        || mapping.isAbstractCompositeDirectCollectionMapping();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("name=");
		sb.append(getName());
		sb.append(", type=");
		sb.append(getMappingType());
		sb.append(", mapping=");
		sb.append(mapping);
		return sb.toString();
	}
}