/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
//  - rbarkhouse - 19 July 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlidref.object;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlList;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class Whitespace {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(ToLowerAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    @XmlElement(required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object idref;

    @XmlList
    @XmlElement(required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREFS")
    protected List<Object> idrefs = new ArrayList<Object>();

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        try {
            Whitespace anObj = (Whitespace) obj;

            if ((null == id && null != anObj.id) || (null != id && null == anObj.id)) {
                return false;
            }
            if (null != id && !id.equals(anObj.id)) {
                return false;
            }
        } catch (ClassCastException e) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return this.id + "|" + (this.idref != null) + "|" + (this.idrefs.size() > 0);
    }

}
