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
//     Denise Smith - January 2014
package org.eclipse.persistence.testing.jaxb.xmladapter.direct;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class Root {
    @XmlSchemaType(name="integer")
    public String myString1;

    @XmlSchemaType(name="integer")
    @XmlJavaTypeAdapter(StringAdapter.class)
    public String myString2;

    @XmlSchemaType(name="date")
    @XmlJavaTypeAdapter(StringStringAdapter.class)
    public String myString3;

    public boolean equals(Object obj){
        if(obj instanceof Root){
            Root compareObj = (Root)obj;
            return  myString1.equals(compareObj.myString1)
                 && myString2.equals(compareObj.myString2)
                 && myString3.equals(compareObj.myString3);
        }
        return false;
    }
}
