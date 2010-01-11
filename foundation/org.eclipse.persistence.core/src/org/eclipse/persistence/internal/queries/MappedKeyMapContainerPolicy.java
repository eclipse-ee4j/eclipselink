/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - initial API check-in for MappedKeyMapContainerPolicy
 ******************************************************************************/
package org.eclipse.persistence.internal.queries;

import org.eclipse.persistence.mappings.foundation.MapKeyMapping;
import org.eclipse.persistence.mappings.foundation.MapComponentMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * A MappedKeyMapContainerPolicy should be used for mappings to implementers of Map.
 * It differs from MapContainerPolicy by allowing the MapKey to be an otherwise unmapped
 * column in a table rather than a mapped element of the value in the map.
 * 
 * This container policy holds a reference to a KeyMapping that will be used to construct the key
 * from the database and a reference to its owner which creates the value for the map.
 * 
 * The key of the map can be any implementer of MapKeyMapping and the data representing the
 * key can either be stored in the target table of the value mapping, or in a collection table that
 * associates the source to the target.   The data can either be everything necessary to compose the
 * key, or foreign keys that allow the key to be retrieved
 * 
 * @see MapContainerPolicy
 * @see MapKeyMapping
 * @see MapComponentMapping
 * 
 * @author tware
 *
 */
public class MappedKeyMapContainerPolicy extends MapContainerPolicy {
    
    protected transient MapKeyMapping keyMapping;

    protected transient MapComponentMapping valueMapping;
    
    /**
     * INTERNAL:
     * Construct a new policy.
     */
    public MappedKeyMapContainerPolicy() {
        super();
    }

    /**
     * INTERNAL:
     * Construct a new policy for the specified class.
     */
    public MappedKeyMapContainerPolicy(Class containerClass) {
        super(containerClass);
    }

    /**
     * INTERNAL:
     * Construct a new policy for the specified class name.
     */
    public MappedKeyMapContainerPolicy(String containerClassName) {
        super(containerClassName);
    }
    
    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this ContainerPolicy to actual class-based
     * settings
     * This method is implemented by subclasses as necessary.
     * @param classLoader 
     */
    public void convertClassNamesToClasses(ClassLoader classLoader){
        ((DatabaseMapping)keyMapping).convertClassNamesToClasses(classLoader);
    };
    
    public MapKeyMapping getKeyMapping(){
        return keyMapping;
    }
    
    public MapComponentMapping getValueMapping(){
        return valueMapping;
    }
    
    public void setKeyMapping(MapKeyMapping mapping){
        this.keyMapping = mapping;
    }
    
    public void setValueMapping(MapComponentMapping mapping){
        this.valueMapping = mapping;
    }
}
