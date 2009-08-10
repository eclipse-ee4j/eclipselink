/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial impl
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.objects;

import java.util.HashMap;
import java.util.Map;

/**
 * Metadata representation of an annotation.
 * 
 * @author James Sutherland
 * @since EclipseLink 2.0
 */
public class MetadataAnnotation {
    /** The name of the annotation. */
    protected String m_name;
    
    /** Map of attribute names and values. */
    protected Map<String, Object> m_attributes = new HashMap<String, Object>();

    /**
     * INTERNAL:
     */
    public void addAttribute(String key, Object value) {
        m_attributes.put(key, value);
    }
    
    /**
     * INTERNAL:
     * Return the attribute value, or null if not set.
     */
    public Object getAttribute(String name) {
        return m_attributes.get(name);
    }

    /**
     * INTERNAL:
     * Return the attribute value, or an empty array if not set.
     */
    public Object getAttributeArray(String name) {
        Object value = getAttribute(name);
        if (value == null) {
            return new Object[0];
        }
        return value;
    }
    
    /**
     * INTERNAL:
     */
    public Map<String, Object> getAttributes() {
        return m_attributes;
    }

    /**
     * INTERNAL:
     * Return the attribute value, or "" if not set.
     */
    public Object getAttributeString(String name) {
        Object value = getAttribute(name);
        if (value == null) {
            return "";
        }
        return value;
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
    public void setAttributes(Map<String, Object> attributes) {
        m_attributes = attributes;
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
    public String toString() {
        return "@" + getName() + "(" + m_attributes + ")";
        //return "@" + getName();
    }
}
