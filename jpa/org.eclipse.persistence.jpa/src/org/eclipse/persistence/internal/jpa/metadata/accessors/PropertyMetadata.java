/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * Contributors:
 *     Andrei Ilitchev (Oracle), April 8, 2008 
 *        - New file introduced for bug 217168.
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors;

import org.eclipse.persistence.internal.helper.ConversionManager;

/**
 * PropertyMetadata. Each mapping may be assigned user-defined properties.
 * 
 * @author Andrei Ilitchev
 * @since EclipseLink 1.0 
 */
public class PropertyMetadata {
    private String m_name;
    private String m_value;
    private Class m_valueType;
    private String m_valueTypeName;
    
    /**
     * INTERNAL:
     */
    public PropertyMetadata() {
        super();
    }
    
    /**
     * INTERNAL:
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * INTERNAL:
     */
    public void setName(String name) {
        m_name = name;
    }
    
    /**
     * INTERNAL:
     */
    public String getValue() {
        return m_value;
    }
    
    /**
     * INTERNAL:
     */
    public void setValue(String value) {
        m_value = value;
    }

    /**
     * INTERNAL:
     */
    public Class getValueType() {
        return m_valueType;
    }
    
    /**
     * INTERNAL:
     */
    public void setValueType(Class valueType) {
        m_valueType = valueType;
    }

    /**
     * INTERNAL:
     */
    public String getValueTypeName() {
        return m_valueTypeName;
    }
    
    /**
     * INTERNAL:
     */
    public void setValueTypeName(String valueTypeName) {
        m_valueTypeName = valueTypeName;
    }

    /**
     * INTERNAL:
     */
    public Object getConvertedValue() {
        if(m_valueType == null || m_valueType.equals(String.class)) {
            return m_value;
        } else {
            return ConversionManager.getDefaultManager().convertObject(m_value, m_valueType);
        }
    }
}
