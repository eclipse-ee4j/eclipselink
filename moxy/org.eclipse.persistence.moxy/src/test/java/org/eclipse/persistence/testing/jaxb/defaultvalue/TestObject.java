/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.defaultvalue;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TestObject {

    @XmlAttribute
    protected String stringA = "stringADefault";
    @XmlElement
    protected String stringB = "stringBDefault";
    @XmlAttribute
    protected MyEnum enumA = MyEnum.ONE;
    @XmlElement
    protected MyEnum enumB = MyEnum.TWO;

    public boolean equals(Object compareObject){
        if(compareObject instanceof TestObject){
            TestObject testObjectCompare = (TestObject)compareObject;
            if(!enumA.equals(testObjectCompare.enumA)){
                return false;
            }
            if(!enumB.equals(testObjectCompare.enumB)){
                return false;
            }
            if(!stringA.equals(testObjectCompare.stringA)){
                return false;
            }
            if(!stringB.equals(testObjectCompare.stringB)){
                return false;
            }
            return true;
        }
        return false;
    }

}
