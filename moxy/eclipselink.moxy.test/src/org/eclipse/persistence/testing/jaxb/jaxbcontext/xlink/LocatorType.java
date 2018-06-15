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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "locatorType", propOrder = { "title" })
public class LocatorType {

    protected List<TitleEltType> title;
    @XmlAttribute(namespace = "myNamespace", required = true)
    protected TypeType type;
    @XmlAttribute(namespace = "myNamespace", required = true)
    protected String href;
    @XmlAttribute(namespace = "myNamespace")
    protected String role;
    @XmlAttribute(name = "title", namespace = "myNamespace")
    protected String otherTitle1;
    @XmlAttribute(namespace = "myNamespace")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String label;

    public List<TitleEltType> getTitle() {
        if (title == null) {
            title = new ArrayList<TitleEltType>();
        }
        return this.title;
    }

    public boolean isSetTitle() {
        return ((this.title != null) && (!this.title.isEmpty()));
    }

    public void unsetTitle() {
        this.title = null;
    }

    public TypeType getType() {
        if (type == null) {
            return TypeType.LOCATOR;
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

    public String getHref() {
        return href;
    }

    public void setHref(String value) {
        this.href = value;
    }

    public boolean isSetHref() {
        return (this.href != null);
    }

    public String getRole() {
        return role;
    }

    public void setRole(String value) {
        this.role = value;
    }

    public boolean isSetRole() {
        return (this.role != null);
    }

    public String getOtherTitle1() {
        return otherTitle1;
    }

    public void setOtherTitle1(String value) {
        this.otherTitle1 = value;
    }

    public boolean isSetOtherTitle1() {
        return (this.otherTitle1 != null);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String value) {
        this.label = value;
    }

    public boolean isSetLabel() {
        return (this.label != null);
    }

}
