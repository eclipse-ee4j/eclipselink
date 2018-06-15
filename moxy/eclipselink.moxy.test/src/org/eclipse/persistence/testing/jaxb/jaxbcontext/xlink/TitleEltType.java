/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 28 February 2013 - 2.4.2 - Initial implementation

package org.eclipse.persistence.testing.jaxb.jaxbcontext.xlink;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlType;
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
