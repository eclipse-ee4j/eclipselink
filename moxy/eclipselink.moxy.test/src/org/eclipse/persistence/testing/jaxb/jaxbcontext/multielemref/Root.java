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
//  - rbarkhouse - 08 March 2013 - 2.4.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.jaxbcontext.multielemref;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class Root {

    @XmlElementRef
    public Parent thing;

    @XmlElementRef
    public List<Parent> things = new ArrayList<Parent>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Root root = (Root) o;

        if (thing != null ? !thing.equals(root.thing) : root.thing != null) return false;
        if (things != null ? !things.equals(root.things) : root.things != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = thing != null ? thing.hashCode() : 0;
        result = 31 * result + (things != null ? things.hashCode() : 0);
        return result;
    }
}
