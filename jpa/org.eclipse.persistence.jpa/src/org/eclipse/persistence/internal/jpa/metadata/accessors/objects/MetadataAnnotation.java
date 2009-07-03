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
    protected String name;
    
    /** Map of attribute names and values. */
    protected Map<String, Object> attributes = new HashMap<String, Object>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * Return the attribute value, or null if not set.
     */
    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    /**
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
     * Return the attribute value, or an empty array if not set.
     */
    public Object getAttributeArray(String name) {
        Object value = getAttribute(name);
        if (value == null) {
            return new Object[0];
        }
        return value;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String toString() {
        return "@" + getName();
    }
}
