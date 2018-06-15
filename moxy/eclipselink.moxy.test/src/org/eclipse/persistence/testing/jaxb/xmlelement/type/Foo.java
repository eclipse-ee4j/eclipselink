/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - May 2013
package org.eclipse.persistence.testing.jaxb.xmlelement.type;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Kohsuke Kawaguchi
 */
@XmlRootElement(name="foo")
class Foo {
    @XmlElement(type=BigDecimal.class)
    Object field;

    public boolean equals(Object obj){
        if(obj instanceof Foo){
            return (field == null && (((Foo)obj).field) == null) || ( field.equals(((Foo)obj).field));
        }
        return false;
    }
}
