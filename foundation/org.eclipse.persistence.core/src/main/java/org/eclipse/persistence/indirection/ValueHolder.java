/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.indirection;

import java.io.Serializable;

/**
 * <p>
 * <b>Purpose</b>: Act as a place holder for a variable that required a value holder interface.
 * This class should be used to initialze an objects attributes that are using indirection is their mappings.
 */
public class ValueHolder<T> implements WeavedAttributeValueHolderInterface<T>, Cloneable, Serializable {

    /**
     * Stores the wrapped object.
     */
    protected T value;

    /**
     * The two variable below are used as part of the implementation of WeavedAttributeValueHolderInterface
     * They are used to track whether a valueholder that has been weaved into a class is coordinated
     * with the underlying property
     */
    // Set internally in TopLink when the state of coordination between a weaved valueholder and the underlying property is known
    private boolean isCoordinatedWithProperty = false;
    // Used to determine if this ValueHolder was added instantiated as part of the constructor of a weaved class
    private boolean isNewlyWeavedValueHolder = false;

    /**
     * PUBLIC:
     * Initialize the holder.
     */
    public ValueHolder() {
        super();
    }

    /**
     * PUBLIC:
     * Initialize the holder with an object.
     */
    public ValueHolder(T value) {
        this.value = value;
    }

    /**
     * INTERNAL:
     */
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException exception) {
            
        }

        return null;
    }

    /**
     * PUBLIC:
     * Return the wrapped object.
     */
    @Override
    public T getValue() {
        return value;
    }

    /**
     * Used as part of the implementation of WeavedAttributeValueHolderInterface
     * Used to track whether a valueholder that has been weaved into a class is coordinated
     * with the underlying property
     */
    @Override
    public boolean isCoordinatedWithProperty(){
        return isCoordinatedWithProperty;
    }

    /**
     * Used as part of the implementation of WeavedAttributeValueHolderInterface
     * Used to determine if this ValueHolder was added instantiated as part of
     * the constructor of a weaved class
     */
    @Override
    public boolean isNewlyWeavedValueHolder(){
        return isNewlyWeavedValueHolder;
    }

    /**
     * PUBLIC:
     * Return a boolean indicating whether the
     * wrapped object has been set or not.
     */
    @Override
    public boolean isInstantiated() {
        // Always return true since we consider
        // null to be a valid wrapped object.
        return true;
    }

    /**
     * Used as part of the implementation of WeavedAttributeValueHolderInterface
     * Used to track whether a valueholder that has been weaved into a class is coordinated
     * with the underlying property
     *
     * This method will be called internall when the state of Coordination between the
     * weaved valueholder and the underlying value is known
     */
    @Override
    public void setIsCoordinatedWithProperty(boolean coordinated){
        this.isCoordinatedWithProperty = coordinated;
        // this is not a newly weaved valueholder any more since we have done some coordination work
        isNewlyWeavedValueHolder = false;
    }

    /**
     * Used as part of the implementation of WeavedAttributeValueHolderInterface
     * Used to determine if this ValueHolder was added instantiated as part of
     * the constructor of a weaved class
     *
     * This method will be called when a ValueHolder is instantiated in a weaved class
     */
    @Override
    public void setIsNewlyWeavedValueHolder(boolean isNew){
        this.isNewlyWeavedValueHolder = isNew;
    }

    /**
     * PUBLIC:
     * Set the wrapped object.
     */
    @Override
    public void setValue(T value) {
        this.value = value;
    }

    /**
     * INTERNAL:
     * Return if add/remove should trigger instantiation or avoid.
     * Current instantiation is avoided is using change tracking.
     */
    @Override
    public boolean shouldAllowInstantiationDeferral() {
        return false;
    }

    /**
     * INTERNAL:
     */
    @Override
    public String toString() {
        if (getValue() == null) {
            return "{" + null + "}";
        }
        return "{" + getValue().toString() + "}";
    }
}
