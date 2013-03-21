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
* mmacivor - April 25/2008 - 1.0M8 - Initial implementation
* bdoughan - July 17/2009 - 2.0 - Refactored to extend JAXBElement
******************************************************************************/
package org.eclipse.persistence.internal.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

/**
 * INTERNAL:
 * <p><b>Purpose: </b>Provide an interface for asm generated wrapper classes to implement to enable the
 * unwrapping of the values after the unmarshal.
 * <p><b>Responsibilities:</b><ul>
 * <li>Provide an API to allow the unwrapping of the wrapped value</li>
 * </ul> 
 * @author mmacivor
 *
 */
public class WrappedValue extends JAXBElement {

    private boolean setValue;

    public WrappedValue(QName name, Class declaredType, Object value) {
        super(name, declaredType, value);
        this.setValue = false;
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value);
        setValue = true;
    }

    public boolean isSetValue() {
        return setValue;
    }

}