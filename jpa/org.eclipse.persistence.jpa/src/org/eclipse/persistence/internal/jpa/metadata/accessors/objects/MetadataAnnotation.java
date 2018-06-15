/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     James Sutherland - initial impl
//     10/21/2009-2.0 Guy Pelletier
//       - 290567: mappedbyid support incomplete
//      //     30/05/2012-2.4 Guy Pelletier
//       - 354678: Temp classloader is still being used during metadata processing
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
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

    /** Metadata annotation flag */
    protected boolean isMeta = false;

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
     * Return the attribute value, or null if not set. This should remain a
     * private method and callers should use one of the more specific
     * getAttribute calls. (and build one if necessary)
     *
     * @see getAttributeAnnotation
     * @see getAttributeArray
     * @see getAttributeInteger
     * @see getAttributeString
     * @see getAttributeBoolean
     * @see getAttributeBooleanDefaultFalse
     * @see getAttributeBooleanDefaultTrue
     */
    private Object getAttribute(String name) {
        return m_attributes.get(name);
    }

    /**
     * INTERNAL:
     * Return annotation attribute value. You should call this method only
     * if you know the object returned will be a MetadataAnnotation.
     */
    public MetadataAnnotation getAttributeAnnotation(String name) {
        Object value = getAttribute(name);
        return (value == null) ? null : (MetadataAnnotation) value;
    }

    /**
     * INTERNAL:
     * Return the attribute value, or an empty array if not set. Callers will
     * have the cast the type.
     */
    public Object[] getAttributeArray(String name) {
        Object value = getAttribute(name);
        return (value == null) ? new Object[0] : (Object[]) value;
    }

    /**
     * INTERNAL:
     * Return the boolean attribute value, or the default value if not set.
     */
    public Boolean getAttributeBoolean(String name, Boolean defaultValue) {
        Object value = getAttribute(name);
        return (value == null) ? defaultValue : (Boolean) value;
    }

    /**
     * INTERNAL:
     * Return the boolean attribute value, or FALSE if not set.
     */
    public Boolean getAttributeBooleanDefaultFalse(String name) {
        return getAttributeBoolean(name, Boolean.FALSE);
    }

    /**
     * INTERNAL:
     * Return the boolean attribute value, or TRUE if not set.
     */
    public Boolean getAttributeBooleanDefaultTrue(String name) {
        return getAttributeBoolean(name, Boolean.TRUE);
    }

    /**
     * INTERNAL:
     * Return the Class attribute value, or the default provided.
     */
    public String getAttributeClass(String name, Class defaultClass) {
        Object value = getAttribute(name);
        return (value == null) ? defaultClass.getName() : (String) value;
    }

    /**
     * INTERNAL:
     * Return Integer attribute value. You should call this method only
     * if you know the object returned will be an Integer.
     */
    public Integer getAttributeInteger(String name) {
        Object value = getAttribute(name);
        return (value == null) ? null : (Integer) value;
    }

    /**
     * INTERNAL:
     */
    public Map<String, Object> getAttributes() {
        return m_attributes;
    }

    /**
     * INTERNAL:
     * Return the attribute value, or null if not set.
     */
    public String getAttributeString(String name) {
        return (String) getAttribute(name);
    }

    /**
     * INTERNAL:
     * Return the attribute value, or defaultValue if not set.
     */
    public String getAttributeString(String name, String defaultValue) {
        Object value = getAttribute(name);
        return (value == null) ? defaultValue : (String) value;
    }

    /**
     * INTERNAL:
     */
    public String getName() {
        return m_name;
    }

    /**
     * INTERNAL:
     * Return true if the attribute exists.
     */
    public boolean hasAttribute(String name) {
        return getAttribute(name) != null;
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
    public void setIsMeta(boolean isMeta) {
        this.isMeta = isMeta;
    }

    /**
     * INTERNAL:
     * @return true if this annotation is a meta-annotation
     */
    public boolean isMeta() {
        return this.isMeta;
    }

    /**
     * INTERNAL:
     */
    @Override
    public String toString() {
        return "@" + getName() + "(" + m_attributes + ")";
    }
}
