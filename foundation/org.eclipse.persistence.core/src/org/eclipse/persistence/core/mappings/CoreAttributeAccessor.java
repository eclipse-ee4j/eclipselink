/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.core.mappings;

public interface CoreAttributeAccessor {

    /**
     * Return the attribute value from the object.
     */
    public abstract Object getAttributeValueFromObject(Object object);

    public abstract boolean isInstanceVariableAttributeAccessor();

    /**
     * Set the attribute value into the object.
     */
    public void setAttributeValueInObject(Object object, Object value);


}
