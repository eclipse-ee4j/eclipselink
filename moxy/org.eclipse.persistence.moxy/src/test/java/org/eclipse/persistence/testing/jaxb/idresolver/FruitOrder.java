/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//  - rbarkhouse - 21 October 2011 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.idresolver;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FruitOrder {

    @XmlElement(name="box")
    List<Box> boxes = new ArrayList<Box>();

    @Override
    public String toString() {
        return boxes.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof FruitOrder)) {
            return false;
        }
        FruitOrder f = (FruitOrder) obj;

        return this.boxes.equals(f.boxes);
    }

}
