/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dclarke, mnorman - Dynamic Persistence
 *       http://wiki.eclipse.org/EclipseLink/Development/Dynamic 
 *       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
 *
 ******************************************************************************/
package org.eclipse.persistence.dynamic;

//javase imports
import java.util.List;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * An EntityType provides a metadata facade into the EclipseLink
 * object-relational metadata (descriptors & mappings) with specific knowledge
 * of the entity types being dynamic.
 * 
 * @author dclarke, mnorman
 * @since EclipseLink 1.2
 */
public interface DynamicType {

    /**
     * Return the entity type's name. This is the short name of the class or the
     * {@link ClassDescriptor#getAlias()}
     */
    public String getName();

    /**
     * @return Fully qualified name of mapped class.
     */
    public String getClassName();

    /**
     * @return The parent type or null if this type does not have a persistent
     *         superclass
     */
    public DynamicType getParentType();

    /**
     * The current number of properties.
     * <p>
     * Note: Some implementations support adding mapped attributes at runtime so
     * it is best to avoid caching the result.
     */
    public int getNumberOfProperties();

    /**
     * The current names of properties.
     * <p>
     * Note: Some implementations support adding mapped attributes at runtime so
     * it is best to avoid caching the result.
     */
    public List<String> getPropertiesNames();

    public boolean containsProperty(String propertyName);

    public int getPropertyIndex(String propertyName);

    public Class<? extends DynamicEntity> getJavaClass();

    public DynamicEntity newDynamicEntity();

    public Class<?> getPropertyType(int propertyIndex);

    public Class<?> getPropertyType(String propertyName);

    /**
     * @return the underlying {@link ClassDescriptor} for the mapped type
     */
    public ClassDescriptor getDescriptor();

    /**
     * Property name used to store the EntityTypeImpl on each descriptor in its
     * {@link ClassDescriptor#properties}. The EntityType instance is generally
     * populated by the {@link DynamicTypeBuilder} and should only be done when
     * properly initialized.
     */
    public static final String DESCRIPTOR_PROPERTY = "ENTITY_TYPE";

}
