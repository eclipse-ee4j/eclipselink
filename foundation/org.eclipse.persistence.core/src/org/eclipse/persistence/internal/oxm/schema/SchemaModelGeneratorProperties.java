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
* dmccann - Mar 2/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.oxm.schema;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.persistence.internal.oxm.schema.model.Schema;

/**
 * INTERNAL:
 *  <p><b>Purpose:</b>Encapsulates a Map of Namespace URIs to Properties. 
 *  <p><b>Responsibilities:</b><ul>
 *  <li>Provide an API to set key/value pairs on a per namespace URI bases</li>
 *  <li>Lazily initialize a new Properties object for each namespace</li>
 *  <li>Provide an API to retrieve the entire map of namespace URI to Properties</li>
 *  <li>Provide an API to retrieve the property value for a given namespace URI and key</li>
 *  </ul>
 *  <p> This class is used to hold onto a Map of Properties objects on a per namespace basis.
 *  It is intended to be used in conjunction with the SchemaModelGenerator class, to hand in
 *  Properties to be applied to the Schema that is generated for a given namespace.
 *  
 *  @see Schema
 *  @see SchemaModelGenerator
 *  @see Properties
 */
public class SchemaModelGeneratorProperties {
    protected Map<String, Properties> propMap;
    // statics
    public static final String ATTRIBUTE_FORM_QUALIFIED_KEY = "attributeFormQualified";
    public static final String ELEMENT_FORM_QUALIFIED_KEY = "elementFormQualified";

    public SchemaModelGeneratorProperties() {}
    
    /**
     * Lazily initialize the URI->Properties map
     * @return
     */
    public Map<String, Properties> getPropertiesMap() {
        if (propMap == null) {
            propMap = new HashMap<String, Properties>();
        }
        return propMap;
    }

    /**
     * Adds the key/value pair to the Properties object associated with the given
     * namespace URI.  If no entry exists for the given URI, a Properties object
     * will be created.
     * 
     * @param uri
     * @param key
     * @param value
     */
    public void addProperty(String uri, String key, Object value) {
        if (uri == null || key == null || value == null) {
            return;
        }
        Map<String, Properties> pMap = getPropertiesMap();
        Properties props = pMap.get(uri);
        if (props == null) {
            props = new Properties();
            pMap.put(uri, props);
        }
        props.put(key, value);
    }

    /**
     * Return the property value for a given namespace/key pair.
     * 
     * @param uri
     * @param key
     * @return
     */
    public Object getProperty(String uri, String key) {
        if (uri == null || key == null) {
            return null;
        }
        Map<String, Properties> pMap = getPropertiesMap();
        Properties props = pMap.get(uri);
        if (props == null) {
            return null;
        }
        return props.get(key);
    }
    
    /**
     * Return the Properties object for the given namespace uri.
     * If none exists a new Properties is created and returned.
     *  
     * @return
     */
    public Properties getProperties(String uri) {
        return getPropertiesMap().get(uri);
    }
}
