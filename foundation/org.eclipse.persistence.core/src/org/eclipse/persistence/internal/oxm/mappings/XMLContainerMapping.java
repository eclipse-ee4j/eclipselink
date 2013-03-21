/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm.mappings;

import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;

/**
 * This interface represents behaviour that is common to all XML mappings that
 * involve containers:  Collection, Map, Arrays, etc.
 */
public interface XMLContainerMapping {

    /**
     * This is the default setting for the defaultEmptyContainer property.  This
     * can be overridden at the mapping level.
     */
    static final boolean EMPTY_CONTAINER_DEFAULT = true; 

    /**
     * Return true if a pre-initialized container already set on the 
     * field/property should be used.  If false a new container will always be 
     * created.
     */
    boolean getReuseContainer();

    /**
     * Get the Wrapper NullPolicy from the Mapping.
     */
    public AbstractNullPolicy getWrapperNullPolicy();

    /**
     * Return true if an empty container should be set on the object if there
     * is no presence of the collection in the XML document.
     */
    boolean isDefaultEmptyContainer();

    /**
     * Indicate whether by default an empty container should be set on the 
     * field/property if the collection is not present in the XML document.
     */
    void setDefaultEmptyContainer(boolean defaultEmptyContainer);

    /**
     * Specify if a pre-existing container on the field/property should be used.
     */
    void setReuseContainer(boolean reuseContainer);

    /**
     * Set the Wrapper NullPolicy on the Mapping.
     */
    void setWrapperNullPolicy(AbstractNullPolicy policy);

}