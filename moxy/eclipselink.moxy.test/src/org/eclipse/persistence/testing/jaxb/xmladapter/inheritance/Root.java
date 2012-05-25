/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - -01 March 2012 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmladapter.inheritance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class Root {

    @XmlJavaTypeAdapter(FooAdapter.class)
    protected Foo foo;

    public boolean equals(Object obj) {
        if (obj instanceof Root) {
            Foo aFoo = ((Root) obj).foo;
            if (aFoo == null && this.foo == null) {
                return true;
            }
            if (aFoo != null && this.foo != null) {
                return true;
            }
        }
        return false;
    }

}
