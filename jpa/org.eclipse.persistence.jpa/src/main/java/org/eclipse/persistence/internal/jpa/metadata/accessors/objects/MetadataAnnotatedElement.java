/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     05/16/2008-1.0M8 Guy Pelletier
//       - 218084: Implement metadata merging functionality between mapping files
//     05/23/2008-1.0M8 Guy Pelletier
//       - 211330: Add attributes-complete support to the EclipseLink-ORM.XML Schema
//     06/20/2008-1.0 Guy Pelletier
//       - 232975: Failure when attribute type is generic
//     09/23/2008-1.1 Guy Pelletier
//       - 241651: JPA 2.0 Access Type support
//     10/01/2008-1.1 Guy Pelletier
//       - 249329: To remain JPA 1.0 compliant, any new JPA 2.0 annotations should be referenced by name
//     02/06/2009-2.0 Guy Pelletier
//       - 248293: JPA 2.0 Element Collections (part 2)
//     03/27/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
//     04/24/2009-2.0 Guy Pelletier
//       - 270011: JPA 2.0 MappedById support
//     06/25/2009-2.0 Michael O'Brien
//       - 266912: change MappedSuperclass handling in stage2 to pre process accessors
//          in support of the custom descriptors holding mappings required by the Metamodel
//     03/08/2010-2.1 Guy Pelletier
//       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM
//     08/11/2010-2.2 Guy Pelletier
//       - 312123: JPA: Validation error during Id processing on parameterized generic OneToOne Entity relationship from MappedSuperclass
//     12/01/2010-2.2 Guy Pelletier
//       - 331234: xml-mapping-metadata-complete overriden by metadata-complete specification
//     12/02/2010-2.2 Guy Pelletier
//       - 251554: ExcludeDefaultMapping annotation needed
//     12/02/2010-2.2 Guy Pelletier
//       - 324471: Do not default to VariableOneToOneMapping for interfaces unless a managed class implementing it is found
//     01/25/2011-2.3 Guy Pelletier
//       - 333488: Serializable attribute being defaulted to a variable one to one mapping and causing exception
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     07/16/2013-2.5.1 Guy Pelletier
//       - 412384: Applying Converter for parameterized basic-type for joda-time's DateTime does not work
package org.eclipse.persistence.internal.jpa.metadata.accessors.objects;

import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.ECLIPSELINK_OXM_PACKAGE_PREFIX;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ACCESS;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_BASIC;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ELEMENT_COLLECTION;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_EMBEDDABLE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_EMBEDDED;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_EMBEDDED_ID;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ENUMERATED;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ID;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_LOB;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_MANY_TO_MANY;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_MANY_TO_ONE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ONE_TO_MANY;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ONE_TO_ONE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_TEMPORAL;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_VERSION;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.annotations.Array;
import org.eclipse.persistence.annotations.BasicCollection;
import org.eclipse.persistence.annotations.BasicMap;
import org.eclipse.persistence.annotations.ReadTransformer;
import org.eclipse.persistence.annotations.Structure;
import org.eclipse.persistence.annotations.TransientCompatibleAnnotations;
import org.eclipse.persistence.annotations.VariableOneToOne;
import org.eclipse.persistence.annotations.WriteTransformer;
import org.eclipse.persistence.annotations.WriteTransformers;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;

/**
 * INTERNAL:
 * Parent object that is used to hold onto a valid JPA decorated method, field
 * or class.
 *
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class MetadataAnnotatedElement extends MetadataAccessibleObject {
    public static final String DEFAULT_RAW_CLASS = "java.lang.String";

    /** The name of the element, i.e. class name, field name, method name. */
    private String m_name;

    /** Defines elements modifiers, i.e. private/static/transient. */
    private int m_modifiers;

    /** Used to cache the type metadata class, but cannot be cached in the case of generics. */
    private MetadataClass m_rawClass;

    /** Used to cache the full type metadata class including some generic specifications. */
    private MetadataClass m_rawClassWithGenerics;

    /**
     * Defines the generic types of the elements type.
     * This is null if no generics are used.
     * This is a list of class/type names from the class/field/method signature.
     * The size of the list varies depending on how many generics are present,
     * i.e.
     * - {@code Map<String, Long> -> ["java.util.Map", "java.lang.String", "java.lang.Long"]}
     * - {@code Beverage<T> extends Object -> [T, :, java.lang.Object, java.lang.Object]}
     */
    private List<String> m_genericType;

    /** Defines the field type, or method return type class name. */
    private String m_type;

    /**
     * Used with the APT processor. Stores the PrimitiveType if this annotated
     * element is a primitive. We can't make it a primitive type here because
     * it introduces a JDK 1.6 dependency. The APT processor will need to cast
     * it back.
     */
    Object m_primitiveType;

    /** Defines the attribute name of a field, or property name of a method. */
    private String m_attributeName;

    /** Stores any annotations defined for the element, keyed by annotation name. */
    private Map<String, MetadataAnnotation> m_annotations;

    /** Stores any meta-annotations defined for the element, keyed by meta-annotation name. */
    private final Map<String, MetadataAnnotation> m_metaAnnotations;

    /**
     * INTERNAL:
     */
    public MetadataAnnotatedElement(MetadataFactory factory) {
        super(factory);

        m_annotations = new HashMap<>();
        m_metaAnnotations = new HashMap<>();
    }

    /**
     * INTERNAL:
     */
    public void addAnnotation(MetadataAnnotation annotation) {
        m_annotations.put(annotation.getName(), annotation);
    }

    /**
     * INTERNAL:
     */
    public void addMetaAnnotation(MetadataAnnotation annotation) {
        m_metaAnnotations.put(annotation.getName(), annotation);
    }

    /**
     * INTERNAL:
     */
    public void addGenericType(String genericType) {
        if (m_genericType == null) {
            m_genericType = new ArrayList<>();
        }

        m_genericType.add(genericType);
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        if (getName() == null) {
            return ((MetadataAnnotatedElement)object).getName() == null;
        }
        return getName().equals(((MetadataAnnotatedElement)object).getName());
    }

    /**
     * INTERNAL:
     * Return the annotated element for this accessor. Note: This method does
     * not check against a metadata complete.
     */
    public MetadataAnnotation getAnnotation(Class<?> annotation) {
        return getAnnotation(annotation.getName());
    }

    /**
     * INTERNAL:
     * Return the annotated element for this accessor. Note: This method does
     * not check against a meta-data complete.
     * @param annotation annotation name
     */
    public MetadataAnnotation getAnnotation(final String annotation) {
        return getAnnotation(annotation, new HashSet<>());
    }

    /**
     * INTERNAL:
     * Return the annotated element for this accessor. Note: This method does
     * not check against a meta-data complete.
     * @param annotation annotation name
     * @param names meta-annotations names cycle detection set, shall not be {@code null}
     */
    protected final MetadataAnnotation getAnnotation(final String annotation, final Set<String> names) {
        if (m_annotations == null) {
            return null;
        }
        MetadataAnnotation metadataAnnotation = m_annotations.get(annotation);
        if (metadataAnnotation == null) {
            for (MetadataAnnotation a: m_metaAnnotations.values()) {
                if (names.contains(a.getName())) {
                    if (annotation.equals(a.getName())) {
                        return a;
                    } else {
                        continue;
                    }
                }
                names.add(getName());
                MetadataAnnotation ma = m_factory.getMetadataClass(a.getName()).getAnnotation(annotation, names);
                if (ma != null) {
                    return ma;
                }
            }
        }
        return metadataAnnotation;
    }

    /**
     * INTERNAL:
     * Return the annotated element for this accessor.
     */
    public MetadataAnnotation getAnnotation(String annotationClassName, ClassAccessor classAccessor) {
        MetadataAnnotation annotation = getAnnotation(annotationClassName);

        if (annotation != null && classAccessor.ignoreAnnotations()) {
            getLogger().logConfigMessage(MetadataLogger.IGNORE_ANNOTATION, annotation, this);
            return null;
        }

        return annotation;
    }

    /**
     * INTERNAL:
     * Return the annotations of this accessible object.
     * @return annotations defined for the element, keyed by annotation name.
     *         Never returns {@code null}.
     */
    public Map<String, MetadataAnnotation> getAnnotations() {
        return m_annotations;
    }

    /**
     * INTERNAL:
     */
    @Override
    public String getAttributeName() {
        return m_attributeName;
    }

    /**
     * INTERNAL:
     */
    protected int getDeclaredAnnotationsCount(ClassAccessor classAccessor) {
        if (classAccessor.ignoreAnnotations() || m_annotations == null) {
            return 0;
        }

        return m_annotations.size();
    }

    /**
     * INTERNAL:
     */
    public List<String> getGenericType() {
        return m_genericType;
    }

    /**
     * INTERNAL:
     * This should only be called for accessor's of type Map. It will return
     * the map key type if generics are used, null otherwise.
     */
    public MetadataClass getMapKeyClass(MetadataDescriptor descriptor) {
        if (isGenericCollectionType()) {
            // The Map key may be a generic itself, or just the class value.
            String type = descriptor.getGenericType(m_genericType.get(2));
            if (type != null) {
                return getMetadataClass(type);
            }
            return getMetadataClass(m_genericType.get(1));
        } else {
            return null;
        }
    }

    /**
     * INTERNAL:
     */
    public int getModifiers() {
        return m_modifiers;
    }

    /**
     * INTERNAL:
     */
    @Override
    public String getName() {
        return m_name;
    }

    /**
     * INTERNAL:
     * If this annotated element was built from java model elements and is
     * a primitive type, this primitive type will be set.
     */
    public Object getPrimitiveType() {
        return m_primitiveType;
    }

    /**
     * INTERNAL:
     * Return the raw class for this accessible object. E.g. For an
     * accessible object with a type of java.util.Collection&lt;Employee&gt;, this
     * method will return java.util.Collection.
     * @see #getReferenceClassFromGeneric(MetadataDescriptor) to get Employee.class back.
     * @see #getRawClassWithGenerics(MetadataDescriptor) to get java.util.CollectionEmployee back.
     */
    public MetadataClass getRawClass(MetadataDescriptor descriptor) {
        if (m_rawClass == null) {
            if (isGenericType()) {
                // Seems that every generic annotated element has the T char
                // in the 0 position. The actual generic type is therefore in
                // the 1 position.
                String type = descriptor.getGenericType(getGenericType().get(1));
                if (type == null) {
                    // If the generic type can not be resolved, take a stab
                    // by returning a default class. One known case where this
                    // will hit is when processing generic accessors from a
                    // mapped superclass for the internal meta model. I wonder
                    // if returning null here would be better? Forcing the
                    // caller to have a plan B in place.
                    // @see e.g. RelationshipAccessor.getReferenceDescriptor()
                    return getMetadataClass(DEFAULT_RAW_CLASS);
                }
                return getMetadataClass(type);
            }
            return getMetadataClass(getType());
        }

        return m_rawClass;
    }

    /**
     * INTERNAL:
     * Return the complete raw class with generics for this accessible object.
     * E.g. For an accessible object with a type of
     * java.util.Collection&lt;Employee&gt;, this method will return
     * java.util.CollectionEmployee. Note, the generics are appended to the name
     * of the class.
     * @see #getReferenceClassFromGeneric(MetadataDescriptor) to get Employee.class back.
     * @see #getRawClass(MetadataDescriptor) to get java.util.Collection back.
     */
    public MetadataClass getRawClassWithGenerics(MetadataDescriptor descriptor) {
        if (m_rawClassWithGenerics == null) {
            MetadataClass rawClass = getRawClass(descriptor);

            if (getGenericType() != null && (! getGenericType().isEmpty()) && getGenericType().size() > 1) {
                StringBuilder rawClassName = new StringBuilder(32);
                rawClassName.append(rawClass.getName());

                for (int i = 1; i < getGenericType().size(); i++) {
                    rawClassName.append(getGenericType().get(i));
                }

                m_rawClassWithGenerics = getMetadataClass(rawClassName.toString());
            } else {
                m_rawClassWithGenerics = rawClass;
            }
        }

        return m_rawClassWithGenerics;
    }

    /**
     * INTERNAL:
     * Return the reference class from the generic specification on this
     * accessible object.
     * Here is what you will get back from this method given the following
     * scenarios:
     * 1 - public Collection&lt;String&gt; getTasks() =&gt; String.class
     * 2 - public Map&lt;String, Integer&gt; getTasks() =&gt; Integer.class
     * 3 - public Employee getEmployee() =&gt; null
     * 4 - public Collection getTasks() =&gt; null
     * 5 - public Map getTasks() =&gt; null
     * 6 - public Collection&lt;byte[]&gt; getAudio() =&gt; byte[].class
     * 7 - public Map&lt;X,Y&gt; on a MappedSuperclass where Y is defined in the Entity superclass&lt;T&gt; =&gt; Void.class (in all bug 266912 cases)
     */
    public MetadataClass getReferenceClassFromGeneric(MetadataDescriptor descriptor) {
        // TODO: investigate multiple levels of generics.
        if (isGenericCollectionType()) {
            // TODO: This is guessing, need to be more logical.
            // Collection<String> -> [Collection, String], get element class.
            String elementClass = m_genericType.get(1);
            if (m_genericType.size() > 2) {
                MetadataClass collectionClass = getMetadataClass(m_genericType.get(0));
                if (collectionClass.extendsInterface(Map.class)) {
                    // If size is greater than 4 then assume it is a double generic Map,
                    // Map<T, Phone> -> [Map, T, Z, T, X]
                    if (m_genericType.size() > 4) {
                        elementClass = m_genericType.get(4);
                    } else if (m_genericType.size() == 4) {
                        // If size is greater than 3 then assume it is a generic Map,
                        // Map<T, Phone> -> [Map, T, Z, Phone]
                        elementClass = m_genericType.get(3);
                    } else if (m_genericType.size() == 3) {
                        // If size is greater than 2 then assume it is a Map,
                        // Map<String, Phone> -> [Map, String, Phone]
                        elementClass = m_genericType.get(2);
                    }
                } else if (elementClass.length() == 1) {
                    // Assume Collection with a generic,
                    // Collection<T> -> [Collection T Z]
                    elementClass = m_genericType.get(2);
                }
            }
            if (elementClass.length() == 1) {
                // Assume is a generic type variable, find real type.
                elementClass = descriptor.getGenericType(elementClass);
            }
            MetadataClass metadataClass = getMetadataClass(elementClass);
            // 266912: We do not currently handle resolution of the parameterized
            // generic type when the accessor is a MappedSuperclass elementClass
            // will be null in this case so a lookup of the metadataClass will
            // also return null on our custom descriptor
            if (metadataClass == null && descriptor.isMappedSuperclass()) {
                // default to Void for all use case 7 instances above
                return new MetadataClass(getMetadataFactory(), Void.class);
            } else {
                return metadataClass;
            }
        } else {
            return null;
        }
    }

    /**
     * INTERNAL:
     */
    public String getType() {
        return m_type;
    }

    /**
     * INTERNAL:
     * Return true if this accessible object has 1 or more declared
     * persistence annotations.
     */
    public boolean hasDeclaredAnnotations(ClassAccessor classAccessor) {
        return getDeclaredAnnotationsCount(classAccessor) > 0;
    }

    /**
     * INTERNAL:
     */
    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    /**
     * INTERNAL:
     * Return true if this accessible object has 2 or more declared
     * persistence annotations.
     */
    public boolean areAnnotationsCompatibleWithTransient(ClassAccessor classAccessor) {
        List<String> annotations = TransientCompatibleAnnotations.getTransientCompatibleAnnotations();
        for (String key: m_annotations.keySet()){
            if (!key.startsWith(ECLIPSELINK_OXM_PACKAGE_PREFIX) && !annotations.contains(key)){
                return false;
            }
        }
        return true;
    }

    /**
     * INTERNAL:
     * Indicates whether the specified annotation is actually not present on
     * this accessible object. Used for defaulting. Need this check since the
     * isAnnotationPresent calls can return a false when true because of the
     * meta-data complete feature.
     */
    public boolean isAnnotationNotPresent(Class<?> annotation, ClassAccessor accessor) {
        return isAnnotationNotPresent(annotation.getName(), accessor);
    }

    /**
     * INTERNAL:
     * Indicates whether the specified annotation is actually not present on
     * this accessible object. Used for defaulting. Need this check since the
     * isAnnotationPresent calls can return a false when true because of the
     * meta-data complete feature.
     */
    public boolean isAnnotationNotPresent(String annotation, ClassAccessor accessor) {
        return ! isAnnotationPresent(annotation, accessor);
    }

    /**
     * INTERNAL:
     * Indicates whether the specified annotation is present on java class
     * for the given descriptor metadata.
     */
    public boolean isAnnotationPresent(Class<?> annotationClass, ClassAccessor accessor) {
        return isAnnotationPresent(annotationClass.getName(), accessor);
    }

    /**
     * INTERNAL:
     * Indicates whether the specified annotation is present on this accessible
     * object. NOTE: Calling this method directly does not take any metadata
     * complete flag into consideration. Look at the other isAnnotationPresent
     * methods that takes a class accessor.
     */
    public boolean isAnnotationPresent(String annotation) {
        return getAnnotation(annotation) != null;
    }

    /**
     * INTERNAL:
     * Indicates whether the specified annotation is present on java class
     * for the given descriptor metadata.
     */
    public boolean isAnnotationPresent(String annotationName, ClassAccessor accessor) {
        MetadataAnnotation annotation = getAnnotation(annotationName);

        if (annotation != null && accessor.ignoreAnnotations()) {
            getLogger().logConfigMessage(MetadataLogger.IGNORE_ANNOTATION, annotation, this);
            return false;
        } else {
            return annotation != null;
        }
    }

    /**
     * INTERNAL:
     * Return true if this field accessor represents an array relationship.
     */
    public boolean isArray(ClassAccessor classAccessor) {
        return isAnnotationPresent(Array.class, classAccessor);
    }

    /**
     * INTERNAL:
     * Return true if this accessor represents a basic mapping.
     */
    public boolean isBasic(ClassAccessor classAccessor) {
        return isAnnotationPresent(JPA_BASIC, classAccessor) ||
               isAnnotationPresent(JPA_LOB, classAccessor) ||
               isAnnotationPresent(JPA_TEMPORAL, classAccessor) ||
               isAnnotationPresent(JPA_ENUMERATED, classAccessor);
    }

    /**
     * INTERNAL:
     * Return true if this accessor represents a basic collection mapping.
     */
    @SuppressWarnings("deprecation")
    public boolean isBasicCollection(ClassAccessor classAccessor) {
        return isAnnotationPresent(BasicCollection.class, classAccessor);
    }

    /**
     * INTERNAL:
     * Return true if this accessor represents a basic collection mapping.
     */
    @SuppressWarnings("deprecation")
    public boolean isBasicMap(ClassAccessor classAccessor) {
        return isAnnotationPresent(BasicMap.class, classAccessor);
    }

    /**
     * INTERNAL:
     * Return true if this accessor represents an id mapping.
     */
    public boolean isDerivedId(ClassAccessor classAccessor) {
        return isId(classAccessor) && (isOneToOne(classAccessor) || isManyToOne(classAccessor));
    }

    /**
     * INTERNAL:
     * Return true if this accessor represents an id mapping.
     */
    public boolean isDerivedIdClass(ClassAccessor classAccessor) {
        return classAccessor.getDescriptor().isEmbeddable() && classAccessor.getProject().isIdClass(getRawClass(classAccessor.getDescriptor()));
    }

    /**
     * INTERNAL:
     * Return true if this accessor represents an element collection mapping.
     */
    public boolean isElementCollection(ClassAccessor classAccessor) {
        return isAnnotationPresent(JPA_ELEMENT_COLLECTION, classAccessor);
    }

    /**
     * INTERNAL:
     * Return true if this accessor represents an aggregate mapping. True is
     * returned if an Embedded annotation is found or if an Embeddable
     * annotation is found on the raw/reference class.
     */
    public boolean isEmbedded(ClassAccessor classAccessor) {
        if (isAnnotationNotPresent(JPA_EMBEDDED, classAccessor) && isAnnotationNotPresent(JPA_EMBEDDED_ID, classAccessor) && ! classAccessor.excludeDefaultMappings()) {
            MetadataClass rawClass = getRawClass(classAccessor.getDescriptor());
            return (rawClass.isAnnotationPresent(JPA_EMBEDDABLE) || classAccessor.getProject().hasEmbeddable(rawClass));
        } else {
            // Still need to make the call since we may need to ignore it
            // because of meta-data complete.
            return isAnnotationPresent(JPA_EMBEDDED, classAccessor);
        }
    }

    /**
     * INTERNAL:
     * Return true if this accessor represents an aggregate id mapping.
     */
    public boolean isEmbeddedId(ClassAccessor classAccessor) {
        return isAnnotationPresent(JPA_EMBEDDED_ID, classAccessor);
    }

    /**
     * INTERNAL:
     * Method to return whether a collection type is a generic.
     */
    public boolean isGenericCollectionType() {
        return (m_genericType != null) && (m_genericType.size() > 1);
    }

    /**
     * INTERNAL:
     * Method to return whether a type is a generic.
     */
    public boolean isGenericType() {
        return (m_genericType != null) && (m_genericType.size() > 1) && (m_genericType.get(0).length() == 1);
    }

    /**
     * INTERNAL:
     * Return true if this accessor represents an id mapping.
     */
    public boolean isId(ClassAccessor classAccessor) {
        return isAnnotationPresent(JPA_ID, classAccessor);
    }

    /**
     * INTERNAL:
     * Return true if this field accessor represents a m-m relationship.
     */
    public boolean isManyToMany(ClassAccessor classAccessor) {
        return isAnnotationPresent(JPA_MANY_TO_MANY, classAccessor);
    }

    /**
     * INTERNAL:
     * Return true if this accessor represents a m-1 relationship.
     */
    public boolean isManyToOne(ClassAccessor classAccessor) {
        return isAnnotationPresent(JPA_MANY_TO_ONE, classAccessor);
    }

    /**
     * INTERNAL:
     * Return true if this accessor represents a 1-m relationship.
     */
    public boolean isOneToMany(ClassAccessor classAccessor) {
        if (isAnnotationNotPresent(JPA_ONE_TO_MANY, classAccessor) && ! classAccessor.excludeDefaultMappings()) {
            if (isGenericCollectionType() && isSupportedToManyCollectionClass(getRawClass(classAccessor.getDescriptor())) && classAccessor.getProject().hasEntity(getReferenceClassFromGeneric(classAccessor.getDescriptor()))) {
                getLogger().logConfigMessage(MetadataLogger.ONE_TO_MANY_MAPPING, this);
                return true;
            }

            return false;
        } else {
            // Still need to make the call since we may need to ignore it
            // because of meta-data complete.
            return isAnnotationPresent(JPA_ONE_TO_MANY, classAccessor);
        }
    }

    /**
     * INTERNAL:
     * Return true if this accessor represents a 1-1 relationship.
     */
    public boolean isOneToOne(ClassAccessor classAccessor) {
        if (isAnnotationNotPresent(JPA_ONE_TO_ONE, classAccessor) && ! classAccessor.excludeDefaultMappings()) {
            if (classAccessor.getProject().hasEntity(getRawClass(classAccessor.getDescriptor())) && ! isEmbedded(classAccessor)) {
                getLogger().logConfigMessage(MetadataLogger.ONE_TO_ONE_MAPPING, this);
                return true;
            } else {
                return false;
            }
        } else {
            // Still need to make the call since we may need to ignore it
            // because of meta-data complete.
            return isAnnotationPresent(JPA_ONE_TO_ONE, classAccessor);
        }
    }

    /**
     * INTERNAL:
     * Return true if this field accessor represents a structure relationship.
     */
    public boolean isStructure(ClassAccessor classAccessor) {
        return isAnnotationPresent(Structure.class, classAccessor);
    }

    /**
     * INTERNAL:
     * Method to return whether the given class is a supported collection class.
     */
    public boolean isSupportedCollectionClass(MetadataClass metadataClass) {
        return metadataClass.isCollection();
    }

    /**
     * INTERNAL:
     * Method to return whether the given class is a supported map class.
     */
    public boolean isSupportedMapClass(MetadataClass metadataClass) {
        return metadataClass.isMap();
    }

    /**
     * INTERNAL:
     * Method to return whether the given class is a supported to many
     * collection class.
     */
    public boolean isSupportedToManyCollectionClass(MetadataClass metadataClass) {
        return isSupportedCollectionClass(metadataClass) || isSupportedMapClass(metadataClass);
    }

    /**
     * INTERNAL:
     * Return true if this accessor represents an transformation mapping.
     */
    public boolean isTransformation(ClassAccessor classAccessor) {
        return isAnnotationPresent(ReadTransformer.class, classAccessor) ||
               isAnnotationPresent(WriteTransformers.class, classAccessor) ||
               isAnnotationPresent(WriteTransformer.class, classAccessor);
    }

    /**
     * INTERNAL:
     * When processing the inverse accessors to an explicit access setting,
     * their must be an Access(FIELD) or Access(PROPERTY) present for the
     * element to be processed. Otherwise, it is ignored.
     */
    protected boolean isValidPersistenceElement(boolean mustBeExplicit, String explicitType, ClassAccessor classAccessor) {
        if (mustBeExplicit) {
            MetadataAnnotation annotation = getAnnotation(JPA_ACCESS, classAccessor);

            if (annotation == null) {
                return false;
            } else {
                String access = annotation.getAttributeString("value");

                if (! access.equals(explicitType)) {
                    throw ValidationException.invalidExplicitAccessTypeSpecified(this, classAccessor.getDescriptorJavaClass(), explicitType);
                }
            }
        }

        return true;
    }

    /**
     * INTERNAL:
     * Return true if the modifiers are not transient, static or abstract.
     */
    protected boolean isValidPersistenceElement(int modifiers) {
        return ! (Modifier.isTransient(modifiers) || Modifier.isStatic(modifiers) || Modifier.isAbstract(modifiers));
    }

    /**
     * INTERNAL:
     * Return true if this accessor represents a variable 1-1 relationship.
     * The method will return true if one of the following conditions is met:
     *  - There is a VariableOneToOne annotation present, or
     *  - The raw class is an interface and not a collection or map, nor a
     *    ValueHolderInterface (and an exclude default mappings flag is not set)
     */
    public boolean isVariableOneToOne(ClassAccessor classAccessor) {
        if (isAnnotationNotPresent(VariableOneToOne.class, classAccessor) && ! classAccessor.excludeDefaultMappings()) {
            MetadataClass rawClass = getRawClass(classAccessor.getDescriptor());

            if (rawClass.isInterface() &&
                    ! rawClass.isMap() &&
                    ! rawClass.isCollection() &&
                    ! rawClass.isSerializableInterface() &&
                    ! rawClass.extendsInterface(ValueHolderInterface.class) &&
                    classAccessor.getProject().hasEntityThatImplementsInterface(rawClass.getName())) {
                getLogger().logConfigMessage(MetadataLogger.VARIABLE_ONE_TO_ONE_MAPPING, this);
                return true;
            }

            return false;
        } else {
            // Still need to make the call since we may need to ignore it
            // because of meta-data complete.
            return isAnnotationPresent(VariableOneToOne.class, classAccessor);
        }
    }

    /**
     * INTERNAL:
     * Return true if this accessor represents a version mapping.
     */
    public boolean isVersion(ClassAccessor classAccessor) {
        return isAnnotationPresent(JPA_VERSION, classAccessor);
    }

    /**
     * INTERNAL:
     * Set the annotations of this accessible object.
     */
    public void setAnnotations(Map<String, MetadataAnnotation> annotations) {
        m_annotations = annotations;
    }

    /**
     * INTERNAL:
     */
    public void setAttributeName(String attributeName) {
        m_attributeName = attributeName;
    }

    /**
     * INTERNAL:
     */
    public void setGenericType(List<String> genericType) {
        m_genericType = genericType;
    }

    /**
     * INTERNAL:
     */
    public void setModifiers(int modifiers) {
        m_modifiers = modifiers;
    }

    /**
     * INTERNAL:
     */
    public void setName(String name) {
        m_name = name;
    }

    /**
     * INTERNAL:
     * If this annotated element was built from java model elements and is
     * a primitive type this method will be called.
     */
    public void setPrimitiveType(Object primitiveType) {
        m_primitiveType = primitiveType;
    }

    /**
     * INTERNAL:
     */
    public void setType(String type) {
        m_type = type;
    }

    /**
     * INTERNAL:
     */
    @Override
    public String toString() {
        String className = getClass().getSimpleName();
        return className.substring("Metadata".length()).toLowerCase() + " " + getName();
    }
}
