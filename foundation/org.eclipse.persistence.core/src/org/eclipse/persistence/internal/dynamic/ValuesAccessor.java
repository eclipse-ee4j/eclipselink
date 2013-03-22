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
package org.eclipse.persistence.internal.dynamic;

//javase imports
import java.util.Map;

//EclipseLink imports
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl.PropertyWrapper;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

/**
 * ValueAccessor is a specialized AttributeAccessor enabling access to property values
 * 
 * @author dclarke, mnorman
 * @since EclipseLink 1.2
 */
public class ValuesAccessor extends AttributeAccessor {

    protected DatabaseMapping mapping;
    
    public ValuesAccessor(DatabaseMapping mapping) {
        super();
        this.mapping = mapping;
        setAttributeName(mapping.getAttributeName());
    }

    @Override
    public boolean isValuesAccessor() {
    	return true;
    }
    
    public Object getAttributeValueFromObject(Object entity) throws DescriptorException {
        Map<String, PropertyWrapper> propertiesMap = ((DynamicEntityImpl)entity).getPropertiesMap();
        PropertyWrapper wrapper = propertiesMap.get(attributeName);
        // wrapper is never null
        return wrapper.getValue();
    }

    public void setAttributeValueInObject(Object entity, Object value) throws DescriptorException {
        Map<String, PropertyWrapper> propertiesMap = ((DynamicEntityImpl)entity).getPropertiesMap();
        PropertyWrapper wrapper = propertiesMap.get(attributeName);
        // wrapper is never null
        wrapper.setValue(value);
        wrapper.isSet(true);
    }

    @Override
    public Class<?> getAttributeClass() {
        if (mapping.isForeignReferenceMapping()) {
            ForeignReferenceMapping refMapping = (ForeignReferenceMapping)mapping;
            if (refMapping.isCollectionMapping()) {
                return ((CollectionMapping)refMapping).getContainerPolicy().getContainerClass();
            }
            if (refMapping.usesIndirection()) {
                return ValueHolderInterface.class;
            }
            return refMapping.getReferenceClass();
        }
        else {
            if (mapping.getAttributeClassification() == null) {
                return ClassConstants.OBJECT;
            }
            return mapping.getAttributeClassification();
        }
    }
}
