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
//     Denise Smith - 2.4 - January 2012
package org.eclipse.persistence.testing.jaxb.unmarshaller.space;

import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TestObject {

    public String theString;
    @XmlAnyElement
    public List<Object> theAny;

    public boolean equals(Object theObject){
        if(theObject instanceof TestObject){
            if(theString == null){
                return ((TestObject)theObject).theString == null;
            }
            return theString.equals(((TestObject)theObject).theString);
        }
        return false;
    }
}
