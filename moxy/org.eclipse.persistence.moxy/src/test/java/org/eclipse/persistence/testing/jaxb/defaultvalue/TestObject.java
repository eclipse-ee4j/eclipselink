/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Denise Smith - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.defaultvalue;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
