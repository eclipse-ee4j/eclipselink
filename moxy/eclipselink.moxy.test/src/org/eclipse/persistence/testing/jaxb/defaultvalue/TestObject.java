/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.3.3 - initial implementation
 ******************************************************************************/
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