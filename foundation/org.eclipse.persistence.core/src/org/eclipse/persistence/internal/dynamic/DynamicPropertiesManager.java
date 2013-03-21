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

//javase imports
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.exceptions.DynamicException;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl.PropertyWrapper;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * Information about a property is extracted (thru DynamicType) from the entity's ClassDescriptor
 * <p>
 * <b><u>Important</u></b> - DynamicPropertiesManager is <b>NOT</b> thread-safe
 * @author mnorman
 */
public class DynamicPropertiesManager {
    
    /**
     * All DynamicEntity classes have a public static field 'DPM' of type DynamicPropertiesManager
     * 
     */
    public static final String PROPERTIES_MANAGER_FIELD = "DPM";

    protected DynamicTypeImpl type;
    protected DynamicPropertiesInitializatonPolicy dpInitializatonPolicy = 
        new DynamicPropertiesInitializatonPolicy();
    
    public DynamicPropertiesManager() {
        super();
    }

    public DynamicType getType() {
        return type;
    }
    public void setType(DynamicType type) {
        this.type = (DynamicTypeImpl)type;
    }

    public DynamicPropertiesInitializatonPolicy getInitializatonPolicy() {
        return dpInitializatonPolicy;
    }
    public void setInitializatonPolicy(DynamicPropertiesInitializatonPolicy dpInitializatonPolicy) {
        this.dpInitializatonPolicy = dpInitializatonPolicy;
    }

    // lifecycle callback
    public void postConstruct(DynamicEntity entity) {
        // first step, create 'slots' in propertiesMap
        createSlots((DynamicEntityImpl)entity);
        // next step, initialize 'slot' values
        initializeSlotValues((DynamicEntityImpl)entity);
    }
    
    protected void createSlots(DynamicEntityImpl entity) {
        Map<String, PropertyWrapper> propertiesMap = entity.getPropertiesMap();
        List<String> propertyNames = getPropertyNames();
        if (propertyNames != null) {
            for (String propertyName : propertyNames) {
                propertiesMap.put(propertyName, entity.new PropertyWrapper());
            }
        }
    }
    
    protected void initializeSlotValues(DynamicEntityImpl entity) {
        getInitializatonPolicy().initializeProperties((DynamicTypeImpl)type, entity);
    }
    
    // delegate to descriptor
    public boolean contains(String propertyName) {
        boolean contains = false;
        if (type != null && type.getDescriptor() != null) {
            for (DatabaseMapping dm : type.getDescriptor().getMappings()) {
                if (dm.getAttributeName().equals(propertyName)) {
                    contains = true;
                    break;
                }
            }
        }
        return contains;
    }

    // delegate to descriptor
    public List<String> getPropertyNames() {
        List<String> propertyNames = new ArrayList<String>();
        if (type != null && type.getDescriptor() != null) {
            for (DatabaseMapping dm : type.getDescriptor().getMappings()) {
                propertyNames.add(dm.getAttributeName());
            }
        }
        return propertyNames;
    }

    public void checkSet(String propertyName, Object value) {
        if (contains(propertyName)) {
            type.checkSet(propertyName, value);
        }
        else {
            throw DynamicException.invalidPropertyName(type, propertyName);
        }
    }

}
