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

/**
 * INTERNAL:
 * This interface defines functionality required by ValueHolders for OneToOneMappings that
 * have LAZY access weaved into them and use Property (method) based access
 *
 * The weaving feature adds a paralell valueholder to the class it weaves and uses that valueholder
 * to control the lazy loading.  The methods on this interface provide information about how that weaved
 * valueholder is related to the underlying value.
 * @author tware
 *
 */
public interface WeavedAttributeValueHolderInterface<T> extends ValueHolderInterface<T> {

    /**
     * When a valueholder is triggered, the weaved code will ensure its value is
     * coordinated with the underlying property.  This method allows TopLink to determine
     * if that has happened.
     * @return
     */
    boolean isCoordinatedWithProperty();

    /**
     * TopLink will call this method when the triggering of a weaved valueholder causes it's
     * value to be coordinated with the underlying property
     */
    void setIsCoordinatedWithProperty(boolean coordinated);

    /**
     * This method returns whether this valueholder has been newly instantiated by weaved code.
     * @return
     */
    boolean isNewlyWeavedValueHolder();

    /**
     * TopLink weaving calls this method on any valueholder it weaves into a class to indicate
     * that it is new and it's value should not be considered.  The method is also called when coordination
     * with the underlying value occurs to indicate the value can now be trusted.
     * @param isNew
     */
    void setIsNewlyWeavedValueHolder(boolean isNew);

    /**
     * INTERNAL:
     * Return if add/remove should trigger instantiation or avoid.
     * Current instantiation is avoided is using change tracking.
     */
    boolean shouldAllowInstantiationDeferral();

}
