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
 *     desmith - July 2012 
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlschematype;

import java.math.BigInteger;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Root {

    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger count;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Root root = (Root) o;

        if (count != null ? !count.equals(root.count) : root.count != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return count != null ? count.hashCode() : 0;
    }
}
