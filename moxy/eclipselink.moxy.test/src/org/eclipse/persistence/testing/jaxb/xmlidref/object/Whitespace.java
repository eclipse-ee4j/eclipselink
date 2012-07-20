/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 19 July 2012 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlidref.object;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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