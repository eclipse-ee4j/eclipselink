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
* mmacivor - September 09/2009 - 1.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.oxm.record;

import org.eclipse.persistence.internal.oxm.mappings.Field;

/**
 * INTERNAL:
 * <p><b>Purpose:</b> Used to hold the value returned from a DOMRecord and the field with which it
 * was associated. Returned from calls to DOMRecord that take a List of fields as a parameter.
 * <p> 
 * @author mmacivor
 *
 */
public class XMLEntry {
    private Object value;
    private Field xmlField;
    
    public Object getValue() {
        return value;
    }
    
    public Field getXMLField() {
        return xmlField;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }
    
    public void setXMLField(Field xmlField) {
        this.xmlField = xmlField;
    }

}
