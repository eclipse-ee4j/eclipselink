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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sdo;

import commonj.sdo.DataObject;
import commonj.sdo.Property;

/**
 * <p><b>Purpose</b>
 *  The ValueStore interface exposes the property model in SDO to pluggable implementers
 *   that wish to wrap their object model in the SDO API.</p>
 *   <p>
 *   The ValueStore interface exposes the properties Map in SDODataObject to extension or replacement.
 *   The implementing class must override or extend the get/set/isSet/unset ValueStore functions so that
 *   any calls routed through the SDO API are routed through to the underlying Data-Model that ValueStore wraps.
 *   </p></p>
 *   Possible implementers must maintain DataObject integrity
 *   which includes containment and changeSummary.
 *   
 *   </p><p>Setup:<br>
 *   Before using the ValueStore interface the type tree must be defined by loading in the
 *   XSD schema file that matches the plugged in model if using dynamic classes.
 * </p><p>  
 * The ValueStore interface enables 3rd party implementers of the SDO API to plug-in their
 * object model in place of the default SDO DataObject interface.
 * The Map like functions set/get and the List like functions isSet/unset are left to the
 * implementation to maintain.<br>
 * It is the responsibility of the implementation to handle read or read/write capability against the
 * underlying object model.  Any access or modification of the pluggable object model done
 * outside of this interface may destabilize the ValueStore state.
 * </p>
 */
public interface ValueStore {

    /*
     07/28/06 - POJOValueStore creation
     08/21/06 - refactor from implementing ValueStore to implementing Pluggable
     08/24/06 - move Pluggable.set() functionality to HashMap.put() - remove Pluggable interface
                                    - ATG performance updates refactor requires non-Map ValueStore interface
     09/04/06 - split POJO read/write functionality into subclasses
     11/28/06 - For custom implementations of ValueStore that utilize
                                             any of the helpers in HelperContext - be aware that
                                             a HelperContext instance must be passed in via
                                             custom constructor or the default static context will be used
     02/01/07 - add copy() function as part of SDOChangeSummary.undoChanges()
     */

    public Object getDeclaredProperty(int propertyIndex);

    public Object getOpenContentProperty(Property property);

    public void setDeclaredProperty(int propertyIndex, Object value);

    public void setOpenContentProperty(Property property, Object value);

    public void setManyProperty(Property propertyName, Object value);

    public boolean isSetDeclaredProperty(int propertyIndex);

    public boolean isSetOpenContentProperty(Property property);

    public void unsetDeclaredProperty(int propertyIndex);

    public void unsetOpenContentProperty(Property property);

    /**
     * Perform any post-instantiation integrity operations that could not be done during
     * ValueStore creation.<br>
     * Since the dataObject reference passed in may be bidirectional or self-referencing
     * - we cannot set this variable until the dataObject itself is finished instantiation
     * - hence the 2-step initialization.
     *
     * @param dataObject
     */
    public void initialize(DataObject dataObject);

    /**
     * Get a shallow copy of the original ValueStore.
     * Changes made to the copy must not impact the original ValueStore
     * @return ValueStore
     */
    public ValueStore copy();
}
