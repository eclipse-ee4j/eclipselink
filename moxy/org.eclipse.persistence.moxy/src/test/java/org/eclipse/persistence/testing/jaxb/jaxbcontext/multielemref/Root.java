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
//  - rbarkhouse - 08 March 2013 - 2.4.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.jaxbcontext.multielemref;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

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
