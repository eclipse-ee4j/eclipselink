/*******************************************************************************
 * Copyright (c) 2011, 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Matt MacIvor - July 4th 2011
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlmixed;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="root")
public class Root {
    @XmlAttribute
    private String attr;

    @XmlMixed
    private ArrayList<Object> objects;

    @XmlElement
    private String elem;


    public ArrayList<Object> getObjects() {
        return objects;
    }

    public void setObjects(ArrayList<Object> objects) {
        this.objects = objects;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String string) {
        this.attr = string;
    }

    public String getElem() {
        return elem;
    }

    public void setElem(String elem) {
        this.elem = elem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Root root = (Root) o;

        if (attr != null ? !attr.equals(root.attr) : root.attr != null) return false;
        if (elem != null ? !elem.equals(root.elem) : root.elem != null) return false;
        if (objects != null ? !objects.equals(root.objects) : root.objects != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = attr != null ? attr.hashCode() : 0;
        result = 31 * result + (objects != null ? objects.hashCode() : 0);
        result = 31 * result + (elem != null ? elem.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Root [attr=" + attr + ", objects=" + objects + ", elem=" + elem + "]";
    }

}
