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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm.documentpreservation;

import java.util.HashMap;

import org.eclipse.persistence.internal.oxm.mappings.Mapping;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Provide an object to be used as a cache entry in the XML Binder cache. This 
 * entry holds on to the root object mapped to a given XML node as well as any self-mapping objects, keyed
 * on Mapping.
 * <p><b>Responsibilities:</b><ul>
 * <li>Hold onto a rootObject associated with a given node</li>
 * <li>Maintain a map of any selfMapping objects keyed on mapping</li>
 * </ul>
 * @author mmacivor
 *
 */
public class XMLBinderCacheEntry {
    private Object rootObject;
    private HashMap selfMappingObjects;
    
    public XMLBinderCacheEntry(Object root) {
        rootObject = root;
    }
    
    public Object getRootObject() {
        return rootObject;
    }
    
    /**
     * @since EclipseLink 2.5.0
     */
    public void addSelfMappingObject(Mapping mapping, Object obj) {
        if(selfMappingObjects == null) {
            selfMappingObjects = new HashMap();
        }
        selfMappingObjects.put(mapping, obj);
    }

    /**
     * @since EclipseLink 2.5.0
     */
    public Object getSelfMappingObject(Mapping mapping) {
        if(selfMappingObjects != null) {
            return selfMappingObjects.get(mapping);
        }
        return null;
    }

}