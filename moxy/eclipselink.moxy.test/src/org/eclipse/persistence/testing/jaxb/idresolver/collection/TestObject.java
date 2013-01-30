/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 29 January 2013 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.idresolver.collection;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class TestObject {

    @XmlTransient
    public boolean processed = false;

    @XmlID
    public int id;

    public String name;

    @XmlIDREF
    public TestObject single;

    @XmlIDREF
    @XmlElement(name = "ref")
    public ArrayList<TestObject> refs = new ArrayList<TestObject>();

    @Override
    public String toString() {
        return "TestObject [processed=" + processed + ", id=" + id + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (processed ? 1231 : 1237);
        result = prime * result + ((refs == null) ? 0 : refs.hashCode());
        result = prime * result + ((single == null) ? 0 : single.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TestObject other = (TestObject) obj;
        if (id != other.id)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (processed != other.processed)
            return false;
        if (refs == null) {
            if (other.refs != null)
                return false;
        } else if (!refs.equals(other.refs))
            return false;
        if (single == null) {
            if (other.single != null)
                return false;
        } else if (!single.equals(other.single))
            return false;
        return true;
    }

}