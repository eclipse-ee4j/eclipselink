/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 21 October 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.sun.idresolver;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
