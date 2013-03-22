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
 *     dclarke, mnorman - Dynamic Persistence
 *       http://wiki.eclipse.org/EclipseLink/Development/Dynamic 
 *       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
 *
 ******************************************************************************/
package org.eclipse.persistence.internal.dynamic;

//EclipseLink imports
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl.PropertyWrapper;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

public class DynamicPropertiesInitializatonPolicy {

    /**
     * After entity is created, initialize all required attributes.
     * @param type
     * @param entity
     */
    public void initializeProperties(DynamicTypeImpl type, DynamicEntityImpl entity) {
        if (type != null) {
            for (DatabaseMapping mapping : type.getMappingsRequiringInitialization()) {
                initializeDefaultValue(mapping, entity);
            }
        }
    }
    
    /**
     * Initialize the default value handling primitives, collections and
     * indirection.
     * 
     * @param mapping
     * @param entity
     */
    private void initializeDefaultValue(DatabaseMapping mapping, DynamicEntityImpl entity) {
        Object value = null;
        if (mapping.isDirectToFieldMapping() && mapping.getAttributeClassification().isPrimitive()) {
            Class<?> primClass = mapping.getAttributeClassification();
            if (primClass == ClassConstants.PBOOLEAN) {
                value = false;
            }
            else if (primClass == ClassConstants.PINT) {
                value = 0;
            }
            else if (primClass == ClassConstants.PLONG) {
                value = 0L;
            }
            else if (primClass == ClassConstants.PCHAR) {
                value = Character.MIN_VALUE;
            }
            else if (primClass == ClassConstants.PDOUBLE) {
                value = 0.0d;
            }
            else if (primClass == ClassConstants.PFLOAT) {
                value = 0.0f;
            }
            else if (primClass == ClassConstants.PSHORT) {
                value = Short.MIN_VALUE;
            }
            else if (primClass == ClassConstants.PBYTE) {
                value = Byte.MIN_VALUE;
            }
        }
        else if (mapping.isForeignReferenceMapping()) {
            ForeignReferenceMapping refMapping = (ForeignReferenceMapping)mapping;
            if (refMapping.usesIndirection() && 
                refMapping.getIndirectionPolicy() instanceof BasicIndirectionPolicy) {
                value = new ValueHolder(value);
            }
            else if (refMapping.isCollectionMapping()) {
                value = ((CollectionMapping)refMapping).getContainerPolicy().containerInstance();
            }
        }
        else if (mapping.isAggregateObjectMapping()) {
            value = mapping.getReferenceDescriptor().getObjectBuilder().buildNewInstance();
        }
        PropertyWrapper propertyWrapper = entity.getPropertiesMap().get(mapping.getAttributeName());
        // NB - only the value is set, not the 'isSet' boolean
        propertyWrapper.setValue(value);
    }
}
