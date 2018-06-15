/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.defaultvalue;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "testObject")
public class TestXmlValueObject {

     @XmlValue
     protected String testString = "test";

     public boolean equals(Object compareObject){
        if(compareObject instanceof TestXmlValueObject){
            return testString.equals(((TestXmlValueObject)compareObject).testString);
        }
        return false;
     }

}
