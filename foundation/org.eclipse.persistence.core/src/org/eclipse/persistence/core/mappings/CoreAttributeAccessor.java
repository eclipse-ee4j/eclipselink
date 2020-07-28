/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.5 - initial implementation
package org.eclipse.persistence.core.mappings;

import org.eclipse.persistence.exceptions.DescriptorException;

/**
 * INTERNAL
 * A abstraction of attribute accessor capturing behavior common to all
 * persistence types.
 */
public interface CoreAttributeAccessor {

    /**
     * Return the class type of the attribute.
     */
    public Class getAttributeClass();

    /**
     * INTERNAL:
     * Return the attribute name.
     */
    public String getAttributeName();

    /**
     * Return the attribute value from the object.
     */
    public Object getAttributeValueFromObject(Object object);

    /**
     * Allow any initialization to be performed with the descriptor class.
     */
    public void initializeAttributes(Class descriptorClass) throws DescriptorException;

    public boolean isInstanceVariableAttributeAccessor();

    public boolean isMethodAttributeAccessor();

    /**
     * INTERNAL:
     * @return
     */
    public boolean isWriteOnly();

    /**
     * Set the attribute value into the object.
     */
    public void setAttributeValueInObject(Object object, Object value);

    /**
     * INTERNAL
     * @param aBoolean
     */
    public void setIsReadOnly(boolean aBoolean);

    public void setIsWriteOnly(boolean aBoolean);

}
