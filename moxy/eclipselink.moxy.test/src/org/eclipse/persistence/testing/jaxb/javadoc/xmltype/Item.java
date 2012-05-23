/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.javadoc.xmltype;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Item {

    public String name;

    @XmlAttribute
    //@XmlPath("@price")
    public USPrice price;

    @Override
    public boolean equals(Object obj) {
        if (null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        Item test = (Item) obj;
        if (!equals(name, test.name)) {
            return false;
        }
        if (!equals(price, test.price)) {
            return false;
        }
        return true;
    }

    private boolean equals(Object control, Object test) {
        if (null == control) {
            return null == test;
        }
        return control.equals(test);
    }

}
