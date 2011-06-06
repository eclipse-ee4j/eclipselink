/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial impl
 *     10/21/2009-2.0 Guy Pelletier 
 *       - 290567: mappedbyid support incomplete
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.objects;

import java.util.HashMap;
import java.util.Map;

/**
 * Metadata representation of an annotation.
 * 
 * @author James Sutherland
 * @since EclipseLink 1.2
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
     * Return the attribute value, or null if not set. Callers to this method
     * should only use it if the annotation requires the attribute. Otherwise,
     * you should call one of the more specific getAttribute calls that will
     * return the annotation default value.
     * @see getAttributeArray
     * @see getAttributeString
     * @see getAttributeBooleanDefaultFalse
     * @see getAttributeBooleanDefaultTrue
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
        return (value == null) ? new Object[0] : value;
    }
    
    /**
     * INTERNAL:
     * Return the boolean attribute value, or the default value if not set.
     */
    public Object getAttributeBoolean(String name, Boolean defaultValue) {
        Object value = getAttribute(name);
        return (value == null) ? defaultValue : value;
    }
    
    /**
     * INTERNAL:
     * Return the boolean attribute value, or FALSE if not set.
     */
    public Object getAttributeBooleanDefaultFalse(String name) {
        return getAttributeBoolean(name, Boolean.FALSE);
    }
    
    /**
     * INTERNAL:
     * Return the boolean attribute value, or TRUE if not set.
     */
    public Object getAttributeBooleanDefaultTrue(String name) {
        return getAttributeBoolean(name, Boolean.TRUE);
    }
    
    /**
     * INTERNAL:
     * Return the Class attribute value, or void if not set.
     */
    public Object getAttributeClass(String name) {
        Object value = getAttribute(name);
        return (value == null) ? "void" : value;
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
        return (value == null) ? "" : value;
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
    @Override
    public String toString() {
        return "@" + getName() + "(" + m_attributes + ")";
    }
}
