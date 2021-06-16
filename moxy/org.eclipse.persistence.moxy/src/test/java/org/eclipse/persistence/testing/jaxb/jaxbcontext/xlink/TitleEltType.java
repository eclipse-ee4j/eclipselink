/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
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
//  - rbarkhouse - 28 February 2013 - 2.4.2 - Initial implementation

package org.eclipse.persistence.testing.jaxb.jaxbcontext.xlink;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlMixed;
import jakarta.xml.bind.annotation.XmlType;
import org.w3c.dom.Element;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "titleEltType", propOrder = { "content" })
public class TitleEltType {

    @XmlMixed
    @XmlAnyElement(lax = true)
    protected List<Serializable> content;
    @XmlAttribute(namespace = "myNamespace", required = true)
    protected TypeType type;
    @XmlAttribute(namespace = "http://www.w3.org/XML/1998/namespace")
    protected String lang;

    public List<Serializable> getContent() {
        if (content == null) {
            content = new ArrayList<Serializable>();
        }
        return this.content;
    }

    public boolean isSetContent() {
        return ((this.content != null) && (!this.content.isEmpty()));
    }

    public void unsetContent() {
        this.content = null;
    }

    public TypeType getType() {
        if (type == null) {
            return TypeType.TITLE;
        } else {
            return type;
        }
    }

    public void setType(TypeType value) {
        this.type = value;
    }

    public boolean isSetType() {
        return (this.type != null);
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String value) {
        this.lang = value;
    }

    public boolean isSetLang() {
        return (this.lang != null);
    }

}
