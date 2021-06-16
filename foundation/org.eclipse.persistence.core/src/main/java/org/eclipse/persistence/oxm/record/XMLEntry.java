/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
// mmacivor - September 09/2009 - 1.0 - Initial implementation
package org.eclipse.persistence.oxm.record;

import org.eclipse.persistence.internal.oxm.mappings.Field;

/**
 * INTERNAL:
 * <p><b>Purpose:</b> Used to hold the value returned from a DOMRecord and the field with which it
 * was associated. Returned from calls to DOMRecord that take a List of fields as a parameter.
 * <p></p>
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
