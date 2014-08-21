/*******************************************************************************
 * Copyright (c) 1998, 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - April 10, 2013
 ******************************************************************************/ 
package org.eclipse.persistence.testing.jaxb.xmlvalue;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Root {

    @XmlElement(name = "thing", required = true)
    protected BytesHolderWithXmlId thing;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Root root = (Root) o;

        if (thing != null ? !thing.equals(root.thing) : root.thing != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return thing != null ? thing.hashCode() : 0;
    }
}
