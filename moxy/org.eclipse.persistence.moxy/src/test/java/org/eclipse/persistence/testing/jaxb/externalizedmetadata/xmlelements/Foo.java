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
// dmccann - November 27/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelements;

import java.util.List;

public class Foo {
    //@jakarta.xml.bind.annotation.XmlElementWrapper(name="items")
    //@jakarta.xml.bind.annotation.XmlElements({
    //    @jakarta.xml.bind.annotation.XmlElement(name="A", type=Integer.class),
    //    @jakarta.xml.bind.annotation.XmlElement(name="B", type=Float.class)
    //})
    public List items;

    public boolean equals(Object compareObj){
        if(compareObj instanceof Foo){
            return items.equals(((Foo)compareObj).items);
        }
        return false;
    }
}
