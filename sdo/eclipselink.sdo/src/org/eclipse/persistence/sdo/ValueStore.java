/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
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
    /**
     * Get declared property by index.<br>
     * Returns the value of the given property of this object.<br>
     * When the returned object is wrapped within an SDODataObject,
     * The SDODataObject wrapper is always the same object instance for a particular property.
     * <p>
     * If the property is {@link Property#isMany many-valued},
     * the result will be a {@link org.eclipse.persistence.sdo.helper.ListWrapper}
     * and each object in the List will be {@link Type#isInstance an instance of}
     * the property's {@link Property#getType type}.
     * Otherwise the result will directly be an instance of the property's type.
     * @param propertyIndex the property index of the value to fetch.
     * @return the value of the given property of the object.
     * @see #setDeclaredProperty(Property, Object)
     * @see #unsetDeclaredProperty(Property)
     * @see #isSetDeclaredProperty(Property)
     */
    public Object getDeclaredProperty(int propertyIndex);

    /**
     * Get open-content property by name.<br>
     * Returns the value of the given property of this object.<br>
     * When the returned object is wrapped within an SDODataObject,
     * The SDODataObject wrapper is always the same object instance for a particular property.
     * <p>
     * If the property is {@link Property#isMany many-valued},
     * the result will be a {@link org.eclipse.persistence.sdo.helper.ListWrapper}
     * and each object in the List will be {@link Type#isInstance an instance of}
     * the property's {@link Property#getType type}.
     * Otherwise the result will directly be an instance of the property's type.
     * @param propertyName the property name of the value to fetch.
     * @return the value of the given property of the object.
     * @see #setOpenContentProperty(Property, Object)
     * @see #unsetOpenContentProperty(Property)
     * @see #isSetOpenContentProperty(Property)
     */
    public Object getOpenContentProperty(String propertyName);

    /**
     * Set declared property by index.<br>
     * Sets the value of the given property of the object to the new value.
     * <p>
     * If the property is {@link Property#isMany many-valued},
     * the new value must be a {@link java.util.List}
     * and each object in that list must be {@link Type#isInstance an instance of}
     * the property's {@link Property#getType type};
     * the existing contents are cleared and the contents of the new value are added.
     * Otherwise the new value directly must be an instance of the property's type
     * and it becomes the new value of the property of the object.
     * @param propertyIndex the property name of the value to set.
     * @param value the new value for the property.
     * @see #unsetDeclaredProperty(Property)
     * @see #isSetDeclaredProperty(Property)
     * @see #getDeclaredProperty(Property)
     */
    public void setDeclaredProperty(int propertyIndex, Object value);

    /**
     * Set open-content property by name.<br>
     * Sets the value of the given property of the object to the new value.
     * <p>
     * If the property is {@link Property#isMany many-valued},
     * the new value must be a {@link java.util.List}
     * and each object in that list must be {@link Type#isInstance an instance of}
     * the property's {@link Property#getType type};
     * the existing contents are cleared and the contents of the new value are added.
     * Otherwise the new value directly must be an instance of the property's type
     * and it becomes the new value of the property of the object.
     * @param propertyName the property name of the value to set.
     * @param value the new value for the property.
     * @see #unsetOpenContentProperty(Property)
     * @see #isSetOpenContentProperty(Property)
     * @see #getOpenContentProperty(Property)
     */
    public void setOpenContentProperty(String propertyName, Object value);

    /**
     * Set 1-n many-valued property by name.<br>
     * This function is implemented by ValueStores that maintain separate wrapped-caching
     * and storage of property values.
     * @param propertyName
     * @param value
     */
    public void setManyProperty(Property propertyName, Object value);

    /**
     * Get isSet boolean status for declared property by index.<br>
     * Returns whether the property of the object is considered to be set.
     * <p>
     * isSet() for many-valued Properties returns true if the List is not empty and
     * false if the List is empty.  For single-valued Properties:
     * <ul><li>If the Property has not been set() or has been unset() then isSet() returns false.</li>
     * <li>If the current value is not the Property's default or null, isSet() returns true.</li>
     * <li>For the remaining cases the implementation may decide between two policies: </li>
     * <ol><li>any call to set() without a call to unset() will cause isSet() to return true, or </li>
     *   <li>the current value is compared to the default value and isSet() returns true when they differ.</li>
     * </ol></ul><p>
     * @param propertyIndex the property index in question.
     * @return whether the property of the object is set.
     * @see #setDeclaredProperty(String, Object)
     * @see #unsetDeclaredProperty(String)
     * @see #getDeclaredProperty(String)
     */
    public boolean isSetDeclaredProperty(int propertyIndex);

    /**
     * Get isSet boolean status for open-content property by name.<br>
     * Returns whether the property of the object is considered to be set.
     * <p>
     * isSet() for many-valued Properties returns true if the List is not empty and
     * false if the List is empty.  For single-valued Properties:
     * <ul><li>If the Property has not been set() or has been unset() then isSet() returns false.</li>
     * <li>If the current value is not the Property's default or null, isSet() returns true.</li>
     * <li>For the remaining cases the implementation may decide between two policies: </li>
     * <ol><li>any call to set() without a call to unset() will cause isSet() to return true, or </li>
     *   <li>the current value is compared to the default value and isSet() returns true when they differ.</li>
     * </ol></ul><p>
     * @param propertyName the property in question.
     * @return whether the property of the object is set.
     * @see #setOpenContentProperty(String, Object)
     * @see #unsetOpenContentProperty(String)
     * @see #getOpenContentProperty(String)
     */
    public boolean isSetOpenContentProperty(String propertyName);

    /**
     * Unset declared property by index position
     * <p>
     * If the property is {@link Property#isMany many-valued},
     * the value must be an {@link java.util.List}
     * and that list is cleared.
     * Otherwise, the value of the property of the object
     * is set to the property's {@link Property#getDefault default value}.
     * The property will no longer be considered {@link #isSet set}.
     * @see #isSetDeclaredProperty(int)
     * @see #setDeclaredProperty(int, Object)
     * @see #getDeclaredProperty(int)
     * @param propertyIndex
     * @return void
     */
    public void unsetDeclaredProperty(int propertyIndex);

    /**
     * Unset open-content property by name
     * <p>
     * If the property is {@link Property#isMany many-valued},
     * the value must be an {@link java.util.List}
     * and that list is cleared.
     * Otherwise, the value of the property of the object
     * is set to the property's {@link Property#getDefault default value}.
     * The property will no longer be considered {@link #isSet set}.
     * @see #isSetDeclaredProperty(int)
     * @see #setDeclaredProperty(int, Object)
     * @see #getDeclaredProperty(int)
     * @param propertyName
     * @return void
     */
    public void unsetOpenContentProperty(String propertyName);

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